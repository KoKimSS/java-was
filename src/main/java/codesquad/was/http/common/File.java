package codesquad.was.http.common;

public class File {
    private String name;
    private String filename;
    private byte[] fileContent;

    public File(String name, String filename, byte[] fileContent) {
        this.name = name;
        this.filename = filename;
        this.fileContent = fileContent;
    }
}
