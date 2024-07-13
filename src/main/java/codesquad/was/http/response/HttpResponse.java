package codesquad.was.http.response;

import codesquad.was.http.common.HttpCookie;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.http.common.HttpHeaders;
import codesquad.was.http.common.Mime;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpStatusCode statusCode;
    private String statusMessage;
    private Mime contentType;
    private final HttpHeaders headers = new HttpHeaders();
    private final Map<String, HttpCookie> cookies = new HashMap<>();

    private byte[] body;
    public HttpResponse() {
    }

    public Map<String, HttpCookie> getCookies() {
        return cookies;
    }

    public void addCookie(HttpCookie cookie) {
        cookies.putIfAbsent(cookie.getName(),cookie);
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }


    public void addHeader(String key, String value) {
        headers.addHeader(key, value);
    }


    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public Mime getContentType() {
        return contentType;
    }

    public void setContentType(Mime contentType) {
        this.contentType = contentType;
    }

    public static void setRedirect(HttpResponse httpResponse, HttpStatusCode statusCode, String redirectUrl) {
        int code = statusCode.getCode();
        // 3xx 로 시작하지 않으면 임의로 302로 변경
        if(code/100!=3) statusCode = HttpStatusCode.FOUND;

        httpResponse.setStatusCode(statusCode);
        httpResponse.addHeader("Location", redirectUrl);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", statusMessage='" + statusMessage + '\'' +
                ", contentType='" + contentType + '\'' +
                ", headers=" + headers +
                ", cookies=" + cookies +
//                ", body=" + (body != null ? new String(body) : "null") +
                '}';
    }
}
