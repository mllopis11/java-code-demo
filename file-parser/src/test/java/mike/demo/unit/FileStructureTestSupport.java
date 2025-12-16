package mike.demo.unit;

import java.time.LocalDate;
import java.util.List;

import mike.bootstrap.utilities.helpers.Utils;
import mike.demo.file.parser.domain.FileStructure;
import mike.demo.file.parser.domain.FileStructure.StructureBuilder;

interface FileStructureTestSupport {

    static final List<String> CSV_LINES_SAMPLE = List.of(
            "12345,John Doe,19850515,M,USA",
            "7654,Jane Smith,19921230,F,CAN",
            "13579,Bob Johnson,19770707,M,GBR"
    );

    static final List<String> FIXED_LINES_SAMPLE = List.of(
            "12345   John Doe            19850515MUSA",
            "7654    Jane Smith          19921230FCAN",
            "13579   Bob Johnson         19770707MGBR"
    );

    static final FileStructure CSV_STRUCTURE = buildFileStructure(FileStructure.csv(","));
    static final FileStructure FIXED_STRUCTURE = buildFileStructure(FileStructure.fixed());

    private static FileStructure buildFileStructure(final StructureBuilder builder) {
        return builder
                .addNumberField("ID", 8, n -> Utils.between(n, 1, 99999999))
                .addTextField("NAME", 20)
                .addDateField("BIRTH_DATE", "yyyyMMdd", d -> d.isAfter(LocalDate.of(1900, 1, 1)))
                .addTextField("GENDER", 1, s -> s.equals("M") || s.equals("F"))
                .addTextField("COUNTRY", 3, "")
                .build();
    }
}
