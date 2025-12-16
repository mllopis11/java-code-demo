package mike.demo.record.field;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Predicate;

public record FieldDate(
        String name, int position, int offset, String format,
        Optional<String> defaultValue, Optional<Predicate<LocalDate>> accept) implements Field<LocalDate> {

    @Override
    public int length() {
        return format.length();
    }

    @Override
    public Class<LocalDate> type() {
        return LocalDate.class;
    }

    @Override
    public LocalDate valueOf(String rawValue) {
        var value = this.validate(rawValue);
        var formatter = DateTimeFormatter.ofPattern(format);
        var dateValue = LocalDate.parse(value, formatter);
        return this.accept(dateValue);
    }
}
