package codesquad.was.handler;

import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;

public interface Handler {
    HttpResponse handleRequest(HttpRequest request);
}
