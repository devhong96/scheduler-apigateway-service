# 📌Scheduler-ApiGateway-Service

---
Scheduler-ApiGateway-Service는 JWT의 인증과 각 서비스별 로드밸런싱을 담당합니다.

이 서비스는 애플리케이션에 접근하는 사용자의 인증 및 권한을 검증하며, 요청을 적절한 마이크로서비스로 라우팅하는 역할을 합니다. 

Spring Cloud Gateway를 기반으로 구현되었으며, JWT 토큰을 통해 사용자 인증을 처리하고, 각 서비스 간 부하를 분산하여 시스템의 안정성을 유지합니다.


---

## 🛠 설계 및 구조

application-apigateway.yml 파일에 정의된 규칙을 기반으로 요청을 처리합니다.

예를 들어, /scheduler-course-service/** 경로로 들어오는 요청은 Scheduler-Course-Service로 라우팅 되며 Spring Cloud LoadBalancer를 통해 동일한 서비스의 여러 인스턴스 중 하나를 선택하여 요청을 전달합니다. 이를 통해 특정 인스턴스에 부하가 집중되는 것을 방지합니다.

타 서비스와 마찬가지고 Grafana와 Prometheus로 모니터링을 하고 있습니다.


