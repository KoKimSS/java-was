package codesquad.was.util;


import codesquad.was.exception.NotFoundException;

import java.io.*;

public class ResourceGetter {

    /**
     * 파일의 MIME 타입을 가져옵니다.
     *
     * @param filePath 파일 경로
     * @return MIME 타입 문자열
     */
    public static String getContentTypeByPath(String filePath) {
        if (filePath.endsWith(".html")) {
            return "text/html";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript";
        } else if (filePath.endsWith(".jpg")) {
            return "image/jpeg";
        } else if (filePath.endsWith(".png")) {
            return "image/png";
        } else if (filePath.endsWith(".svg")) {
            return "image/svg+xml";
        } else if (filePath.endsWith(".ico")) {
            return "image/x-icon";
        } else {
            return "application/octet-stream"; // 기타 파일 형식의 기본 타입
        }
    }



    /**
     * 파일의 바이트 배열을 읽어옵니다.
     *
     * @param filePath 파일 경로Ï
     * @return 파일의 바이트 배열
     */
    public static byte[] getResourceBytesByPath(String filePath) throws IOException, NotFoundException {
        System.out.println("파일패스"+filePath);
        InputStream resourceAsStream = ResourceGetter.class.getResourceAsStream(filePath);
        if(resourceAsStream == null) {
            throw new NotFoundException("매핑되는 url이 없습니다");
        }
        byte[] bytes = resourceAsStream.readAllBytes();

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
