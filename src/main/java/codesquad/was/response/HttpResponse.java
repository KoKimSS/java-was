package codesquad.was.response;

import codesquad.was.common.HttpStatusCode;
import codesquad.was.common.HttpHeaders;

public class HttpResponse {
    private HttpStatusCode statusCode;
    private String statusMessage;
    private String contentType;
    private final HttpHeaders headers = new HttpHeaders();
    private byte[] body;

    public HttpResponse() {
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

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
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
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
