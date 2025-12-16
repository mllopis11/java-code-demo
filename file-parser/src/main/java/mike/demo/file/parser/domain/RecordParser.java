package mike.demo.file.parser.domain;

public interface RecordParser {
    
    RecordValues parse(String line);
}
