package codesquad.was.handler;

import codesquad.was.common.HttpMethod;
import codesquad.was.exception.BadRequestException;
import codesquad.was.exception.InternalServerException;
import codesquad.was.exception.MethodNotAllowedException;
import codesquad.was.request.HttpRequest;
import codesquad.was.response.HttpResponse;

import java.io.IOException;

public interface Handler {

    HttpResponse handlePOSTRequest(HttpRequest request) throws InternalServerException, IOException, MethodNotAllowedException, BadRequestException;
    HttpResponse handleGETRequest(HttpRequest request) throws InternalServerException, IOException, MethodNotAllowedException;
    HttpResponse handlePUTRequest(HttpRequest request) throws InternalServerException, IOException, MethodNotAllowedException;
    HttpResponse handleDELETERequest(HttpRequest request) throws InternalServerException, IOException, MethodNotAllowedException;

     default HttpResponse doBusinessByMethod(HttpRequest request) throws MethodNotAllowedException, InternalServerException, BadRequestException, IOException {
        return switch (request.getMethod()) {
            case POST -> handlePOSTRequest(request);
            case GET -> handleGETRequest(request);
            case PUT -> handlePUTRequest(request);
            case DELETE -> handleDELETERequest(request);
            default -> throw new MethodNotAllowedException();
        };
    }
}
