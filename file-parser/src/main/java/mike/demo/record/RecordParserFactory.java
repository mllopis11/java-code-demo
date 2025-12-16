package mike.demo.record;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import mike.bootstrap.utilities.helpers.Strings;

public class RecordParserFactory {

    private RecordParserFactory() {}

    public static RecordParser csv(RecordStruct structure) {
        return RecordParserFactory.csv(structure, null);
    }

    public static RecordParser csv(RecordStruct structure, String delimiter) {
        return new CsvRecordParser(structure, delimiter);
    }

    public static RecordParser fixed(RecordStruct structure) {
        return new FixedRecordParser(structure);
    }

    /**
     * A parser implementation for CSV (Comma-Separated Values) records.
     * <p>
     * This parser uses a specified {@link RecordStruct} to determine the expected fields
     * and a configurable delimiter (defaulting to ";") to split input lines.
     * Each line is parsed into a {@link RecordValues} object, with validation to ensure
     * the number of fields matches the structure definition.
     * </p>
     * 
     * <p>
     * This class is intended for internal use within {@link RecordParserFactory}.
     * </p>
     */
    private static class CsvRecordParser implements RecordParser {

        private final RecordStruct structure;
        private final String delimiter;

        private int lineCounter = 0;

        private CsvRecordParser(RecordStruct structure, String delimiter) {
            this.structure = structure;
            this.delimiter = Strings.blankAs(delimiter, ";");
        }

        @Override
        public RecordValues parse(String line) {

            lineCounter++;
            var fields = structure.fields();

            String[] values = line.split(delimiter);

            if (values.length != fields.size()) {
                throw new IllegalStateException(
                    "Invalid number of fields at line %d (expected: %d, got: %d)"
                        .formatted(lineCounter, fields.size(), values.length));
            }

            return new RecordValues(this.valuesOf(values));
        }

        private List<RecordValue> valuesOf(String[] values) {
            var idx = new AtomicInteger();

            return structure.fields().stream().map(field -> {
                var val = values[idx.getAndIncrement()];
                return new RecordValue(field.name(), field.valueOf(val));
            })
            .toList();
        }
    } 

    /**
     * A {@link RecordParser} implementation for parsing fixed-width record lines based on a {@link RecordStruct}.
     * <p>
     * This parser expects each input line to have a specific length as defined by the structure.
     * It extracts field values from the line using the offsets and lengths specified in the structure's fields.
     * </p>
     * <p>
     * If a line is shorter than the expected length, an {@link IllegalStateException} is thrown.
     * </p>
     */
    public static class FixedRecordParser implements RecordParser {

        private final RecordStruct structure;

        private int lineCounter = 0;

        private FixedRecordParser(RecordStruct structure) {
            this.structure = structure;
        }

        @Override
        public RecordValues parse(String line) {

            lineCounter++;
            var expectedLength = structure.length();
    
            if (line.length() < expectedLength) {
                throw new IllegalStateException(
                    "Invalid line size at line %d (expected: %d, got: %d)"
                        .formatted(lineCounter, expectedLength, line.length()));
            }

            return new RecordValues(this.valuesOf(line));
        }

        private List<RecordValue> valuesOf(String line) {

            return structure.fields().stream().map(f -> {
                var val = line.substring(f.offset(), f.offset() + f.length());
                return new RecordValue(f.name(), f.valueOf(val));
            })
            .toList();
        }
    }
}
