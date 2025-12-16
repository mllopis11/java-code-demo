package mike.demo.record;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import mike.bootstrap.utilities.helpers.Strings;
import mike.demo.record.field.Field;
import mike.demo.record.field.FieldDate;
import mike.demo.record.field.FieldNumber;
import mike.demo.record.field.FieldText;

public record RecordStruct(String name, List<Field<?>> fields) {

    public String schema() {
        var separator = System.lineSeparator();

        var struct = fields.stream()
            .map(f -> "- %s".formatted(f.schema()))
            .collect(Collectors.joining(separator));

        return new StringBuilder(separator)
                    .append("schema: ").append(name)
                    .append(separator).append(struct)
                    .toString();
    }

    public static StructureBuilder name(String name) {
        return new StructureBuilder(name);
    }

    public static class StructureBuilder {

        private final String name;
        private final List<Field<?>> fields = new ArrayList<>();
       
        private int position = 1;
        private int offset = 0;

        private StructureBuilder(String name) {
            this.name = Strings.blankAs(name, "unknown");
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

        public RecordStruct build() {
            return new RecordStruct(this.name, this.fields);
        }
    }
}
