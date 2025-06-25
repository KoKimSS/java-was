from locust import HttpUser, task, between
import random

class PerformanceTestUser(HttpUser):
    # 사용자간 요청 간격 (초)
    wait_time = between(0.1, 1.0)
    
    def on_start(self):
        """각 사용자가 시작될 때 실행"""
        print(f"User {self.user_id} started")
    
    @task(4)
    def database_intensive_test(self):
        """데이터베이스 집약적 테스트 (가장 빈번)"""
        delay = random.randint(50, 150)
        with self.client.get(
            f"/api/performance-test?type=db&delay={delay}",
            catch_response=True,
            name="DB Test"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")
    
    @task(3)
    def mixed_io_test(self):
        """복합 I/O 테스트"""
        delay = random.randint(100, 200)
        with self.client.get(
            f"/api/performance-test?type=mixed&delay={delay}",
            catch_response=True,
            name="Mixed I/O Test"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")
    
    @task(2)
    def file_io_test(self):
        """파일 I/O 테스트"""
        delay = random.randint(30, 100)
        with self.client.get(
            f"/api/performance-test?type=file&delay={delay}",
            catch_response=True,
            name="File I/O Test"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")
    
    @task(1)
    def network_test(self):
        """네트워크 테스트"""
        delay = random.randint(20, 80)
        with self.client.get(
            f"/api/performance-test?type=network&delay={delay}",
            catch_response=True,
            name="Network Test"
        ) as response:
            if response.status_code == 200:
                response.success()
            else:
                response.failure(f"Got status code {response.status_code}")

class LightLoadUser(HttpUser):
    """가벼운 부하용 사용자"""
    wait_time = between(1, 3)
    weight = 1  # 비중
    
    @task
    def light_test(self):
        self.client.get("/api/performance-test?type=db&delay=10", name="Light Test")

class HeavyLoadUser(HttpUser):
    """무거운 부하용 사용자"""
    wait_time = between(0.1, 0.3)
    weight = 3  # 비중
    
    @task
    def heavy_test(self):
        self.client.get("/api/performance-test?type=mixed&delay=200", name="Heavy Test")
