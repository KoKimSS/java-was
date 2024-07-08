package codesquad.was.handler;

import codesquad.was.exception.InternalServerException;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;

import java.io.IOException;

public interface Handler {
    HttpResponse handleRequest(HttpRequest request) throws InternalServerException, IOException;
}
