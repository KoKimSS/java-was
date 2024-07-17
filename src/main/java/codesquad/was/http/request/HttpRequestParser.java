package codesquad.was.http.request;

import codesquad.was.http.common.HttpHeaders;
import codesquad.was.http.common.Mime;
import codesquad.was.session.Manager;
import codesquad.was.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
        HttpRequest request = new HttpRequest();

        // Parse request line
        String requestLine = readLine(inputStream);
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
        while (!(headerLine = readLine(inputStream)).isEmpty()) {
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
        parseBody(request, inputStream);

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

    private static void parseBody(HttpRequest request, InputStream inputStream) throws IOException {
        String contentType = null;
        HttpHeaders headers = request.getHeaders();
        List<String> contentTypeList = headers.getHeader("Content-Type");

        if (contentTypeList != null) {
            contentType = contentTypeList.get(0);
        }

        // Handle multipart/form-data
        if (contentType != null && contentType.startsWith("multipart/form-data")) {
            String boundary = extractBoundary(contentType);
            if (boundary != null) {
                parseMultipartData(request, inputStream, boundary);
                return;
            }
        }

        if (contentTypeList != null) {
            contentType = contentTypeList.get(0);
            request.setContentType(Mime.fromString(contentType));
        }

        ByteArrayOutputStream body = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            body.write(buffer, 0, read);
        }

        logger.info("Request body: {}", body);

        String bodyStr = URLDecoder.decode(body.toString(StandardCharsets.UTF_8.name()), StandardCharsets.UTF_8.name());

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

    private static void parseMultipartData(HttpRequest request, InputStream inputStream, String boundary) throws IOException {
        String boundaryLine = "--" + boundary;
        String endBoundaryLine = boundaryLine + "--";
        boolean inPart = false;
        boolean isHeader = true;
        Map<String, String> partHeaders = new HashMap<>();
        ByteArrayOutputStream partData = new ByteArrayOutputStream();
        StringBuilder currentPart = new StringBuilder();

        int nextByte;

        while ((nextByte = inputStream.read()) != -1) {
            currentPart.append((char) nextByte);
            if (currentPart.toString().endsWith(boundaryLine)) {
                if (inPart) {
                    processPart(request, partHeaders, partData.toByteArray());
                    partHeaders.clear();
                    partData.reset();
                }
                inPart = true;
                isHeader = true;
                currentPart.setLength(0);
            } else if (currentPart.toString().endsWith(endBoundaryLine)) {
                if (inPart) {
                    processPart(request, partHeaders, partData.toByteArray());
                }
                break;
            } else if (inPart) {
                if (isHeader) {
                    if (currentPart.toString().endsWith("\r\n\r\n")) {
                        isHeader = false; // End of headers, start of body
                        currentPart.setLength(0);
                    } else if (currentPart.toString().endsWith("\r\n")) {
                        String headerLine = currentPart.toString().trim();
                        int colonIndex = headerLine.indexOf(':');
                        if (colonIndex != -1) {
                            String headerName = headerLine.substring(0, colonIndex).trim();
                            String headerValue = headerLine.substring(colonIndex + 1).trim();
                            partHeaders.put(headerName, headerValue);
                        }
                        currentPart.setLength(0);
                    }
                } else {
                    partData.write(nextByte);
                }
            }
        }
    }

    private static void processPart(HttpRequest request, Map<String, String> headers, byte[] data) throws IOException {
        String contentDisposition = headers.get("Content-Disposition");
        if (contentDisposition != null) {
            Map<String, String> dispositionParams = parseContentDisposition(contentDisposition);
            String name = dispositionParams.get("name");
            String filename = dispositionParams.get("filename");

            if (filename != null) {
                request.addFile(name, filename, data);
            } else {
                String value = new String(data, StandardCharsets.UTF_8).trim();
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

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nextByte;
        while ((nextByte = inputStream.read()) != -1) {
            if (nextByte == '\r') {
                nextByte = inputStream.read(); // read '\n'
                if (nextByte == '\n') {
                    break;
                }
                buffer.write('\r');
            }
            buffer.write(nextByte);
        }
        return buffer.toString(StandardCharsets.UTF_8.name());
    }
}
