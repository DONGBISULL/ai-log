# AI-Log 테스트 환경 설정 가이드

이 디렉토리는 AI-Log 시스템의 테스트 환경을 구성하기 위한 Docker Compose 설정을 포함합니다.

## 🔧 사전 준비

### 필수 요구사항
- Docker 및 Docker Compose 설치
- 8080, 8081, 9092, 2181 포트가 사용 가능해야 함

### 네트워크 설정 확인

**중요**: Kafka 외부 접근을 위해 현재 호스트의 IP 주소를 설정해야 합니다.

#### 1. 현재 IP 주소 확인

**macOS/Linux:**
```bash
# 방법 1: ifconfig 사용
ifconfig | grep "inet " | grep -v 127.0.0.1

# 방법 2: IP route 사용 (Linux)
ip route get 1 | awk '{print $7}' | head -1

# 방법 3: hostname 사용
hostname -I | awk '{print $1}'
```

**Windows:**
```cmd
ipconfig | findstr "IPv4"
```

#### 2. docker-compose.yml 파일 수정

`docker-compose.yml` 파일의 53번째 라인에서 IP 주소를 수정하세요:

```yaml
# 수정 전 (예시)
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://192.168.68.60:9092

# 수정 후 (본인의 IP로 변경)
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://[YOUR_IP]:9092
```

**예시:**
- IP가 `192.168.1.100`인 경우: `PLAINTEXT_HOST://192.168.1.100:9092`
- IP가 `10.0.0.50`인 경우: `PLAINTEXT_HOST://10.0.0.50:9092`

## 🚀 실행 방법

### 1. 테스트 환경 시작
```bash
cd test-env
docker-compose up -d
```

### 2. 서비스 상태 확인
```bash
docker-compose ps
```

### 3. 로그 확인
```bash
# 모든 서비스 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f kafka
docker-compose logs -f demo-app
```

## 📋 서비스 구성

| 서비스 | 포트 | 설명 |
|--------|------|------|
| demo-app | 8080 | 테스트용 데모 애플리케이션 |
| kafka-ui | 8081 | Kafka 관리 웹 UI |
| kafka | 9092 | Kafka 브로커 (외부 접근용) |
| zookeeper | 2181 | Zookeeper (Kafka 코디네이터) |

## 🔍 접속 정보

- **데모 앱**: http://localhost:8080
- **Kafka UI**: http://localhost:8081
- **Kafka 브로커**: localhost:9092

## 🛠️ 트러블슈팅

### Kafka 연결 문제
1. IP 주소가 올바르게 설정되었는지 확인
2. 방화벽에서 9092 포트가 열려있는지 확인
3. Docker 네트워크 상태 확인: `docker network ls`

### 포트 충돌 문제
다른 서비스가 포트를 사용 중인 경우:
```bash
# 포트 사용 확인
netstat -tulpn | grep :8080
netstat -tulpn | grep :9092

# 포트를 사용하는 프로세스 종료 후 재시작
```

### 컨테이너 재시작
```bash
# 모든 서비스 재시작
docker-compose restart

# 특정 서비스만 재시작
docker-compose restart kafka
```

## 🧹 정리

테스트 완료 후 환경 정리:
```bash
# 컨테이너 중지 및 제거
docker-compose down

# 볼륨까지 제거 (데이터 완전 삭제)
docker-compose down -v
```

## 📝 주의사항

1. **IP 주소**: 반드시 본인 환경의 실제 IP 주소로 수정하세요
2. **네트워크**: 같은 네트워크의 다른 머신에서 접근하려면 방화벽 설정 확인
3. **데이터**: 테스트 데이터는 `./logs` 디렉토리에 저장됩니다
4. **재시작**: Docker 데몬 재시작 시 자동으로 컨테이너가 재시작됩니다

---

문제가 발생하면 로그를 확인하고, IP 설정을 다시 검토해주세요.