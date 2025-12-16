package mike.demo.file.parser.domain;

public enum FileType {
    
    CSV(";"),
    FIXED("");

    private final String delimiter;

    private FileType(String delimiter) {
        this.delimiter = delimiter;
    }

    public String delimiter() {
        return this.delimiter;
    }
}
