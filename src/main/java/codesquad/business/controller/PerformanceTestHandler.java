package codesquad.business.controller;

import codesquad.business.service.PerformanceTestService;
import codesquad.was.exception.InternalServerException;
import codesquad.was.handler.Handler;
import codesquad.was.http.common.HttpStatusCode;
import codesquad.was.http.common.Mime;
import codesquad.was.http.request.HttpRequest;
import codesquad.was.http.response.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PerformanceTestHandler implements Handler {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTestHandler.class);
    private static final PerformanceTestService performanceTestService = new PerformanceTestService();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static final PerformanceTestHandler performanceTestHandler = new PerformanceTestHandler();

    @Override
    public HttpResponse handleGETRequest(HttpRequest request) {
        HttpResponse response = new HttpResponse();
        
        try {
            String testType = request.getParameter("type");
            String delayParam = request.getParameter("delay");
            
            int delay = delayParam != null ? Integer.parseInt(delayParam) : 100;
            
            Map<String, Object> result = switch (testType != null ? testType : "mixed") {
                case "db" -> performanceTestService.performDatabaseTest(delay);
                case "file" -> performanceTestService.performFileTest(delay);
                case "network" -> performanceTestService.performNetworkTest(delay);
                default -> performanceTestService.performMixedTest(delay);
            };
            
            String jsonResponse = objectMapper.writeValueAsString(result);
            
            response.setStatusCode(HttpStatusCode.OK);
            response.setContentType(Mime.APPLICATION_JSON);
            response.setBody(jsonResponse.getBytes(StandardCharsets.UTF_8));
            
            logger.info("Performance test completed: type={}, delay={}, duration={}ms", 
                       testType, delay, result.get("duration"));
            
        } catch (Exception e) {
            logger.error("Performance test failed: {}", e.getMessage());
            throw new InternalServerException("Performance test failed: " + e.getMessage());
        }
        
        return response;
    }
}
