package codesquad.http.urlMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceGetter {
    private static final String ROOT_DIRECTORY = "src/main/resources"; // 웹 리소스가 있는 디렉토리 경로


    /**
     * 요청된 URL에 해당하는 리소스 파일 경로를 가져옵니다.
     *
     * @param url 요청된 URL
     * @return 리소스 파일 경로
     */
    private static String getResourcePath(String url) {
        String filePath = ROOT_DIRECTORY + url;
        Path path = Paths.get(filePath);
        if (Files.exists(path) && !Files.isDirectory(path)) {
            return filePath;
        }
        return null;
    }

    /**
     * 파일의 MIME 타입을 가져옵니다.
     *
     * @param filePath 파일 경로
     * @return MIME 타입 문자열
     */
    public static String getContentType(String filePath) {
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
     * @param filePath 파일 경로
     * @return 파일의 바이트 배열
     */
    public static byte[] getResourceBytes(String filePath) throws IOException {
        String resourcePath = getResourcePath(filePath);
        System.out.println(resourcePath);
        Path path = Paths.get(resourcePath);
        return Files.readAllBytes(path);
    }
}
