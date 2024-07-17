package codesquad.was.handler;

import codesquad.was.exception.MethodNotAllowedException;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;

public interface Handler {

    default HttpResponse handlePOSTRequest(HttpRequest request){
        throw new MethodNotAllowedException();
    }

    default HttpResponse handleGETRequest(HttpRequest request) {
        throw new MethodNotAllowedException();
    }

    default HttpResponse handlePUTRequest(HttpRequest request) {
        throw new MethodNotAllowedException();
    }

    default HttpResponse handleDELETERequest(HttpRequest request) {
        throw new MethodNotAllowedException();
    }

    default HttpResponse doBusinessByMethod(HttpRequest request) {
        return switch (request.getMethod()) {
            case POST -> handlePOSTRequest(request);
            case GET -> handleGETRequest(request);
            case PUT -> handlePUTRequest(request);
            case DELETE -> handleDELETERequest(request);
            default -> throw new MethodNotAllowedException();
        };
    }
}
