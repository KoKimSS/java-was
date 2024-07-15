package codesquad.was.http.common;

public class HttpCookie {
    private String name;
    private String value;
    private String path;
    private String domain;
    private long maxAge;
    private boolean secure;
    private boolean httpOnly;

    public HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
        this.path = "/";
        this.maxAge = -1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    public boolean isSecure() {
        return secure;
    }

    public void setSecure(boolean secure) {
        this.secure = secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public void setHttpOnly(boolean httpOnly) {
        this.httpOnly = httpOnly;
    }

    @Override
    public String toString() {
        StringBuilder cookieString = new StringBuilder(name + "=" + value);
        if (domain != null) {
            cookieString.append("; Domain=").append(domain);
        }
        if (path != null) {
            cookieString.append("; Path=").append(path);
        }
        if (maxAge >= 0) {
            cookieString.append("; Max-Age=").append(maxAge);
        }
        if (secure) {
            cookieString.append("; Secure");
        }
        if (httpOnly) {
            cookieString.append("; HttpOnly");
        }
        return cookieString.toString();
    }
}
