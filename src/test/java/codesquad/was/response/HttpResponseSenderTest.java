package codesquad.was.response;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.io.OutputStream;

import static org.assertj.core.api.Assertions.*;

import codesquad.was.common.HTTPStatusCode;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        response.setStatusCode(HTTPStatusCode.OK);
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
        response.setStatusCode(HTTPStatusCode.NO_CONTENT);
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
        response.setStatusCode(HTTPStatusCode.OK);
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
}