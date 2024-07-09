package codesquad.was.response;

import codesquad.was.common.HTTPStatusCode;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HTTPStatusCode statusCode;
    private String statusMessage;
    private String contentType;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public HttpResponse() {
    }

    public HTTPStatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HTTPStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setHeader(String key, String value) {
        headers.put(key, value);
    }

    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public static void setRedirect(HttpResponse httpResponse, HTTPStatusCode statusCode, String redirectUrl) {
        int code = statusCode.getCode();
        // 3xx 로 시작하지 않으면 임의로 302로 변경
        if(code/100!=3) statusCode = HTTPStatusCode.FOUND;

        httpResponse.setStatusCode(statusCode);
        httpResponse.setHeader("Location", redirectUrl);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", statusMessage='" + statusMessage + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
