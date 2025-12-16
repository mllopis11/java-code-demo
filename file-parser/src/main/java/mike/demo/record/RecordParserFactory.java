package mike.demo.record;

import java.util.concurrent.atomic.AtomicInteger;

import mike.bootstrap.utilities.helpers.Strings;
import mike.demo.record.field.Field;

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

    private static class CsvRecordParser implements RecordParser {

        private final RecordStruct fileStructure;
        private final String delimiter;

        private int lineCounter = 0;

        private CsvRecordParser(RecordStruct fileStructure, String delimiter) {
            this.fileStructure = fileStructure;
            this.delimiter = Strings.blankAs(delimiter, ";");
        }

        @Override
        public RecordValues parse(String line) {

            lineCounter++;
            var fields = fileStructure.fields();

            String[] values = line.split(delimiter);

            if (values.length != fields.size()) {
                throw new IllegalStateException(
                    "Invalid number of fields at line %d (expected: %d, got: %d)"
                        .formatted(lineCounter, fields.size(), values.length));
            }

            var idx = new AtomicInteger();

            var recordValues = fileStructure.fields().stream().map(field -> {
                var val = values[idx.getAndIncrement()];
                return new RecordValue(field.name(), field.valueOf(val));
            })
            .toList();

            return new RecordValues(recordValues);
        }
    } 

    public static class FixedRecordParser implements RecordParser {

        private final RecordStruct fileStructure;

        private int lineCounter = 0;

        private FixedRecordParser(RecordStruct fileStructure) {
            this.fileStructure = fileStructure;
        }

        @Override
        public RecordValues parse(String line) {

            lineCounter++;
            var fields = fileStructure.fields();
            var expectedLineSize = fields.stream().map(Field::length).reduce(0, Integer::sum);

            if (line.length() < expectedLineSize) {
                throw new IllegalStateException(
                    "Invalid line size at line %d (expected: %d, got: %d)"
                        .formatted(lineCounter, expectedLineSize, line.length()));
            }

            var recordValues = fileStructure.fields().stream().map(field -> {
                var val = line.substring(field.offset(), field.offset() + field.length());
                return new RecordValue(field.name(), field.valueOf(val));
            })
            .toList();

            return new RecordValues(recordValues);
        }
    }
}
