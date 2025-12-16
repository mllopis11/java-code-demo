package mike.demo.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import mike.demo.file.parser.domain.RecordParserFactory;

class RecordParserTest implements FileStructureTestSupport {

    @Nested
    class CsvRecords {

        @Test
        void should_return_parsed_records() {

            var recordParser = RecordParserFactory.create(CSV_STRUCTURE);
            var listOfEecordValues = CSV_LINES_SAMPLE.stream().map(recordParser::parse).toList();

            assertThat(listOfEecordValues).hasSize(3);
        }
    }

    @Nested
    class FixedRecords {

        @Test
        void should_return_parsed_records() {

            FIXED_STRUCTURE.schema();

            var recordParser = RecordParserFactory.create(FIXED_STRUCTURE);
            var listOfEecordValues = FIXED_LINES_SAMPLE.stream().map(recordParser::parse).toList();

            assertThat(listOfEecordValues).hasSize(3);
        }
    }
}
