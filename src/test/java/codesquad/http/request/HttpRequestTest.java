package codesquad.http.request;

import codesquad.was.request.HttpRequest;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class HttpRequestTest {

    @Test
    public void testSetAndGetUrl() throws MalformedURLException {
        HttpRequest request = new HttpRequest();
        String url = "http://example.com/page?name=value&key=value2";
        request.setUrl(new URL(url));
        assertEquals(url, request.getUrl().toString());
        assertEquals("/page", request.getUrlPath());
    }

    @Test
    public void testParseParameters() throws MalformedURLException {
        HttpRequest request = new HttpRequest();
        String url = "http://example.com/page?name=value&key=value2";
        request.setUrl(new URL(url));
        Map<String, String> parameters = request.getParameters();
        assertEquals(2, parameters.size());
        assertEquals("value", parameters.get("name"));
        assertEquals("value2", parameters.get("key"));
    }


    @Test
    public void testToString() throws MalformedURLException {
        HttpRequest request = new HttpRequest();
        request.setMethod("POST");
        request.setUrl(new URL("http://example.com/page?name=value"));
        request.setVersion("HTTP/1.1");
        request.addHeader("Content-Type", "application/json");
        request.setBody("{\"key\":\"value\"}");
        String expected = "HttpRequest{" +
                "method='POST'" +
                ", url='http://example.com/page?name=value'" +
                ", version='HTTP/1.1'" +
                ", headers={Content-Type=application/json}" +
                ", parameters={name=value}" +
                ", body='{\"key\":\"value\"}'" +
                '}';
        assertEquals(expected, request.toString());
    }
}