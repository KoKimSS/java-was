package codesquad.was.server;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LoadTest {
//
//    private static Server server;
//
//    @BeforeAll
//    static void setUp() throws IOException {
//        server = new Server(10, 8079, 50);
//        new Thread(() -> {
//            try {
//                server.run();
//            } catch (IOException ignored) {
//            }
//        }).start();
//    }
//
//    @Test
//    @DisplayName("")
//    public void testLoad() {
//
//    }
//
//    @Test
//    public void testConcurrentRequests() throws InterruptedException, IOException {
//        final int NUM_REQUESTS = 3000;
//        ExecutorService executorService = Executors.newFixedThreadPool(NUM_REQUESTS);
//        CountDownLatch latch = new CountDownLatch(NUM_REQUESTS);
//        AtomicInteger responseCount = new AtomicInteger();
//        for (int i = 0; i < NUM_REQUESTS; i++) {
//            executorService.execute(() -> {
//                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
//                    HttpGet request = new HttpGet("http://localhost:8079");
//                    try (CloseableHttpResponse response = httpClient.execute(request)) {
//                        assertEquals(200, response.getStatusLine().getStatusCode());
//                        responseCount.getAndIncrement();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally {
//                    latch.countDown();
//                }
//            });
//        }
//
//        latch.await();
//
//        System.out.println(responseCount.get());
//        executorService.shutdown();
//
//    }
//
//    @AfterAll
//    static void tearDown() throws IOException {
//        server.close();
//    }
}
