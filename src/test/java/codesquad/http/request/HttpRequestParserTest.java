package codesquad.http.request;

import codesquad.was.request.HttpRequest;
import codesquad.was.request.HttpRequestParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestParserTest {

    @Test
    void testParseHttpRequest() throws Exception {
        String rawRequest =
                "GET http://example.com/index.html HTTP/1.1\r\n" +
                        "Host: example.com\r\n" +
                        "User-Agent: test-agent\r\n" +
                        "\r\n" +
                        "body content";

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        HttpRequest request = HttpRequestParser.parseHttpRequest(inputStream);

        assertEquals("GET", request.getMethod());
        assertEquals(new URL("http://example.com/index.html"), request.getUrl());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("example.com", request.getHeaders().get("Host"));
        assertEquals("test-agent", request.getHeaders().get("User-Agent"));
        assertEquals("body content", request.getBody());
    }

    @Test
    void testParseHttpRequestWithNoBody() throws Exception {
        String rawRequest =
                "POST http://example.com/api HTTP/1.1\r\n" +
                        "Host: example.com\r\n" +
                        "User-Agent: test-agent\r\n" +
                        "\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        HttpRequest request = HttpRequestParser.parseHttpRequest(inputStream);

        assertEquals("POST", request.getMethod());
        assertEquals(new URL("http://example.com/api"), request.getUrl());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("example.com", request.getHeaders().get("Host"));
        assertEquals("test-agent", request.getHeaders().get("User-Agent"));
        assertEquals(request.getBody(),"");
    }

    @Test
    void testParseHttpRequestWithInvalidRequestLine() {
        String rawRequest =
                "INVALID REQUEST LINE\r\n" +
                        "Host: example.com\r\n" +
                        "\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        assertThrows(IOException.class, () -> {
            HttpRequestParser.parseHttpRequest(inputStream);
        });
    }

    @Test
    void testParseHttpRequestWithEmptyRequestLine() {
        String rawRequest =
                "\r\n" +
                        "Host: example.com\r\n" +
                        "\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        assertThrows(IOException.class, () -> {
            HttpRequestParser.parseHttpRequest(inputStream);
        });
    }
}
