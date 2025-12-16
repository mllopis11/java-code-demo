package mike.demo.file.parser.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mike.bootstrap.utilities.helpers.Strings;

public record FileStructure(FileType fileType, String delimiter, List<Field<?>> fields) {

    private static final Logger log = LoggerFactory.getLogger(FileStructure.class);

    public void schema() {
        var struct = fields.stream()
            .map(f -> "%s (type: %s, offset: %d, length: %s, default: '%s', required: %s)\n".formatted( 
                f.name(), f.type(), f.offset(), f.length(), f.defaultValue(), f.required()))
            .toList();

        log.info("Show schema:\n {}", struct);
    }

    public static StructureBuilder csv() {
        return FileStructure.csv(null);
    }

    public static StructureBuilder csv(String delimiter) {
        return new StructureBuilder(FileType.CSV, delimiter);
    }

    public static StructureBuilder fixed() {
        return new StructureBuilder(FileType.FIXED, null);
    }

    public static class StructureBuilder {

        private final FileType fileType;
        private final List<Field<?>> fields = new ArrayList<>();
        private final String delimiter;

        private int position = 1;
        private int offset = 0;

        private StructureBuilder(FileType fileType, String delimiter) {
            this.fileType = fileType;
            this.delimiter = Strings.blankAs(delimiter, fileType.delimiter());
        }

        // Text fields
        public StructureBuilder addTextField(String name, int length) {
            return this.addTextField(name, length, null, null);
        }

        public StructureBuilder addTextField(String name, int length, String defaultValue) {
            return this.addTextField(name, length, defaultValue, null);
        }

        public StructureBuilder addTextField(String name, int length, Predicate<String> accept) {
            return this.addTextField(name, length, null, accept);
        }

        public StructureBuilder addTextField(String name, int length, String defaultValue, Predicate<String> accept) {
            this.fields.add(new FieldText(name, position++, offset, length, Optional.ofNullable(defaultValue), Optional.ofNullable(accept)));
            this.offset += length;
            return this;
        }

        // Number fields
        public StructureBuilder addNumberField(String name, int length) {
            return this.addNumberField(name, length, null, null);
        }

        public StructureBuilder addNumberField(String name, int length, String defaultValue) {
            return this.addNumberField(name, length, defaultValue, null);
        }

        public StructureBuilder addNumberField(String name, int length, Predicate<Integer> accept) {
            return this.addNumberField(name, length, null, accept);
        }

        public StructureBuilder addNumberField(String name, int length, String defaultValue, Predicate<Integer> accept) {
            this.fields.add(new FieldNumber(name, position++, offset, length, Optional.ofNullable(defaultValue), Optional.ofNullable(accept)));
            this.offset += length;
            return this;
        }

        // Date fields
        public StructureBuilder addDateField(String name, String format) {
            return this.addDateField(name, format, null, null);
        }

        public StructureBuilder addDateField(String name, String format, String defaultValue) {
            return this.addDateField(name, format, defaultValue, null);
        }

        public StructureBuilder addDateField(String name, String format, Predicate<LocalDate> accept) {
            return this.addDateField(name, format, null, accept);
        }
        
        public StructureBuilder addDateField(String name, String format, String defaultValue, Predicate<LocalDate> accept) {
            this.fields.add(new FieldDate(name, position++, offset, format, Optional.ofNullable(defaultValue), Optional.ofNullable(accept)));
            this.offset += format.length();
            return this;
        }

        public FileStructure build() {
            return new FileStructure(this.fileType, delimiter, this.fields);
        }
    }
}
