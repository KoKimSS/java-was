package codesquad.was.request;


import codesquad.was.log.Log;
import codesquad.was.session.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;

import static codesquad.was.session.Session.sessionStr;

public class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public static HttpRequest parseHttpRequest(InputStream inputStream) throws IOException {
        StringBuilder requestSb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest();

        // Parse request line
        String requestLine = reader.readLine();
        requestSb.append("\n").append(requestLine).append("\n");
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request line");
        }
        String[] requestLineParts = requestLine.split(" ");
        if (requestLineParts.length != 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }
        String method = requestLineParts[0];
        request.setMethod(method);
        String path = requestLineParts[1];
        request.setVersion(requestLineParts[2]);

        // Parse headers
        String headerLine;

        while (!(headerLine = reader.readLine()).isEmpty()) {
            requestSb.append(headerLine).append("\n");
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
                request.addHeader(headerParts[0], headerParts[1]);
                if (headerParts[0].equalsIgnoreCase("Cookie")) {
                    parseCookies(headerParts[1], request);
                }

            }
        }

        String host = request.getHeaders().getHeader("Host").get(0);
        String protocol = "http";
        URL url = new URL(protocol, host, path);
        request.setUrl(url);
        logger.info(requestSb.toString());
        // GET 이어도 body 를 갖을 수 있게 설정해 놓음
        parseBody(request, reader);

        // sessionId가 존재하면 session 을 할당

        return request;
    }

    private static void parseCookies(String cookieHeader, HttpRequest request) {
        String[] cookies = cookieHeader.split("; ");
        Arrays.stream(cookies)
                .map(cookie -> cookie.split("=", 2))
                .filter(cookieParts -> cookieParts.length == 2)
                .forEach(cookieParts -> {
                    String key = cookieParts[0];
                    String value = cookieParts[1];
                    if (key.equals(sessionStr)) {
                        request.addSession(Manager.findSession(value));
                    }
                    request.addCookie(key, value);
                });
    }


    private static void parseBody(HttpRequest request, BufferedReader reader) throws IOException {
//         Parse body (if any)
        String contentType = null;
        List<String> contentTypeList = request.getHeaders().getHeader("Content-Type");
        if (contentTypeList != null) {
            contentType = contentTypeList.get(0);
            request.setContentType(contentType);
        }
        StringBuilder body = new StringBuilder();

        while (reader.ready()) {
            body.append((char) reader.read());
        }

        logger.info("리퀘스트 바디{}", body.toString());

        String bodyStr = URLDecoder.decode(body.toString(), "UTF-8");

        if ("application/x-www-form-urlencoded".equals(contentType)) {

            request.parseParameters(bodyStr);
            return;
        }

        if (contentType == null || contentType.isEmpty()) {
            request.setBody(bodyStr);
        }
//
//        // Parse body (if any)
//        String contentType = null;
//        List<String> contentTypeList = request.getHeaders().getHeader("Content-Type");
//        if (contentTypeList != null) {
//            contentType = contentTypeList.get(0);
//            request.setContentType(contentType);
//        }
//        StringBuilder body = new StringBuilder();
//
//        // UTF-8로 디코딩하며 본문 읽기
//        char[] buffer = new char[1024];
//        int numCharsRead;
//        while ((numCharsRead = reader.read(buffer)) != -1) {
//            body.append(buffer, 0, numCharsRead);
//        }
//
//        String bodyString = body.toString();
//
//        if ("application/x-www-form-urlencoded".equals(contentType)) {
//            request.parseParameters(bodyString);
//            return;
//        }
//
//        if (contentType == null || contentType.isEmpty()) {
//            request.setBody(bodyString);
//        }
//        logger.info(bodyString);
    }
}
