package mike.demo.file.parser.domain;

import java.util.Optional;
import java.util.function.Predicate;

import mike.bootstrap.utilities.helpers.Strings;

record FieldNumber(
        String name, int position, int offset, int length,
        Optional<String> defaultValue, Optional<Predicate<Integer>> accept) implements Field<Integer> {

    @Override
    public String format() {
        return Strings.EMPTY;
    }

    @Override
    public Class<Integer> type() {
        return Integer.class;
    }

    @Override
    public Integer valueOf(String rawValue) {
        var value = validate(rawValue);
        return this.accept(Integer.valueOf(value));
    }
}
