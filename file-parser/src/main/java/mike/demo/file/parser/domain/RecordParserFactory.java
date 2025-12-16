package mike.demo.file.parser.domain;

import java.util.concurrent.atomic.AtomicInteger;

public class RecordParserFactory {

    private RecordParserFactory() {}

    public static RecordParser create(FileStructure fileStructure) {
        return switch (fileStructure.fileType()) {
            case CSV -> new CsvRecordParser(fileStructure);
            case FIXED -> new FixedRecordParser(fileStructure);
        };
    }

    private static class CsvRecordParser implements RecordParser {

        private final FileStructure fileStructure;

        private int lineCounter = 0;

        public CsvRecordParser(FileStructure fileStructure) {
            this.fileStructure = fileStructure;
        }

        @Override
        public RecordValues parse(String line) {

            lineCounter++;
            var fields = fileStructure.fields();

            String[] values = line.split(fileStructure.delimiter());

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

        private final FileStructure fileStructure;

        private int lineCounter = 0;

        public FixedRecordParser(FileStructure fileStructure) {
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
