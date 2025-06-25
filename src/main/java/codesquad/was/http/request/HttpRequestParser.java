package codesquad.was.http.request;

import codesquad.was.http.common.HttpCookie;
import codesquad.was.http.common.Mime;
import codesquad.was.session.Manager;
import codesquad.was.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class HttpRequestParser {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    /**
     * InputStream에서 HTTP 요청 파싱 (기존 메서드 유지)
     */
    public static HttpRequest parseHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        return parseHttpRequest(reader);
    }

    /**
     * String에서 HTTP 요청 파싱 (NIO용 새로운 메서드)
     */
    public static HttpRequest parseHttpRequest(String requestData) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(requestData));
        return parseHttpRequest(reader);
    }

    /**
     * BufferedReader에서 HTTP 요청 파싱 (공통 로직)
     */
    private static HttpRequest parseHttpRequest(BufferedReader reader) throws IOException {
        HttpRequest request = new HttpRequest();

        // 첫 번째 줄 파싱 (Request Line)
        String requestLine = reader.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Invalid HTTP request: empty request line");
        }

        parseRequestLine(request, requestLine);

        // 헤더 파싱
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            parseHeader(request, line);
        }

        // Content-Type 설정
        List<String> contentType = request.getHeaders().getHeader("Content-Type");
        if (contentType != null) {
            String contentTypeStr = contentType.get(0);
            request.setContentType(Mime.fromString(contentTypeStr));
        }

        // 쿠키 파싱
        List<String> cookieHeader = request.getHeaders().getHeader("Cookie");
        if (cookieHeader != null) {
            String cookie = cookieHeader.get(0);
            if (cookie != null) {
                parseCookies(request, cookie);
            }
        }


        // 세션 처리
        processSession(request);

        // 바디 파싱 (POST 요청 등)
        List<String> contentLengthHeader = request.getHeaders().getHeader("Content-Length");
        if (contentLengthHeader != null) {
            String contentLength = contentLengthHeader.get(0);
            try {
                if (Integer.parseInt(contentLength) > 0) {
                    parseBody(request, reader, Integer.parseInt(contentLength));
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid Content-Length header: {}", contentLength);
            }
        }


        return request;
    }

    private static void parseRequestLine(HttpRequest request, String requestLine) throws IOException {
        String[] parts = requestLine.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }

        request.setMethod(parts[0]);
        request.setVersion(parts[2]);

        try {
            // URL이 절대 경로가 아닌 경우 http://localhost 추가
            String urlString = parts[1];
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                urlString = "http://localhost" + urlString;
            }
            request.setUrl(new URL(urlString));
        } catch (MalformedURLException e) {
            throw new IOException("Invalid URL: " + parts[1], e);
        }
    }

    private static void parseHeader(HttpRequest request, String line) {
        int colonIndex = line.indexOf(":");
        if (colonIndex > 0) {
            String key = line.substring(0, colonIndex).trim();
            String value = line.substring(colonIndex + 1).trim();
            request.addHeader(key, value);
        }
    }

    private static void parseCookies(HttpRequest request, String cookieHeader) {
        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] parts = cookie.trim().split("=", 2);
            if (parts.length == 2) {
                request.addCookie(parts[0], parts[1]);
            }
        }
    }

    private static void processSession(HttpRequest request) {
        String sessionId = request.getCookies().get(Session.sessionStr);
        Session session;

        if (sessionId != null) {
            session = Manager.findSession(sessionId);
            if (session == null) {
                session = new Session();
                String newSessionId = Session.createSessionId();
                Manager.addSession(newSessionId, session);

                HttpCookie sessionCookie = new HttpCookie(Session.sessionStr, newSessionId);
                // 쿠키를 요청에 추가하는 로직이 필요하다면 여기에 구현
            }
        } else {
            session = new Session();
            String newSessionId = Session.createSessionId();
            Manager.addSession(newSessionId, session);
        }

        request.setSession(session);
    }

    private static void parseBody(HttpRequest request, BufferedReader reader, int contentLength) throws IOException {
        char[] bodyChars = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int read = reader.read(bodyChars, totalRead, contentLength - totalRead);
            if (read == -1) {
                break;
            }
            totalRead += read;
        }

        String body = new String(bodyChars, 0, totalRead);
        request.setBody(body);

        // Content-Type에 따른 파라미터 파싱
        Mime contentType = request.getHeaders().getHeader("Content-Type") != null
                ? Mime.fromString(request.getHeaders().getHeader("Content-Type").get(0))
                : null;

        if (contentType == Mime.APPLICATION_X_WWW_FORM_URLENCODED) {
            parseFormParameters(request, body);
        } else if (contentType != null && contentType.toString().startsWith("multipart/form-data")) {
            parseMultipartFormData(request, body, contentType.toString());
        }
    }

    private static void parseFormParameters(HttpRequest request, String body) {
        if (body == null || body.isEmpty()) {
            return;
        }

        String[] paramPairs = body.split("&");
        for (String pair : paramPairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length >= 1) {
                try {
                    String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                    String value = keyValue.length > 1
                            ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)
                            : "";
                    request.addParameter(key, value);
                } catch (Exception e) {
                    logger.warn("Error decoding parameter: {}", pair);
                }
            }
        }
    }

    private static void parseMultipartFormData(HttpRequest request, String body, String contentType) {
        // boundary 추출
        String boundary = null;
        String[] contentTypeParts = contentType.split(";");
        for (String part : contentTypeParts) {
            part = part.trim();
            if (part.startsWith("boundary=")) {
                boundary = part.substring("boundary=".length());
                break;
            }
        }

        if (boundary == null) {
            logger.warn("No boundary found in multipart/form-data");
            return;
        }

        // 간단한 multipart 파싱 (실제로는 더 복잡한 구현이 필요)
        String[] parts = body.split("--" + boundary);
        for (String part : parts) {
            if (part.trim().isEmpty() || part.equals("--")) {
                continue;
            }

            String[] lines = part.split("\r\n");
            String name = null;
            String filename = null;
            boolean isFile = false;
            int dataStartIndex = 0;

            // 헤더 파싱
            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                if (line.isEmpty()) {
                    dataStartIndex = i + 1;
                    break;
                }

                if (line.startsWith("Content-Disposition:")) {
                    if (line.contains("name=\"")) {
                        int nameStart = line.indexOf("name=\"") + 6;
                        int nameEnd = line.indexOf("\"", nameStart);
                        if (nameEnd > nameStart) {
                            name = line.substring(nameStart, nameEnd);
                        }
                    }
                    if (line.contains("filename=\"")) {
                        int filenameStart = line.indexOf("filename=\"") + 10;
                        int filenameEnd = line.indexOf("\"", filenameStart);
                        if (filenameEnd > filenameStart) {
                            filename = line.substring(filenameStart, filenameEnd);
                            isFile = true;
                        }
                    }
                }
            }

            // 데이터 추출
            if (name != null && dataStartIndex < lines.length) {
                StringBuilder data = new StringBuilder();
                for (int i = dataStartIndex; i < lines.length; i++) {
                    if (i > dataStartIndex) {
                        data.append("\r\n");
                    }
                    data.append(lines[i]);
                }

                if (isFile) {
                    byte[] fileContent = data.toString().getBytes(StandardCharsets.UTF_8);
                    request.addFile(name, filename, fileContent);
                } else {
                    request.addParameter(name, data.toString());
                }
            }
        }
    }
}
