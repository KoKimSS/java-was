package codesquad.was.util;


import codesquad.was.exception.InternalServerException;
import codesquad.was.exception.NotFoundException;
import codesquad.was.http.common.Mime;

import java.io.*;

public class ResourceGetter {

    /**
     * 파일의 MIME 타입을 가져옵니다.
     *
     * @param filePath 파일 경로
     * @return MIME 타입 문자열
     */
    public static Mime getContentTypeByPath(String filePath) {
        if (filePath.endsWith(".html")) {
            return Mime.TEXT_HTML;
        } else if (filePath.endsWith(".css")) {
            return Mime.TEXT_CSS;
        } else if (filePath.endsWith(".js")) {
            return Mime.APPLICATION_JAVASCRIPT;
        } else if (filePath.endsWith(".jpg")) {
            return Mime.IMAGE_JPEG;
        } else if (filePath.endsWith(".png")) {
            return Mime.IMAGE_PNG;
        } else if (filePath.endsWith(".svg")) {
            return Mime.IMAGE_SVG;
        } else if (filePath.endsWith(".ico")) {
            return Mime.IMAGE_ICO;
        } else {
            return Mime.APPLICATION_OCTET_STREAM;
        }
    }



    /**
     * 파일의 바이트 배열을 읽어옵니다.
     *
     * @param filePath 파일 경로Ï
     * @return 파일의 바이트 배열
     */
    public static byte[] getResourceBytesByPath(String filePath){
        InputStream resourceAsStream = ResourceGetter.class.getResourceAsStream(filePath);
        if(resourceAsStream == null) {
            throw new NotFoundException("매핑되는 url이 없습니다");
        }
        byte[] bytes;

        try {
            bytes = resourceAsStream.readAllBytes();
        } catch (IOException e) {
            throw new InternalServerException("리소스 읽을 때 IOException 발생");
        }

        if(bytes == null) {
            throw new NotFoundException("매핑되는 url이 없습니다");
        }

        return bytes;
    }

    public static byte[] readBytesFromFile(File file) throws IOException {
        FileInputStream fis = null;
        byte[] fileBytes = null;

        try {
            fis = new FileInputStream(file);
            fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        return fileBytes;
    }
}
