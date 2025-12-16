package mike.demo.record.field;

import java.util.Optional;
import java.util.function.Predicate;

import mike.bootstrap.utilities.helpers.Strings;

public record FieldText(
        String name, int position, int offset, int length,
        Optional<String> defaultValue, Optional<Predicate<String>> accept) implements Field<String> {

    @Override
    public Class<String> type() {
        return String.class;
    }

    @Override
    public String format() {
        return Strings.EMPTY;
    }

    @Override
    public String valueOf(String rawValue) {
        var value = validate(rawValue);
        return this.accept(value);
    }
}
