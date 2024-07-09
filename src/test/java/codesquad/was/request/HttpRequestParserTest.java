package codesquad.was.request;

import codesquad.was.common.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class HttpRequestParserTest {

    @Test
    void testParseHttpRequest() throws Exception {
        String rawRequest =
                "GET /index.html HTTP/1.1\r\n" +
                        "Host: example.com\r\n" +
                        "User-Agent: test-agent\r\n" +
                        "\r\n" +
                        "body content";

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        HttpRequest request = HttpRequestParser.parseHttpRequest(inputStream);

        assertEquals(HttpMethod.GET, request.getMethod());
        assertEquals(new URL("http://example.com/index.html"), request.getUrl());
        assertEquals("HTTP/1.1", request.getVersion());
        assertEquals("[example.com]", request.getHeaders().getHeader("Host").toString());
        assertEquals("[test-agent]", request.getHeaders().getHeader("User-Agent").toString());
        assertEquals("body content", request.getBody());
    }

    @Test
    void testParseHttpRequestWithNoBody() throws Exception {
        String rawRequest =
                "POST /api HTTP/1.1\r\n" +
                        "Host: example.com\r\n" +
                        "User-Agent: test-agent\r\n" +
                        "\r\n";

        InputStream inputStream = new ByteArrayInputStream(rawRequest.getBytes());
        HttpRequest request = HttpRequestParser.parseHttpRequest(inputStream);

        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals(new URL("http://example.com/api"), request.getUrl());
        assertEquals("HTTP/1.1", request.getVersion());
        assertThat("[example.com]").isEqualTo(request.getHeaders().getHeader("Host").toString());
        assertThat("[test-agent]").isEqualTo(request.getHeaders().getHeader("User-Agent").toString());
        assertEquals(request.getBody(),"");
    }

    @Test
    void testParseHttpRequestWithInvalidRequestLine() {
        String rawRequest =
                "INVALID REQUESTLINE\r\n" +
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
