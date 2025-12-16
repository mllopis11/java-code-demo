package mike.demo.unit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import mike.demo.file.parser.domain.Field;
import mike.demo.file.parser.domain.FileType;

@TestMethodOrder(OrderAnnotation.class)
class FileStructureTest implements FileStructureTestSupport {

    @Test
    @Order(1)
    void assert_file_structure() {

        assertThat(CSV_STRUCTURE.fileType()).isEqualTo(FileType.CSV);
        assertThat(CSV_STRUCTURE.delimiter()).isEqualTo(";");
        assertThat(CSV_STRUCTURE.fields()).hasSize(5);

        assertThat(CSV_STRUCTURE.fields().get(0)).isNotNull().satisfies(field -> {
            assertThat(field.name()).isEqualTo("ID");
            assertThat(field.position()).isEqualTo(1);
            assertThat(field.type()).isEqualTo(Integer.class);
            assertThat(field.offset()).isZero();
            assertThat(field.length()).isEqualTo(8);
            assertThat(field.format()).isEmpty();
            assertThat(field.defaultValue()).isNotPresent();
            assertThat(field.required()).isTrue();
        });

        // assert field: NAME
        assertThat(CSV_STRUCTURE.fields().get(1)).isNotNull().satisfies(field -> {
            assertThat(field.name()).isEqualTo("NAME");
            assertThat(field.position()).isEqualTo(2);
            assertThat(field.type()).isEqualTo(String.class);
            assertThat(field.offset()).isEqualTo(8);
            assertThat(field.length()).isEqualTo(20);
            assertThat(field.format()).isEmpty();
            assertThat(field.defaultValue()).isNotPresent();
            assertThat(field.required()).isTrue();

            var rawValue = "John Doe            ";
            assertThat(field.valueOf(rawValue)).isEqualTo("John Doe");
        });

        // assert field: BIRTH_DATE
        assertThat(CSV_STRUCTURE.fields().get(2)).isNotNull().satisfies(field -> {
            assertThat(field.name()).isEqualTo("BIRTH_DATE");
            assertThat(field.position()).isEqualTo(3);
            assertThat(field.type()).isEqualTo(LocalDate.class);
            assertThat(field.offset()).isEqualTo(28);
            assertThat(field.length()).isEqualTo(8);
            assertThat(field.format()).isEqualTo("yyyyMMdd");
            assertThat(field.defaultValue()).isNotPresent();
            assertThat(field.required()).isTrue();
        });

        // assert field: GENDER
        assertThat(CSV_STRUCTURE.fields().get(3)).isNotNull().satisfies(field -> {
            assertThat(field.name()).isEqualTo("GENDER");
            assertThat(field.position()).isEqualTo(4);
            assertThat(field.type()).isEqualTo(String.class);
            assertThat(field.offset()).isEqualTo(36);
            assertThat(field.length()).isEqualTo(1);
            assertThat(field.format()).isEmpty();
            assertThat(field.defaultValue()).isNotPresent();
            assertThat(field.accept()).isPresent();
            assertThat(field.required()).isTrue();
        });

        // assert field: COUNTRY
        assertThat(CSV_STRUCTURE.fields().get(4)).isNotNull().satisfies(field -> {
            assertThat(field.name()).isEqualTo("COUNTRY");
            assertThat(field.position()).isEqualTo(5);
            assertThat(field.type()).isEqualTo(String.class);
            assertThat(field.offset()).isEqualTo(37);
            assertThat(field.length()).isEqualTo(3);
            assertThat(field.format()).isEmpty();
            assertThat(field.defaultValue()).isPresent().get().isEqualTo("");
            assertThat(field.required()).isFalse();
        });
    }

    @Nested
    class AssertFieldId {

        private final Field<?> field = CSV_STRUCTURE.fields().get(0);

        @Test
        void should_return_12345_when_value_is_12345_with_padding() {
            assertThat(field.valueOf("12345     ")).isEqualTo(12345);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "          ", "0         ", "A12345    ", "-12345    ", "100000000" })
        void should_throw_IllegalArgumentException_when_value_is(String rawValue) {
            assertThatIllegalArgumentException()
                .isThrownBy(() -> field.valueOf(rawValue));
        }
    }

    @Nested
    class AssertFieldBirthDate {

        private final Field<?> field = CSV_STRUCTURE.fields().get(2);

        @Test
        void should_return_birthdate_when_value_is_19820221() {
            assertThat(field.valueOf("19820221"))
                .isEqualTo(LocalDate.of(1982, 2, 21));
        }

        @ParameterizedTest
        @ValueSource(strings = { "20231301", "20A30201" })
        void should_throw_DataTimeParseException_when_date_is_invalid(String rawValue) {
            assertThatExceptionOfType(DateTimeParseException.class)
                .isThrownBy(() -> field.valueOf(rawValue));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "18991231", "20", "1982-02-21" })
        void should_throw_IllegalArgumentException_when_date_is(String rawValue) {
            assertThatIllegalArgumentException()
                .isThrownBy(() -> field.valueOf(rawValue));
        }

        @ParameterizedTest
        @CsvSource({ "20250230, 2025-02-28", "20250431, 2025-04-30" })
        void should_return_last_day_of_month_when_day_of_month_is_30_or_31(String rawValue, LocalDate expected) {
            assertThat(field.valueOf(rawValue)).isEqualTo(expected);
        }
    }

    @Nested
    class AssertFieldGender {
        private final Field<?> field = CSV_STRUCTURE.fields().get(3);

        @ParameterizedTest
        @ValueSource(strings = { "M", "F" })
        void should_return_value_when_value_is_M_or_F(String rawValue) {
            assertThat(field.valueOf(rawValue)).isEqualTo(rawValue);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "X", " ", "MALE" })
        void should_throw_IllegalArgumentException_when_value_is_invalid(String rawValue) {
            assertThatIllegalArgumentException()
                .isThrownBy(() -> field.valueOf(rawValue));
        }
    }

    @Nested
    class AssertFieldCountry {
        private final Field<?> field = CSV_STRUCTURE.fields().get(4);

        @Test
        void should_return_value_when_value_is_USA() {
            assertThat(field.valueOf("USA")).isEqualTo("USA");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "   " })
        void should_return_default_value_when_value_is_empty(String rawValue) {
            assertThat(field.valueOf(rawValue)).isEqualTo("");
        }

        @Test
        void should_throw_IllegalArgumentException_when_value_too_large() {
            assertThatIllegalArgumentException()
                .isThrownBy(() -> field.valueOf("FRANCE"));
        }
    }
}
