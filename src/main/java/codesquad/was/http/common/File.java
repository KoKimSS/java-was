package codesquad.was.http.common;

import java.io.FileOutputStream;
import java.io.IOException;

public class File {
    private String name;
    private String filename;
    private byte[] fileContent;

    public File(String name, String filename, byte[] fileContent) {
        this.name = name;
        this.filename = filename;
        this.fileContent = fileContent;
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public static void save(File file) {
        // Get the user's home directory
        String homeDirectory = System.getProperty("user.home");
        System.out.println("homeDirectory = " + homeDirectory);
        // Create the file object for the destination
        java.io.File destFile = new java.io.File(homeDirectory, file.getFilename());

        // Write the file content to the specified path
        try (FileOutputStream fos = new FileOutputStream(destFile)) {
            fos.write(file.getFileContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
