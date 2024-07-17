package codesquad.was.http.request;


import codesquad.was.http.common.HttpHeaders;
import codesquad.was.http.common.Mime;
import codesquad.was.session.Manager;
import codesquad.was.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;

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
            logger.info("Header: {}", headerLine);
            requestSb.append(headerLine).append("\n");
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
                addHeaderTo(headerParts, request);
            }
        }

        String host = request.getHeaders().getHeader("Host").get(0);
        String protocol = "http";
        URL url = new URL(protocol, host, path);
        request.setUrl(url);
        logger.info(requestSb.toString());
        // GET 이어도 body 를 갖을 수 있게 설정해 놓음
        parseBody(request, reader);

        return request;
    }

    private static void addHeaderTo(String[] headerParts, HttpRequest request) {
        request.addHeader(headerParts[0], headerParts[1]);
        if (headerParts[0].equalsIgnoreCase("Cookie")) {
            parseCookies(headerParts[1], request);
        }
    }

    private static void parseCookies(String cookieHeader, HttpRequest request) {
        String[] cookies = cookieHeader.split("; ");
        Arrays.stream(cookies).map(cookie -> cookie.split("=", 2)).filter(cookieParts -> cookieParts.length == 2).forEach(addCookieTo(request));
    }

    private static Consumer<String[]> addCookieTo(HttpRequest request) {
        return cookieParts -> {
            String key = cookieParts[0];
            String value = cookieParts[1];
            if (key.equals(sessionStr)) {
                Session session = Manager.findSession(value);
                if (session != null) {
                    request.addSession(session);
                }
            }
            request.addCookie(key, value);
        };
    }


    private static void parseBody(HttpRequest request, BufferedReader reader) throws IOException {
//         Parse body (if any)
        String contentType = null;
        HttpHeaders headers = request.getHeaders();
        Set<String> headerNames = headers.getHeaderNames();
        for (String headerName : headerNames) {
            System.out.println("헤더이름" + headerName);
        }
        List<String> contentTypeList = headers.getHeader("Content-Type");

        if (contentTypeList != null) {
            contentType = contentTypeList.get(0);
        }

        // Handle multipart/form-data
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            String boundary = extractBoundary(contentType);
            if (boundary != null) {
                parseMultipartData(request, reader, boundary);
                return;
            }
        }

        if (contentTypeList != null) {
            contentType = contentTypeList.get(0);
            request.setContentType(Mime.fromString(contentType));
        }

        StringBuilder body = new StringBuilder();

        while (reader.ready()) {
            body.append((char) reader.read());
        }

        logger.info("리퀘스트 바디{}", body);

        String bodyStr = URLDecoder.decode(body.toString(), StandardCharsets.UTF_8);

        if ("application/x-www-form-urlencoded".equals(contentType)) {

            request.parseParameters(bodyStr);
            return;
        }

        if (contentType == null || contentType.isEmpty()) {
            request.setBody(bodyStr);
        }
    }

    private static String extractBoundary(String contentType) {
        String[] params = contentType.split(";");
        for (String param : params) {
            param = param.trim();
            if (param.startsWith("boundary=")) {
                return param.substring("boundary=".length());
            }
        }
        return null;
    }

    // Parse the multipart data
    private static void parseMultipartData(HttpRequest request, BufferedReader reader, String boundary) throws IOException {
        String boundaryLine = "--" + boundary;
        String endBoundaryLine = boundaryLine + "--";
        String line;
        boolean inPart = false;
        boolean isHeader = true;
        Map<String, String> partHeaders = new HashMap<>();
        StringBuilder partData = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            logger.info("multipartLine: {}", line);

            if (line.equals(boundaryLine)) {
                if (inPart) {
                    processPart(request, partHeaders, partData.toString());
                    partHeaders.clear();
                    partData.setLength(0);
                }
                inPart = true;
                isHeader = true;
            } else if (line.equals(endBoundaryLine)) {
                if (inPart) {
                    processPart(request, partHeaders, partData.toString());
                }
                break;
            } else if (inPart) {
                if (isHeader) {
                    if (line.isEmpty()) {
                        isHeader = false; // End of headers, start of body
                    } else {
                        int colonIndex = line.indexOf(':');
                        if (colonIndex != -1) {
                            String headerName = line.substring(0, colonIndex).trim();
                            String headerValue = line.substring(colonIndex + 1).trim();
                            partHeaders.put(headerName, headerValue);
                        }
                    }
                } else {
                    partData.append(line).append("\r\n");
                }
            }
        }
    }

    private static void processPart(HttpRequest request, Map<String, String> headers, String data) throws IOException {
        String contentDisposition = headers.get("Content-Disposition");
        if (contentDisposition != null) {
            Map<String, String> dispositionParams = parseContentDisposition(contentDisposition);
            String name = dispositionParams.get("name");
            String filename = dispositionParams.get("filename");

            if (filename != null) {
                byte[] fileContent = data.getBytes(StandardCharsets.ISO_8859_1);
                request.addFile(name, filename, fileContent);
            } else {
                String value = data.trim();
                request.addParameter(name, value);
            }
        }
    }

    private static Map<String, String> parseContentDisposition(String contentDisposition) {
        Map<String, String> params = new HashMap<>();
        String[] parts = contentDisposition.split(";");
        for (String part : parts) {
            int equalsIndex = part.indexOf('=');
            if (equalsIndex != -1) {
                String name = part.substring(0, equalsIndex).trim();
                String value = part.substring(equalsIndex + 1).trim().replace("\"", "");
                params.put(name, value);
            }
        }
        return params;
    }
}
