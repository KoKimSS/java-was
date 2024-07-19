package codesquad.was.request;

import codesquad.was.http.common.HttpMethod;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.request.HttpRequestParser;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

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
        assertEquals(null, request.getBody());
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
        assertEquals(request.getBody(),null);
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

    @Test
    public void testParseHttpRequest_WithCookies() throws Exception {
        // HTTP 요청 샘플 (쿠키 포함)
        String httpRequestString =
                "GET /index.html HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Cookie: sessionId=abc123; userId=789xyz\r\n" +
                        "\r\n";

        InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes());

        // HttpRequestParser를 사용하여 요청 파싱
        HttpRequest request = HttpRequestParser.parseHttpRequest(inputStream);

        // 쿠키 확인
        Map<String, String> cookies = request.getCookies();
        assertNotNull(cookies);
        assertEquals(2, cookies.size());
        assertEquals("abc123", cookies.get("sessionId"));
        assertEquals("789xyz", cookies.get("userId"));
    }
}
