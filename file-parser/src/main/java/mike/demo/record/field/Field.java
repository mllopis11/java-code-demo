package mike.demo.record.field;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Predicate;

import mike.bootstrap.utilities.helpers.Strings;

public interface Field<T> {

    String name();
    
    int position();
    int offset();
    int length();
    String format();
    Optional<String> defaultValue();
    Optional<Predicate<T>> accept();

    Class<T> type();
    T valueOf(String rawValue);

    default boolean required() {
        return !this.defaultValue().isPresent();
    }

    default String schema() {
        return "%s (type: %s, offset: %d, length: %s, required: %s, default: '%s')".formatted(
            this.name(), this.type().getSimpleName(), this.offset(), this.length(),
            this.required(), this.defaultValue().orElse(null));
    }

    default String validate(String rawValue) {

        var value = Strings.blankAs(rawValue).trim();

        if (this.defaultValue().isPresent() && value.length() == 0) {
            value = this.defaultValue().get();
        }

        if (this.required() && value.length() == 0) {
            throw new IllegalArgumentException(
                "Field '%s' is required but no value was provided".formatted(name()));
        }

        if (value.length() > length() || type() == LocalDate.class && value.length() != length()) {
            throw new IllegalArgumentException(
                "Field '%s' exceed maximum length (expected %d but got %d)"
                    .formatted(name(), length(), rawValue.length()));
        }

        return value;
    }

    default T accept(T value) {
        this.accept().ifPresent(predicate -> {
            if (!predicate.test(value)) {
                throw new IllegalArgumentException(
                    "Field '%s' has invalid value: %s".formatted(name(), value));
            }
        });

        return value;
    }
}
