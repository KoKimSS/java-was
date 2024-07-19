package codesquad.was.http.common;

import java.util.Arrays;

public enum Mime {
    // 기본적인 MIME 타입들 정의
    TEXT_PLAIN("text/plain"),
    TEXT_HTML("text/html"),
    TEXT_XML("text/xml"),
    TEXT_JAVASCRIPT("text/javascript"),
    TEXT_CSS("text/css"),
    TEXT_HTML5("text/html5"),
    APPLICATION_JSON("application/json"),
    APPLICATION_XML("application/xml"),
    IMAGE_PNG("image/png"),
    IMAGE_JPEG("image/jpeg"),
    IMAGE_GIF("image/gif"),
    IMAGE_SVG("image/svg+xml"),
    IMAGE_ICO("image/x-icon"),
    IMAGE_WEBP("image/webp"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    APPLICATION_OCTET_STREAM("application/octet-stream"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded"),
    MULTIPART_FORM_DATA("multipart/form-data");

    private final String mimeType;

    Mime(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    // MIME 타입 문자열을 통해 Enum 객체를 얻는 메서드
    public static Mime fromString(String mimeType) {
        if(mimeType.startsWith(MULTIPART_FORM_DATA.getMimeType())){
            return MULTIPART_FORM_DATA;
        }

        return Arrays.stream(Mime.values())
                .filter((m) -> m.getMimeType().equals(mimeType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown MIME type: " + mimeType));
    }

    @Override
    public String toString() {
        return this.mimeType;
    }
}
