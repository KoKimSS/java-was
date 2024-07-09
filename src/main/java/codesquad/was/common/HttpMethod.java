package codesquad.was.common;

public enum HttpMethod {
    GET, POST, PUT, DELETE;

    public static HttpMethod getHttpMethodByString(String string) {
        string = string.toLowerCase();
        return switch (string) {
            case "get" -> GET;
            case "post" -> POST;
            case "put" -> PUT;
            case "delete" -> DELETE;
            default -> throw new IllegalArgumentException("Invalid HTTP method: " + string);
        };
    }
}
