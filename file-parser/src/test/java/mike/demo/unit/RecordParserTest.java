package mike.demo.unit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mike.demo.record.RecordParserFactory;

class RecordParserTest implements RecordStructTestSupport {

    private static final Logger log = LoggerFactory.getLogger(RecordParserTest.class);

    @BeforeAll
    static void init() {
        log.debug("Show schema ...{}", REC_STRUCT.schema());
    }

    @Nested
    class CsvRecords {

        @Test
        void should_return_parsed_records() {

            var recordParser = RecordParserFactory.csv(REC_STRUCT, ",");
            var listOfEecordValues = CSV_LINES_SAMPLE.stream().map(recordParser::parse).toList();

            assertThat(listOfEecordValues).hasSize(3);
        }
    }

    @Nested
    class FixedRecords {

        @Test
        void should_return_parsed_records() {

            var recordParser = RecordParserFactory.fixed(REC_STRUCT);
            var listOfEecordValues = FIXED_LINES_SAMPLE.stream().map(recordParser::parse).toList();

            assertThat(listOfEecordValues).hasSize(3);
        }
    }
}
