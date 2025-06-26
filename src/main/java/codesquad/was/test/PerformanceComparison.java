package codesquad.was.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * NIO vs AIO 성능 비교 테스트
 */
public class PerformanceComparison {
    
    private static final Logger logger = LoggerFactory.getLogger(PerformanceComparison.class);
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== NIO vs AIO Performance Comparison ===\n");
        
        // 워밍업
        System.out.println("Warming up server...");
        warmupServer();
        Thread.sleep(2000);
        
        // 성능 테스트 시나리오들
        runPerformanceTest("Light Load", 10, 100);
        Thread.sleep(3000);
        
        runPerformanceTest("Medium Load", 100, 500);
        Thread.sleep(3000);
        
        runPerformanceTest("Heavy Load", 500, 1000);
        Thread.sleep(3000);
        
        runPerformanceTest("Stress Test", 1000, 2000);
    }
    
    /**
     * 서버 워밍업
     */
    private static void warmupServer() throws Exception {
        ExecutorService warmupPool = Executors.newFixedThreadPool(10);
        
        for (int i = 0; i < 50; i++) {
            warmupPool.submit(() -> {
                try {
                    sendSimpleRequest();
                } catch (Exception e) {
                    // 워밍업에서는 에러 무시
                }
            });
        }
        
        warmupPool.shutdown();
        warmupPool.awaitTermination(10, TimeUnit.SECONDS);
    }
    
    /**
     * 성능 테스트 실행
     */
    private static void runPerformanceTest(String testName, int concurrency, int totalRequests) 
            throws Exception {
        
        System.out.println("--- " + testName + " ---");
        System.out.println("Concurrency: " + concurrency + ", Total Requests: " + totalRequests);
        
        ExecutorService executor = Executors.newFixedThreadPool(concurrency);
        CountDownLatch latch = new CountDownLatch(totalRequests);
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicLong totalResponseTime = new AtomicLong(0);
        
        long startTime = System.currentTimeMillis();
        
        // 요청 생성
        for (int i = 0; i < totalRequests; i++) {
            executor.submit(() -> {
                try {
                    long requestStart = System.nanoTime();
                    String response = sendSimpleRequest();
                    long requestEnd = System.nanoTime();
                    
                    if (response != null && response.contains("200 OK")) {
                        successCount.incrementAndGet();
                        totalResponseTime.addAndGet((requestEnd - requestStart) / 1_000_000); // ms로 변환
                    } else {
                        errorCount.incrementAndGet();
                    }
                    
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 모든 요청 완료 대기
        latch.await(60, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();
        
        executor.shutdown();
        
        // 결과 계산
        long totalTime = endTime - startTime;
        double throughput = (double) successCount.get() / totalTime * 1000; // RPS
        double avgResponseTime = totalResponseTime.get() / (double) successCount.get();
        
        // 결과 출력
        System.out.println("Results:");
        System.out.println("  Total Time: " + totalTime + " ms");
        System.out.println("  Success: " + successCount.get());
        System.out.println("  Errors: " + errorCount.get());
        System.out.println("  Throughput: " + String.format("%.2f", throughput) + " RPS");
        System.out.println("  Avg Response Time: " + String.format("%.2f", avgResponseTime) + " ms");
        System.out.println();
    }
    
    /**
     * 간단한 HTTP 요청 전송
     */
    private static String sendSimpleRequest() throws Exception {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            // HTTP GET 요청
            out.println("GET /test HTTP/1.1");
            out.println("Host: " + SERVER_HOST);
            out.println("Connection: close");
            out.println();
            
            // 응답 읽기
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line).append("\n");
            }
            
            return response.toString();
        }
    }
}
