# ParkInsight AI

> 차량 사진 분석 및 AI 기반 차량 관리 코칭 플랫폼

차량 4면 사진을 업로드하면 Gemini Vision이 외관 상태를 진단하고, 날씨 데이터 및 RAG 기반 차량 관리 지식을 결합해 맞춤형 주차/세차 리포트를 생성합니다.

---

## Features

- **차량 사진 4면 분석** — 전면 / 후면 / 좌측 / 우측 업로드
- **Gemini Vision 외관 진단** — 오염도, 스크래치, 주차 정렬, 벽과 거리, 문콕 위험도
- **주차 성향 분석** — 벽 선호형 / 중앙 정렬형 / 문콕 회피형 / 초보 운전자 패턴
- **날씨 기반 세차 추천** — 강수 확률 / 황사 / 미세먼지 연동
- **RAG 기반 차량 관리 Q&A** — 차량 매뉴얼, 세차 가이드, 주차 안전 가이드 검색
- **종합 AI 리포트** — 주차 점수, 세차 필요도, 위험 알림, 관리 일정 제안

---

## Tech Stack

### Frontend
| 항목 | 기술 |
|------|------|
| Framework | React 18 |
| Server State | React Query (TanStack Query v5) |
| UI | MUI v6 (dark theme) |
| HTTP | Axios |

### Backend
| 항목 | 기술 |
|------|------|
| Framework | Spring Boot 3.3.5 |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| ORM | Spring Data JPA |
| DB | MySQL 8 |
| Build | Maven |

### AI / RAG
| 항목 | 기술 |
|------|------|
| Vision & Chat | Google Gemini 1.5 Pro (multimodal) |
| Embedding | Google text-embedding-004 |
| RAG Framework | LangChain4j |
| Vector DB | Qdrant |

### External API
| 항목 | 기술 |
|------|------|
| 날씨 | OpenWeatherMap (`/forecast` + `/air_pollution`) |
| 이미지 저장 | Cloudinary |

---

## Architecture

```
┌────────────────────────────┐
│         React App          │
└────────────┬───────────────┘
             │ REST (JWT)
             ▼
┌────────────────────────────┐
│      Spring Boot API       │
├────────────────────────────┤
│ Auth Service               │
│ Vehicle Service            │
│ Image Analysis Service     │
│ Weather Service            │
│ RAG Service                │
│ Report Service             │
└──────┬─────────┬───────────┘
       │         │
  ┌────┴───┐ ┌───┴──────────────────┐
  │ MySQL  │ │ Qdrant (Vector DB)   │
  └────────┘ └───────────┬──────────┘
                         │ Semantic Search
             ┌───────────┴──────────┐
             │  Gemini 1.5 Pro      │
             │  (Vision + Chat)     │
             └──────────────────────┘
                         │
             ┌───────────┴──────────┐
             │  OpenWeatherMap API  │
             └──────────────────────┘
```

---

## Directory Structure

```
ParkInsight/
├── docker-compose.yml              # MySQL 8 + Qdrant
├── .env.example
├── README.md
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/parking/ai/
│       │   ├── ParkInsightApplication.java
│       │   ├── auth/               # User, JWT, 회원가입/로그인
│       │   ├── vehicle/            # 차량 CRUD
│       │   ├── analysis/           # Vision 분석 오케스트레이터, Cloudinary 업로드
│       │   ├── weather/            # OpenWeatherMap WebClient
│       │   ├── rag/                # RagService, ChatService, ChatController
│       │   ├── report/             # ReportService (Gemini 최종 리포트)
│       │   ├── common/             # ApiResponse, BusinessException, GlobalExceptionHandler
│       │   ├── config/             # SecurityConfig, CloudinaryConfig, QdrantConfig, WebClientConfig
│       │   └── batch/              # RagIngestionRunner (@Profile("ingest"))
│       └── resources/
│           ├── application.yml
│           └── data/
│               └── rag_documents.json  # RAG 학습 문서 (한국어)
└── frontend/
    └── src/
        ├── api/                    # axiosInstance, authApi, vehicleApi, analysisApi, chatApi
        ├── components/             # NavBar, ImageUploadZone, ScoreGauge, LoadingOverlay
        ├── pages/                  # Login, Signup, Vehicles, NewAnalysis, AnalysisResult, History, Chat
        ├── hooks/                  # useAuth
        └── theme.js                # MUI dark theme
```

---

## DB Schema

```sql
-- 사용자
users (id, email, password, name, created_at)

-- 차량
vehicles (id, user_id, model, year, color, mileage, created_at)

-- 분석 결과
analyses (
  id, vehicle_id, user_id,
  front_url, rear_url, left_url, right_url,
  parking_score, wash_score, door_dent_risk,
  weather_snapshot JSON,
  report_text TEXT,
  created_at
)

-- 채팅
chat_messages (id, user_id, vehicle_id, role, content, created_at)
```

---

## API Design

### 인증
```http
POST /api/auth/signup
POST /api/auth/login
```

### 차량
```http
GET    /api/vehicles
POST   /api/vehicles
GET    /api/vehicles/{id}
DELETE /api/vehicles/{id}
```

### 분석
```http
# 분석 요청 (multipart/form-data)
POST /api/analysis
  vehicleId: Long
  front:     File
  rear:      File
  left:      File
  right:     File

# 결과 조회
GET /api/analysis/{id}

# 히스토리
GET /api/analysis?vehicleId={id}
```

### AI 채팅
```http
POST /api/chat
Content-Type: application/json

{
  "vehicleId": 1,
  "question": "자동세차 해도 되나요?"
}
```

---

## Analysis Report Sample

```json
{
  "parkingScore": 84,
  "washScore": 72,
  "doorDentRisk": "HIGH",
  "diagnosis": {
    "contamination": "후면 하단 오염 심각",
    "scratch": "좌측 도어 경미한 스크래치",
    "alignment": "우측 편향 주차, 벽과 거리 30cm 미만",
    "driverPattern": "문콕 회피형"
  },
  "weather": {
    "summary": "3일 후 강수 예정",
    "fineDust": "나쁨",
    "recommendation": "세차 연기 권장"
  },
  "advice": "현재 오염도를 고려하면 세차가 필요하나, 3일 후 비 예보로 인해 주말 이후 세차를 권장합니다."
}
```

---

## Local Setup

### Prerequisites
- Docker Desktop
- JDK 21
- Node.js 20+
- Maven 3.9+

### 1. 인프라 실행
```bash
docker compose up -d
# MySQL 8 → localhost:3306
# Qdrant   → localhost:6333 (HTTP), 6334 (gRPC)
```

### 2. 환경변수 설정
```bash
cp .env.example .env
# 아래 항목 입력
```

| 변수명 | 설명 |
|--------|------|
| `GEMINI_API_KEY` | Google AI Studio API 키 |
| `WEATHER_API_KEY` | OpenWeatherMap API 키 |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary 클라우드명 |
| `CLOUDINARY_API_KEY` | Cloudinary API 키 |
| `CLOUDINARY_API_SECRET` | Cloudinary API 시크릿 |
| `JWT_SECRET` | JWT 서명 키 (32자 이상) |

### 3. 백엔드 실행
```bash
cd backend

# 첫 실행 (테이블 생성)
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-DDDL_AUTO=create"

# RAG 문서 ingestion (최초 1회)
./mvnw spring-boot:run -Dspring.profiles.active=ingest

# 이후 실행
./mvnw spring-boot:run
```

### 4. 프론트엔드 실행
```bash
cd frontend
npm install
npm start
# → http://localhost:3000
```

---

## Deployment

| 서비스 | 플랫폼 |
|--------|--------|
| Spring Boot + MySQL | Railway |
| Qdrant Vector DB | Qdrant Cloud (무료 플랜) |
| React | Vercel |

---

## Future Works

- [ ] 차량 손상 이력 추적 및 시각화
- [ ] 보험 사고 위험 예측 모델
- [ ] AI 세차 일정 자동 관리
- [ ] OBD2 연동 (주행 데이터 통합)
- [ ] 주차 습관 장기 트렌드 분석
- [ ] 차량 사진 비교 (이전/이후)
- [ ] 푸시 알림 (세차 타이밍, 날씨 경보)
