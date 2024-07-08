package codesquad.was.request;

import java.io.*;
import java.net.URL;

public class HttpRequestParser {

    public static HttpRequest parseHttpRequest(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest request = new HttpRequest();

        // Parse request line
        String requestLine = reader.readLine();
        System.out.println(requestLine);
        if (requestLine == null || requestLine.isEmpty()) {
            throw new IOException("Empty request line");
        }
        String[] requestLineParts = requestLine.split(" ");
        if (requestLineParts.length != 3) {
            throw new IOException("Invalid request line: " + requestLine);
        }
        request.setMethod(requestLineParts[0]);
        String path = requestLineParts[1];
        request.setVersion(requestLineParts[2]);

        // Parse headers
        String headerLine;

        while (!(headerLine = reader.readLine()).isEmpty()) {
            String[] headerParts = headerLine.split(": ", 2);
            if (headerParts.length == 2) {
                request.addHeader(headerParts[0], headerParts[1]);
            }
        }

        String host = request.getHeaders().get("Host");
        String protocol = "http";
        URL url = new URL(protocol, host, path);
        request.setUrl(url);
        parseBody(request, reader);

        return request;
    }

    private static void parseBody(HttpRequest request, BufferedReader reader) throws IOException {
        // Parse body (if any)
        String contentType = request.getHeaders().get("Content-Type");
        request.setContentType(contentType);
        StringBuilder body = new StringBuilder();

        while (reader.ready()) {
            body.append((char) reader.read());
        }

        if("application/x-www-form-urlencoded".equals(contentType)) {
            request.parseParameters(body.toString());
        }
    }
}
