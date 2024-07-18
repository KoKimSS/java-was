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

    public static void parseBody(HttpRequest request, InputStream inputStream) throws IOException {
        List<String> header = request.getHeaders().getHeader("Content-Length");
        if (header == null) {
            return;
        }

        int contentLength = Integer.parseInt(header.get(0));

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
                parseMultipartData(request, inputStream, boundary, contentLength);
                return;
            }
        }

        if (contentTypeList != null) {
            contentType = contentTypeList.get(0);
            request.setContentType(Mime.fromString(contentType));
        }

        ByteArrayOutputStream body = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        int totalRead = 0;

        while (totalRead < contentLength && (read = inputStream.read(buffer, 0, Math.min(buffer.length, contentLength - totalRead))) != -1) {
            body.write(buffer, 0, read);
            totalRead += read;
        }

        String bodyStr = URLDecoder.decode(body.toString(StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        if ("application/x-www-form-urlencoded".equals(contentType)) {
            request.parseParameters(bodyStr);
            return;
        }

        if (contentType == null || contentType.isEmpty()) {
            request.setBody(bodyStr);
        }
    }

    private static String extractBoundary(String contentType) {
        String[] parts = contentType.split(";");
        for (String part : parts) {
            if (part.trim().startsWith("boundary=")) {
                return part.split("=")[1].trim();
            }
        }
        return null;
    }

    private static void parseMultipartData(HttpRequest request, InputStream inputStream, String boundary, int contentLength) throws IOException {
        byte[] boundaryBytes = ("--" + boundary).getBytes(StandardCharsets.UTF_8);
        byte[] endBoundaryBytes = ("--" + boundary + "--").getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream partStream = new ByteArrayOutputStream();
        ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
        int read;
        int totalRead = 0;

        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        boolean inPart = false;
        int b;
        while (totalRead < contentLength && (b = bufferedInputStream.read()) != -1) {
            totalRead++;
            bufferStream.write(b);

            if (bufferStream.size() >= boundaryBytes.length) {
                byte[] bufferArray = bufferStream.toByteArray();
                if (!inPart && startsWith(bufferArray, boundaryBytes)) {
                    inPart = true;
                    bufferStream.reset();
                } else if (inPart && endsWith(bufferArray, endBoundaryBytes)) {
                    processPart(request, partStream.toByteArray(), boundaryBytes.length + 2);
                    break;
                } else if (inPart && endsWith(bufferArray, boundaryBytes)) {
                    processPart(request, partStream.toByteArray(), boundaryBytes.length + 2);
                    partStream.reset();
                    bufferStream.reset();
                } else {
                    partStream.write(bufferArray[0]);
                    bufferStream.reset();
                    bufferStream.write(bufferArray, 1, bufferArray.length - 1);
                }
            }
        }
    }

    private static boolean startsWith(byte[] source, byte[] prefix) {
        if (source.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (source[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static boolean endsWith(byte[] source, byte[] suffix) {
        if (source.length < suffix.length) {
            return false;
        }
        for (int i = 0; i < suffix.length; i++) {
            if (source[i] != suffix[i]) {
                return false;
            }
        }
        return true;
    }

    private static void processPart(HttpRequest request, byte[] partData, int offset) throws IOException {
        ByteArrayInputStream partInputStream = new ByteArrayInputStream(partData);
        HttpHeaders partHeaders = new HttpHeaders();
        ByteArrayOutputStream partBody = new ByteArrayOutputStream();
        boolean headerEnded = false;
        ByteArrayOutputStream bufferStream = new ByteArrayOutputStream();
        int b;
        partInputStream.read();
        partInputStream.read();
        while ((b = partInputStream.read()) != -1) {
            String line = bufferStream.toString(StandardCharsets.UTF_8);
            if (b == '\r') {
                partInputStream.mark(1);
                if (partInputStream.read() == '\n') {
                    if (bufferStream.size() == 0) {
                        headerEnded = true;
                        continue;
                    }

                    if (!headerEnded) {
                        String[] headerParts = line.split(":");
                        if(headerParts.length == 2) {
                            partHeaders.addHeader(headerParts[0].trim(), headerParts[1].trim());
                        }
                    } else {
                        partBody.write(bufferStream.toByteArray());
                        partBody.write("\r\n".getBytes(StandardCharsets.UTF_8));
                    }

                    bufferStream.reset();
                } else {
                    partInputStream.reset();
                    bufferStream.write(b);
                }
            } else {
                bufferStream.write(b);
            }
        }

        // Handling the last part of the body
        if (bufferStream.size() > 0) {
            partBody.write(bufferStream.toByteArray());
        }

        String disposition = partHeaders.getHeader("Content-Disposition").get(0);
        String[] dispositionParts = disposition.split(";");
        String name = null;
        String filename = null;
        for (String part : dispositionParts) {
            if (part.trim().startsWith("name=")) {
                name = part.split("=")[1].trim().replace("\"", "");
            } else if (part.trim().startsWith("filename=")) {
                filename = part.split("=")[1].trim().replace("\"", "");
            }
        }

        if (filename != null) {
            // Process file upload part
            request.addFile(name, filename, partBody.toByteArray());
        } else {
            // Process form field part
            request.addParameter(name, partBody.toString(StandardCharsets.UTF_8).trim());
        }
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
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
