package codesquad.was.http.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class File {
    private String name;
    private String filename;
    private byte[] fileContent;

    public static final String homeDirectory = System.getProperty("user.home");

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

    public void setFileName(String filename) {
        this.filename = filename;
    }

    public byte[] getFileContent() {
        return fileContent;
    }

    public static void save(File file) {
        // Get the user's home directory
        java.io.File destFile = new java.io.File(homeDirectory, file.getFilename());

        // Write the file content to the specified path
        try (FileOutputStream fos = new FileOutputStream(destFile)) {
            fos.write(file.getFileContent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // a.png 파일

    public static byte[] read(String filepath) throws FileNotFoundException {
        System.out.println("파일패스"+filepath);
        java.io.File sourceFile = new java.io.File(homeDirectory,filepath);
        byte[] fileContent = null;

        try (FileInputStream fis = new FileInputStream(sourceFile)) {
            fileContent = new byte[(int) sourceFile.length()];
            fis.read(fileContent);
        } catch (IOException e) {
            throw new FileNotFoundException();
        }

        return fileContent;
    }
}
