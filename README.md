# AI Log Analysis System

## 📊 대용량 로그 처리 최적화

### 🚀 성능 개선 사항 (50개+ 서버 지원)

#### 1. Kafka Consumer 최적화
- **동시 처리 스레드**: 1개 → 10개 (`concurrency: 10`)
- **배치 처리**: 한 번에 100개 메시지 처리 (`max-poll-records: 100`)
- **페치 최적화**: 50KB 최소 페치 크기, 500ms 대기 시간
- **메모리 효율성**: 파티션당 1MB 최대 페치

#### 2. 배치 처리 아키텍처
```java
// 기존: 메시지 1개씩 처리
@KafkaListener(topics = {"raw-logs.spring"})
public void processOne(ConsumerRecord<String, String> record)

// 개선: 메시지 100개씩 배치 처리  
@KafkaListener(containerFactory = "batchKafkaListenerContainerFactory")
public void processBatch(List<ConsumerRecord<String, String>> records)
```

#### 3. 비동기 처리 향상
- **전용 스레드 풀**: 로그 처리용 별도 Executor (10-50 스레드)
- **논블로킹 처리**: `@Async` + `CompletableFuture`
- **백프레셔 대응**: `CallerRunsPolicy`로 시스템 보호

#### 4. 데이터베이스 최적화
- **연결 풀 확장**: 최대 20개 연결, 최소 10개 유지
- **배치 저장**: `repository.saveAll()` 활용
- **트랜잭션 최적화**: 배치 단위 트랜잭션 처리

### 📈 예상 성능 향상

| 항목 | 기존 | 개선 후 | 향상률 |
|------|------|---------|--------|
| 처리량 | ~100 msg/sec | ~1,000+ msg/sec | **10x** |
| 레이턴시 | 개별 트랜잭션 | 배치 처리 | **50%** 감소 |
| 메모리 사용량 | 높음 | 최적화됨 | **30%** 감소 |
| CPU 활용률 | 단일 스레드 | 멀티 스레드 | **효율성** 개선 |

### 🔧 운영 환경 권장 설정

#### JVM 옵션
```bash
-Xms2g -Xmx4g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=100
-XX:+HeapDumpOnOutOfMemoryError
```

#### Kafka 파티션 전략
```
raw-logs.spring: 10 파티션 (서버 수/5)
raw-logs.nginx: 10 파티션
urgent-logs: 3 파티션 (고우선순위)
```

### 📊 모니터링 지표

#### 핵심 메트릭
- `kafka.consumer.lag`: 컨슈머 지연도
- `log.processing.batch.size`: 배치 크기
- `log.processing.success.rate`: 처리 성공률
- `jvm.memory.used`: 메모리 사용률

#### 알람 임계값
- 컨슈머 지연 > 10,000 메시지
- 처리 성공률 < 95%
- 메모리 사용률 > 80%

### 🛠️ 확장성 고려사항

#### 수평 확장
- **인스턴스 복제**: 동일한 컨슈머 그룹으로 여러 인스턴스 실행
- **파티션 증설**: 트래픽 증가 시 Kafka 파티션 수 확장
- **로드 밸런싱**: Nginx/HAProxy를 통한 트래픽 분산

#### 수직 확장  
- **CPU**: 16+ 코어 권장 (동시 처리 스레드 수만큼)
- **메모리**: 8GB+ 권장 (배치 처리용 버퍼)
- **디스크**: SSD 사용 (DB I/O 성능)

### 🔍 문제 해결 가이드

#### 높은 컨슈머 지연
1. `concurrency` 값 증가 (CPU 코어 수까지)
2. `max-poll-records` 조정 (메모리 허용 범위 내)
3. 인스턴스 추가 (수평 확장)

#### 메모리 부족
1. 배치 크기 감소 (`max-poll-records`)
2. 힙 메모리 증가 (`-Xmx`)
3. G1GC 튜닝

#### 처리 실패율 증가
1. 로그 확인 (`logging.level.com.demo.ailog: DEBUG`)
2. DB 연결 풀 상태 점검
3. 네트워크 상태 확인

---

## 기존 내용...

