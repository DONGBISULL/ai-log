# AI Log Analysis System

AI 기반 실시간 로그 분석 및 에러 보고서 생성 시스템

## 🎯 프로젝트 개요

대용량 로그 데이터를 실시간으로 수집, 분석하여 AI 기반 에러 보고서를 생성하는 시스템입니다.
Kafka를 통한 스트림 처리와 Vector DB를 활용한 유사 로그 검색 기능을 제공합니다.

## 🏗️ 시스템 아키텍처

```
┌─────────────┐    ┌─────────┐    ┌──────────────┐    ┌─────────────┐
│ Log Sources │───▶│  Kafka  │───▶│ AI-Log App   │───▶│ PostgreSQL  │
│ (Filebeat)  │    │ Streams │    │ (Spring)     │    │ + PgVector  │
└─────────────┘    └─────────┘    └──────────────┘    └─────────────┘
                                         │
                                         ▼
                                  ┌─────────────┐
                                  │ Gemini AI   │
                                  │ (Analysis)  │
                                  └─────────────┘
```

## 🚀 주요 기능

### 1. 실시간 로그 수집 및 처리
- **Kafka Consumer**: 대용량 로그 배치 처리 (100개/배치)
- **동시 처리**: 10개 스레드 동시 처리로 성능 최적화
- **로그 파서**: Spring Boot, Nginx 로그 자동 파싱
- **에러 레벨 추출**: INFO, WARN, ERROR 등 로그 레벨 자동 분류

### 2. AI 기반 에러 분석
- **Gemini AI 연동**: Google Gemini 2.0 Flash 모델 활용
- **자동 에러 분석**: 스케줄러 기반 주기적 에러 로그 분석
- **에러 보고서 생성**: 한국어 에러 분석 보고서 자동 생성
- **실패 처리**: 분석 실패 시 재시도 메커니즘

### 3. Vector 기반 유사 로그 검색
- **PgVector 연동**: PostgreSQL Vector 확장 활용
- **임베딩 생성**: Gemini Embedding 모델로 로그 벡터화
- **유사도 검색**: 코사인 유사도 기반 유사 로그 탐지
- **HNSW 인덱스**: 고성능 벡터 검색을 위한 인덱스 최적화

### 4. 웹 기반 관리 인터페이스
- **로그 분석 API**: RESTful API를 통한 에러 분석 조회
- **대시보드**: Thymeleaf 기반 웹 인터페이스
- **실시간 모니터링**: 처리 현황 및 에러 통계 제공

## 📁 프로젝트 구조

```
src/main/java/com/demo/ailog/
├── analogy/                    # 로그 분석 및 검색
│   ├── controller/            # REST API 컨트롤러
│   ├── domain/               # DTO 및 도메인 객체
│   └── service/              # 비즈니스 로직
├── common/                    # 공통 설정 및 유틸리티
│   ├── config/               # Spring 설정 클래스
│   ├── enums/                # 열거형 정의
│   ├── exception/            # 예외 처리
│   └── handler/              # 글로벌 예외 핸들러
├── embed/                     # 벡터 임베딩 처리
│   ├── controller/           # 임베딩 API
│   ├── entity/               # 벡터 엔티티
│   └── service/              # 임베딩 서비스
└── processor/                 # 로그 처리 핵심 모듈
    ├── consumer/             # Kafka 컨슈머
    │   ├── entity/           # 로그 엔티티
    │   ├── mapper/           # 객체 매핑
    │   ├── parser/           # 로그 파서
    │   ├── repository/       # 데이터 액세스
    │   └── service/          # 로그 처리 서비스
    └── schedular/            # 스케줄 작업
```

## 🛠️ 기술 스택

### Backend
- **Java 21** - 최신 LTS 버전
- **Spring Boot 3.5.3** - 메인 프레임워크
- **Spring AI 1.0.0** - AI 모델 연동
- **Spring Kafka 3.3.7** - 메시지 스트림 처리
- **Spring Data JPA** - 데이터 액세스 계층

### Database & Storage
- **PostgreSQL 16** - 메인 데이터베이스
- **PgVector** - 벡터 검색 확장
- **Apache Kafka** - 실시간 스트림 처리

### AI & ML
- **Google Gemini 2.0 Flash** - 텍스트 분석
- **Gemini Embedding 001** - 벡터 임베딩

### DevOps & Tools
- **Docker Compose** - 컨테이너 오케스트레이션
- **Filebeat** - 로그 수집
- **Kafka UI** - Kafka 모니터링
- **MapStruct** - 객체 매핑
- **Lombok** - 코드 생성

## 🔧 환경 설정

### 1. 사전 요구사항
- Java 21
- Docker & Docker Compose
- PostgreSQL 16 (PgVector 확장 포함)

### 2. 테스트 환경 구성
```bash
# 테스트 환경 시작 (Kafka, Zookeeper, 데모앱)
cd test-env
docker-compose up -d
```

**중요**: `test-env/docker-compose.yml`에서 Kafka 설정 수정 필요
```yaml
KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://[YOUR_IP]:9092
```
자세한 설정 방법은 [`test-env/README.md`](test-env/README.md) 참조

### 3. 데이터베이스 설정
```bash
# PostgreSQL 및 PgVector 설치
cd postgresql
docker-compose up -d
```

### 4. 애플리케이션 설정
`src/main/resources/application.yml` 파일 설정:
```yaml
spring:
  datasource:
    username: '사용자명'
    password: '비밀번호'
  ai:
    openai:
      api-key: 'OPENAI_API_KEY'
```

## 🚀 실행 방법

### 1. Gradle 빌드
```bash
./gradlew clean build
```

### 2. 애플리케이션 실행
```bash
java -jar build/libs/app.jar
```

### 3. 서비스 접속
- **메인 애플리케이션**: http://localhost:8084
- **Kafka UI**: http://localhost:8081
- **데모 앱**: http://localhost:8080

## 📊 성능 최적화

### Kafka Consumer 최적화
- **동시 처리**: 10개 스레드 (`concurrency: 10`)
- **배치 처리**: 100개 메시지 배치 (`max-poll-records: 100`)
- **페치 최적화**: 50KB 최소, 1MB 최대 페치 크기
- **연결 풀**: 최대 20개 DB 연결

### 예상 성능
- **처리량**: ~1,000+ msg/sec (기존 100 msg/sec 대비 **10배** 향상)
- **레이턴시**: 배치 처리로 **50%** 감소
- **메모리**: **30%** 사용량 감소

## 📋 API 엔드포인트

### 로그 분석 API
```http
GET /log-analysis?keyword={검색어}&startDate={시작일}&endDate={종료일}
```

**응답 예시:**
```json
{
  "analysisResults": [
    {
      "errorMessage": "Connection timeout",
      "analysis": "데이터베이스 연결 타임아웃 발생...",
      "recommendations": ["연결 풀 크기 증가", "타임아웃 설정 조정"]
    }
  ],
  "similarLogs": [],
  "totalCount": 150
}
```

## 🧪 테스트

```bash
# 단위 테스트 실행
./gradlew test

# 통합 테스트 실행
./gradlew integrationTest
```

## 📝 주요 업데이트

### v0.0.1-SNAPSHOT
- ✅ Kafka 기반 실시간 로그 수집
- ✅ Gemini AI 연동 에러 분석
- ✅ PgVector 기반 유사 로그 검색
- ✅ 배치 처리 성능 최적화
- ✅ 웹 기반 관리 인터페이스
- ✅ Docker 환경 구성 완료