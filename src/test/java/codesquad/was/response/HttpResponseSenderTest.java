package codesquad.was.response;

import codesquad.was.common.HttpCookie;
import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.*;

import codesquad.was.common.HttpStatusCode;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class HttpResponseSenderTest {
    private HttpResponse response;
    private OutputStream outputStream;

    @BeforeEach
    void setUp() {
        response = new HttpResponse();
        outputStream = mock(OutputStream.class);
    }

    @Test
    void sendHttpResponse_withValidResponse_writesCorrectOutput() throws IOException {
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType("text/html");
        byte[] bytes = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        response.setBody(bytes);
        response.addHeader("Set-Cookie", "sessionId=abc123");
        response.addHeader("Set-Cookie", "theme=light");

        HttpResponseSender.sendHttpResponse(outputStream, response);

        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        verify(outputStream, times(3)).write(captor.capture());

        List<byte[]> allValues = captor.getAllValues();
        String statusLine = new String(allValues.get(0), StandardCharsets.UTF_8);
        String headers = new String(allValues.get(1), StandardCharsets.UTF_8);
        String body = new String(allValues.get(2), StandardCharsets.UTF_8);

        assertThat("HTTP/1.1 200\r\n").isEqualTo(statusLine);
        assertThat("Content-Type: text/html\r\nContent-Length: "+bytes.length+"\r\nSet-Cookie: sessionId=abc123\r\nSet-Cookie: theme=light\r\n\r\n").isEqualTo(headers);
        assertThat("Hello, World!").isEqualTo(body);
    }

    @Test
    void sendHttpResponse_withNoBody_writesCorrectOutput() throws IOException {
        response.setStatusCode(HttpStatusCode.NO_CONTENT);
        response.setContentType("text/plain");

        HttpResponseSender.sendHttpResponse(outputStream, response);

        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        verify(outputStream, times(2)).write(captor.capture());

        List<byte[]> allValues = captor.getAllValues();
        String statusLine = new String(allValues.get(0), StandardCharsets.UTF_8);
        String headers = new String(allValues.get(1), StandardCharsets.UTF_8);

        assertEquals("HTTP/1.1 204\r\n", statusLine);
        assertEquals("Content-Type: text/plain\r\nContent-Length: 0\r\n\r\n", headers);
    }

    @Test
    void sendHttpResponse_withMultipleHeaders_writesCorrectOutput() throws IOException {
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType("application/json");
        byte[] bytes = "{\"message\":\"success\"}".getBytes(StandardCharsets.UTF_8);
        response.setBody(bytes);
        response.addHeader("X-Custom-Header", "value1");
        response.addHeader("X-Custom-Header", "value2");

        HttpResponseSender.sendHttpResponse(outputStream, response);

        ArgumentCaptor<byte[]> captor = ArgumentCaptor.forClass(byte[].class);
        verify(outputStream, times(3)).write(captor.capture());

        List<byte[]> allValues = captor.getAllValues();
        String statusLine = new String(allValues.get(0), StandardCharsets.UTF_8);
        String headers = new String(allValues.get(1), StandardCharsets.UTF_8);
        String body = new String(allValues.get(2), StandardCharsets.UTF_8);

        assertEquals("HTTP/1.1 200\r\n", statusLine);
        assertEquals("Content-Type: application/json\r\nContent-Length: "+bytes.length+"\r\nX-Custom-Header: value1\r\nX-Custom-Header: value2\r\n\r\n", headers);
        assertEquals("{\"message\":\"success\"}", body);
    }

    @Test
    public void testSendHttpResponse_WithCookies() throws Exception {
        // OutputStream 모킹
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // HttpResponse 객체 생성 및 설정
        HttpResponse response = new HttpResponse();
        response.setStatusCode(HttpStatusCode.OK);
        response.setContentType("text/html");
        response.setBody("Hello, World!".getBytes());

        // 쿠키 설정
        response.addCookie(new HttpCookie("sessionId", "abc123"));
        response.addCookie(new HttpCookie("userId", "789xyz"));

        // HTTP 응답 전송
        HttpResponseSender.sendHttpResponse(outputStream, response);

        // 전송된 데이터 확인
        String httpResponseString = outputStream.toString();

        // 쿠키 헤더가 포함되었는지 확인
        assertTrue(httpResponseString.contains("Set-Cookie: sessionId=abc123"));
        assertTrue(httpResponseString.contains("Set-Cookie: userId=789xyz"));
    }
}