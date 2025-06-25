package codesquad.business.service;

import codesquad.business.repository.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PerformanceTestService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTestService.class);
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * 데이터베이스 집약적 테스트
     */
    public Map<String, Object> performDatabaseTest(int delayMs) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 의도적 지연 추가
            Thread.sleep(delayMs);
            
            List<Map<String, Object>> data = new ArrayList<>();
            
            // 여러 개의 DB 쿼리 실행
            for (int i = 0; i < 5; i++) {
                data.addAll(executeSlowQuery());
            }
            
            result.put("type", "database");
            result.put("queryCount", 5);
            result.put("recordCount", data.size());
            result.put("data", data.subList(0, Math.min(3, data.size()))); // 샘플 데이터만
            
        } catch (Exception e) {
            logger.error("Database test failed: {}", e.getMessage());
            result.put("error", e.getMessage());
        }
        
        long duration = System.currentTimeMillis() - startTime;
        result.put("duration", duration);
        
        return result;
    }

    /**
     * 파일 I/O 집약적 테스트
     */
    public Map<String, Object> performFileTest(int delayMs) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        try {
            Thread.sleep(delayMs);
            
            List<String> operations = new ArrayList<>();
            
            // 임시 파일 생성 및 쓰기
            for (int i = 0; i < 3; i++) {
                String fileName = "test_" + System.nanoTime() + "_" + i + ".txt";
                Path filePath = Paths.get(TEMP_DIR, fileName);
                
                // 파일 쓰기
                String content = generateLargeContent(1000); // 1000줄 생성
                Files.write(filePath, content.getBytes());
                operations.add("WRITE: " + fileName);
                
                // 파일 읽기
                List<String> lines = Files.readAllLines(filePath);
                operations.add("READ: " + fileName + " (" + lines.size() + " lines)");
                
                // 파일 삭제
                Files.deleteIfExists(filePath);
                operations.add("DELETE: " + fileName);
            }
            
            result.put("type", "file");
            result.put("operations", operations);
            result.put("fileCount", 3);
            
        } catch (Exception e) {
            logger.error("File test failed: {}", e.getMessage());
            result.put("error", e.getMessage());
        }
        
        long duration = System.currentTimeMillis() - startTime;
        result.put("duration", duration);
        
        return result;
    }

    /**
     * 네트워크 I/O 집약적 테스트
     */
    public Map<String, Object> performNetworkTest(int delayMs) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        try {
            Thread.sleep(delayMs);
            
            List<Map<String, Object>> requests = new ArrayList<>();
            
            // 여러 HTTP 요청 시뮬레이션 (실제로는 로컬 요청)
            for (int i = 0; i < 3; i++) {
                Map<String, Object> requestResult = simulateHttpRequest();
                requests.add(requestResult);
            }
            
            result.put("type", "network");
            result.put("requests", requests);
            result.put("requestCount", requests.size());
            
        } catch (Exception e) {
            logger.error("Network test failed: {}", e.getMessage());
            result.put("error", e.getMessage());
        }
        
        long duration = System.currentTimeMillis() - startTime;
        result.put("duration", duration);
        
        return result;
    }

    /**
     * 복합 I/O 테스트
     */
    public Map<String, Object> performMixedTest(int delayMs) {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        try {
            Thread.sleep(delayMs);
            
            // DB + File + Network 혼합
            Map<String, Object> dbResult = performDatabaseTest(0);
            Map<String, Object> fileResult = performFileTest(0);
            Map<String, Object> networkResult = performNetworkTest(0);
            
            result.put("type", "mixed");
            result.put("components", Map.of(
                "database", dbResult,
                "file", fileResult,
                "network", networkResult
            ));
            
        } catch (Exception e) {
            logger.error("Mixed test failed: {}", e.getMessage());
            result.put("error", e.getMessage());
        }
        
        long duration = System.currentTimeMillis() - startTime;
        result.put("duration", duration);
        
        return result;
    }

    private List<Map<String, Object>> executeSlowQuery() {
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Connection conn = JdbcTemplate.getConnection()) {
            // 의도적으로 느린 쿼리 (CROSS JOIN 사용)
            String sql = """
                SELECT m1.username, m2.username as username2, a.title
                FROM member m1 
                CROSS JOIN member m2 
                JOIN article a ON a.user_id = m1.id
                LIMIT 10
                """;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("username1", rs.getString("username"));
                    row.put("username2", rs.getString("username2"));
                    row.put("title", rs.getString("title"));
                    results.add(row);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Slow query failed: {}", e.getMessage());
        }
        
        return results;
    }

    // PerformanceTestService에 추가
    public Map<String, Object> performCpuIntensiveTest(int iterations) {
        long startTime = System.currentTimeMillis();

        // CPU 집약적 작업
        long result = 0;
        for (int i = 0; i < iterations * 100000; i++) {
            result += Math.sqrt(i) * Math.sin(i);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("type", "cpu");
        response.put("result", result);
        response.put("duration", System.currentTimeMillis() - startTime);
        return response;
    }

    private String generateLargeContent(int lines) {
        StringBuilder content = new StringBuilder();
        Random random = ThreadLocalRandom.current();
        
        for (int i = 0; i < lines; i++) {
            content.append("Line ").append(i + 1).append(": ");
            content.append("Random data: ").append(random.nextInt(1000000));
            content.append(" - ").append(UUID.randomUUID().toString());
            content.append("\n");
        }
        
        return content.toString();
    }

    private Map<String, Object> simulateHttpRequest() {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            // 실제 HTTP 요청 대신 Thread.sleep으로 네트워크 지연 시뮬레이션
            Thread.sleep(50 + ThreadLocalRandom.current().nextInt(100)); // 50-150ms
            
            result.put("status", "success");
            result.put("responseTime", System.currentTimeMillis() - startTime);
            result.put("data", "Simulated response data: " + UUID.randomUUID().toString());
            
        } catch (InterruptedException e) {
            result.put("status", "error");
            result.put("error", e.getMessage());
        }
        
        return result;
    }
}
