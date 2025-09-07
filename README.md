![Build](https://img.shields.io/badge/build-passing-brightgreen) ![Java](https://img.shields.io/badge/Java-17-blue) ![Node](https://img.shields.io/badge/Node-18-green) ![Docker](https://img.shields.io/badge/Docker-Compose-informational) ![License](https://img.shields.io/badge/License-MIT-lightgrey) ![SBERT](https://img.shields.io/badge/NLP-SBERT-purple) ![LLM](https://img.shields.io/badge/LLM-llama.cpp-orange) ![DetectGPT](https://img.shields.io/badge/AntiFraud-DetectGPT-red)

# HR Assistant — система проведения интервью с AI

Комплексная платформа для интервью: транскрипция речи, семантический анализ соответствия вакансии, антифрод по тексту и видео, генерация объяснений для рекрутера.

## 🚀 Возможности
- Проведение интервью с записью аудио/видео
- Авто‑транскрипция (WhisperX)
- Семантическое сравнение ответов с требованиями (SBERT)
- Классификация соответствия (скор/уровень + годен/не годен)
- Объяснения LLM: «почему кандидат подходит / не подходит»
- Антифрод: DetectGPT (AI‑текст), DeepFace/MediaPipe (verify/liveness)
- Мониторинг: Prometheus/Grafana и ELK

## 🏗️ Архитектура (компоненты)

```mermaid
graph LR
  subgraph Client
    FE[Frontend (React)]
  end
  subgraph Backend
    BE[Spring Boot /api/v1]
    DB[(Postgres)]
    Cache[(Redis)]
    Storage[(MinIO)]
  end
  subgraph ML Services
    ASR[WhisperX]
    EMB[SBERT /embed]
    LLM[llama.cpp /generate]
    DET[DetectGPT /detect]
    VID[DeepFace+MediaPipe /verify /liveness]
  end
  FE <--> BE
  BE --- DB
  BE --- Cache
  BE --- Storage
  BE <--> ASR
  BE <--> EMB
  BE <--> LLM
  BE <--> DET
  BE <--> VID
```

## 📦 Структура проекта
```
hr-assist/
  backend/                   # Spring Boot API (/api/v1)
  frontend/                  # React UI
  whisperx-service/          # ASR (WhisperX)
  embed-service/             # SBERT all‑MiniLM‑L6‑v2 (/embed,/similarity)
  llm-service/               # llama.cpp (/generate)
  video-antifraud-service/   # DeepFace+MediaPipe (/verify,/liveness)
  detectgpt-service/         # roberta-base-openai-detector (/detect)
  models/                    # локальные веса (опционально)
  monitoring/                # Prometheus/Grafana/ELK
  helm/                      # Helm‑чарты
  docker-compose.yml         # Оркестрация локально
```

## 📋 Требования и порты
- Java 17, Node 18, Docker/Compose
- Ресурсы (рекомендации): CPU 8 vCPU, RAM 16 GB (минимум 8 GB без LLM)
- Порты:
  - Backend: 8081 (контейнер 8080, context‑path `/api/v1`)
  - Frontend: 3000
  - WhisperX: 9100
  - SBERT: 8088
  - LLM: 8090
  - Video antifraud: 8091
  - DetectGPT: 8092
  - MinIO: 9002 (S3), 9003 (console)
  - Grafana / Prometheus: 3001 / 9090, Elasticsearch / Kibana: 9200 / 5601

## ⚙️ Запуск (Docker Compose)
```bash
# Основные сервисы (БД/кеш/очередь/файлы + ML + backend)
docker compose up --build -d postgres redis rabbitmq minio sbert-embed llm detectgpt video-antifraud backend

# Статус
docker compose ps
```
Доступ:
- Backend API: http://localhost:8081/api/v1
- Swagger UI: http://localhost:8081/api/v1/swagger-ui/index.html
- SBERT: http://localhost:8088
- LLM: http://localhost:8090
- DetectGPT: http://localhost:8092
- Video antifraud: http://localhost:8091

## 🔧 Конфигурация (backend)
`backend/src/main/resources/application-dev.properties` (выдержка):
```properties
# Внешние ML сервисы
services.sbert.base-url=http://sbert-embed:8080
services.llm.base-url=http://llm:8090
services.video-antifraud.base-url=http://video-antifraud:8091
services.detectgpt.base-url=http://detectgpt:8092
```

## 🧠 Семантические эмбеддинги (SBERT)
- Сервис: `embed-service` (all‑MiniLM‑L6‑v2)
- Эндпойнты: POST `/embed` → вектор; POST `/similarity` → cosine similarity
- Best practices: хранить векторы вакансий в PostgreSQL + pgvector (или Milvus), кешировать в Redis, лимитировать Nginx

## 📝 Объяснения LLM (llama.cpp)
- Сервис: `llm-service` (Mistral‑7B‑Instruct или LLaMA 2 7B, quantized GGUF 4bit)
- Эндпойнт: POST `/generate`
- Эндпойнт backend: `POST /api/v1/api/nlp/explanation` (формирует промпт и обращается к LLM)
- Best practices: усечение промпта (truncate), локальный volume с моделью (опционально)

## 🛡 DetectGPT (антифрод по тексту)
- Сервис: `detectgpt-service` (модель `roberta-base-openai-detector`)
- Эндпойнт: POST `/detect` → `{ ai_probability }`
- Интеграция: после каждого ответа кандидата вызывать `/detect`; если `>0.8` — помечать «подозрительный» (уведомление без блокировки). Комбинировать со стилистическим анализом речи.

## 🎥 Видео‑антифрод (DeepFace + MediaPipe)
- Сервис: `video-antifraud-service`
- Эндпойнты: POST `/verify` → `{ is_match, confidence }`; POST `/liveness` → `{ is_live, confidence }`
- Best practices: GPU при наличии; хранить только метаданные (boolean/confidence), клипы ≤3 сек для низкой латентности

## 📚 Датасеты
- DetectGPT датасет: `backend/src/main/resources/ml-data/training/ai_vs_human_detectgpt.csv`
  - Формат: `answer_text,label(human|ai),role,grade`
- Видео антифрод: LFW, CelebA, DeepFakeDetection (`video_file,label(real|fake),candidate_id`)
- Эмоции/soft skills: FER2013, AffectNet (`image/video,emotion_label`)

## 🧠 Метки соответствия (Supervised Learning)
Отдельный датасет для обучения классификатора «Подходит/Не подходит»:
```
candidate_id,job_id,similarity_score,decision,explanation
```
Где `decision` ∈ {1,0}. Можно синтетически сгенерировать из CSV кандидатов и вакансий.

## 🔒 Пользователи (DEV)
См. `backend/src/main/resources/data.sql`. Авторизация в Swagger через JWT.

## 📈 Мониторинг
Prometheus/Grafana/ELK (см. `monitoring/`).

## 📜 Лицензия
MIT.

![Build](https://img.shields.io/badge/build-passing-brightgreen) ![Java](https://img.shields.io/badge/Java-17-blue) ![Node](https://img.shields.io/badge/Node-18-green) ![Docker](https://img.shields.io/badge/Docker-Compose-informational) ![License](https://img.shields.io/badge/License-MIT-lightgrey)

# HR Assistant — система проведения интервью с AI

Комплексная платформа для интервью: транскрипция речи, семантический анализ соответствия вакансии, антифрод по тексту и видео, генерация объяснений для рекрутера.

## 🚀 Возможности
- Проведение интервью с записью аудио/видео
- Авто‑транскрипция (WhisperX)
- Семантическое сравнение ответов с требованиями (SBERT)
- Классификация соответствия (скор/уровень + годен/не годен)
- Объяснения LLM: «почему кандидат подходит / не подходит»
- Антифрод: DetectGPT (AI‑текст), DeepFace/MediaPipe (verify/liveness)
- Мониторинг: Prometheus/Grafana и ELK

## 🏗️ Архитектура (высокоуровнево)
```
Frontend (React)  <-->  Backend (Spring Boot, /api/v1)
                             |
                   ---------------------------
                   |           |             |
                 Postgres     Redis        MinIO
                   |                         \
                   |                          \
                Datasets            +  ML‑сервисы (Docker)
                                      WhisperX / SBERT / LLM / DetectGPT / Video
```

## 📦 Структура проекта
```
hr-assist/
  backend/                   # Spring Boot API (/api/v1)
  frontend/                  # React UI
  whisperx-service/          # ASR (WhisperX)
  embed-service/             # SBERT all‑MiniLM‑L6‑v2 (/embed,/similarity)
  llm-service/               # llama.cpp (/generate)
  video-antifraud-service/   # DeepFace+MediaPipe (/verify,/liveness)
  detectgpt-service/         # roberta-base-openai-detector (/detect)
  models/                    # локальные веса (если нужны)
  monitoring/                # Prometheus/Grafana/ELK
  helm/                      # Helm‑чарты
  docker-compose.yml         # Оркестрация локально
```

## 📋 Требования и порты
- Java 17, Node 18, Docker/Compose
- Ресурсы (рекомендации): CPU 8 vCPU, RAM 16 GB (минимум 8 GB без LLM)
- Порты:
  - Backend: 8081 (контейнер 8080, context‑path `/api/v1`)
  - Frontend: 3000
  - WhisperX: 9100
  - SBERT: 8088
  - LLM: 8090
  - Video antifraud: 8091
  - DetectGPT: 8092
  - MinIO: 9002 (S3), 9003 (console)
  - Grafana / Prometheus: 3001 / 9090, Elasticsearch / Kibana: 9200 / 5601

## ⚙️ Запуск (Docker Compose)
```bash
# Основные сервисы (БД/кеш/очередь/файлы + ML + backend)
docker compose up --build -d postgres redis rabbitmq minio sbert-embed llm detectgpt video-antifraud backend

# Статус
docker compose ps
```
Доступ:
- Backend API: http://localhost:8081/api/v1
- Swagger UI: http://localhost:8081/api/v1/swagger-ui/index.html
- SBERT: http://localhost:8088
- LLM: http://localhost:8090
- DetectGPT: http://localhost:8092
- Video antifraud: http://localhost:8091

## 🔧 Конфигурация (backend)
`backend/src/main/resources/application-dev.properties` (выдержка):
```properties
# Внешние ML сервисы
services.sbert.base-url=http://sbert-embed:8080
services.llm.base-url=http://llm:8090
services.video-antifraud.base-url=http://video-antifraud:8091
services.detectgpt.base-url=http://detectgpt:8092
```

## 🧠 Семантические эмбеддинги (SBERT)
- Сервис: `embed-service` (all‑MiniLM‑L6‑v2)
- Эндпойнты: POST `/embed` → вектор; POST `/similarity` → cosine similarity
- Best practices: хранить векторы вакансий в PostgreSQL + pgvector (или Milvus), кешировать в Redis, лимитировать Nginx

## 📝 Объяснения LLM (llama.cpp)
- Сервис: `llm-service` (Mistral‑7B‑Instruct или LLaMA 2 7B, quantized GGUF 4bit)
- Эндпойнт: POST `/generate`
- Эндпойнт backend: `POST /api/v1/api/nlp/explanation` (формирует промпт и обращается к LLM)
- Best practices: усечение промпта (truncate), локальный volume с моделью

## 🛡 DetectGPT (антифрод по тексту)
- Сервис: `detectgpt-service` (модель `roberta-base-openai-detector`)
- Эндпойнт: POST `/detect` → `{ ai_probability }`
- Интеграция: после каждого ответа кандидата вызывать `/detect`; если `>0.8` — помечать «подозрительный» (уведомление без блокировки). Комбинировать со стилистическим анализом речи.

## 🎥 Видео‑антифрод (DeepFace + MediaPipe)
- Сервис: `video-antifraud-service`
- Эндпойнты: POST `/verify` → `{ is_match, confidence }`; POST `/liveness` → `{ is_live, confidence }`
- Best practices: GPU при наличии; хранить только метаданные (boolean/confidence), клипы ≤3 сек для низкой латентности

## 📚 Датасеты
- DetectGPT датасет: `backend/src/main/resources/ml-data/training/ai_vs_human_detectgpt.csv`
  - Формат: `answer_text,label(human|ai),role,grade`
- Видео антифрод: LFW, CelebA, DeepFakeDetection (`video_file,label(real|fake),candidate_id`)
- Эмоции/soft skills: FER2013, AffectNet (`image/video,emotion_label`)

## 🧠 Метки соответствия (Supervised Learning)
Отдельный датасет для обучения классификатора «Подходит/Не подходит»:
```
candidate_id,job_id,similarity_score,decision,explanation
```
Где `decision` ∈ {1,0}. Можно синтетически сгенерировать из CSV кандидатов и вакансий.

## 🔒 Пользователи (DEV)
См. `backend/src/main/resources/data.sql`. Авторизация в Swagger через JWT.

## 📈 Мониторинг
Prometheus/Grafana/ELK (см. `monitoring/`).

## 📜 Лицензия
MIT. См. `LICENSE`.
# HR Assistant - Система проведения интервью с AI

Комплексная система для проведения интервью с кандидатами, включающая автоматическую транскрипцию речи, анализ соответствия требованиям и антифрод проверки.

## 🚀 Возможности

### Основной функционал
- **Проведение интервью** с аудио и видео записью
- **Автоматическая транскрипция** речи в текст (Whisper ONNX)
- **Анализ соответствия** ответов требованиям вакансии
- **Классификация компетенций** (0/0.5/1 балл)
- **Детальные отчеты** с рекомендациями

### Антифрод система
- **Liveness detection** - проверка живости кандидата
- **Face matching** - сравнение лиц
- **Voice verification** - верификация голоса
- **AI text detection** - детекция AI-генерированного текста
- **Browser monitoring** - отслеживание активности браузера
- **Heartbeat tracking** - мониторинг соединения

### Технические особенности
- **Локальные ML модели** (DJL/ONNXRuntime)
- **Асинхронная обработка** (RabbitMQ)
- **Масштабируемое хранилище** (MinIO/S3)
- **Мониторинг и логирование** (Prometheus/Grafana/ELK)
- **Безопасность** (JWT, RBAC, TLS)

## 🏗️ Архитектура

```
Frontend (React)  <-->  Spring Boot (Monolith)
                             |
             -----------------------------------------
             |           |           |              |
         MinIO/S3      Postgres    Redis        DJL + ONNXRuntime
             |                                   (ML модели)
          (storage)                             |
                                            Model Store (/opt/models)
```

## 📋 Требования

### Системные требования
- **Java 17+**
- **Node.js 18+**
- **Docker & Docker Compose**
- **8GB+ RAM** (рекомендуется 16GB для ML моделей)
- **GPU** (опционально, для ускорения ML)

### ML модели
Система использует следующие модели (должны быть размещены в `/opt/models/`):
- `whisper-small.onnx` - ASR транскрипция
- `paraphrase-multilingual-MiniLM-L12-v2.onnx` - эмбеддинги
- `rubert-classifier.onnx` - классификатор соответствия
- `ecapa-tdnn.onnx` - верификация голоса
- `facenet.onnx` - распознавание лиц
- `liveness-detector.onnx` - детекция живости
- `ai-text-detector.onnx` - детекция AI текста

## 🚀 Быстрый старт

### 1. Клонирование репозитория
```bash
git clone <repository-url>
cd hr-assist
```

### 2. Подготовка ML моделей
```bash
# Создайте директорию для моделей
sudo mkdir -p /opt/models

# Скачайте и разместите модели в соответствующих подпапках:
# /opt/models/whisper/whisper-small.onnx
# /opt/models/embeddings/paraphrase-multilingual-MiniLM-L12-v2.onnx
# /opt/models/classifier/rubert-classifier.onnx
# /opt/models/voice-verification/ecapa-tdnn.onnx
# /opt/models/face-recognition/facenet.onnx
# /opt/models/liveness/liveness-detector.onnx
# /opt/models/ai-detector/ai-text-detector.onnx
```

### 3. Запуск с Docker Compose
```bash
# DEV: запуск всех сервисов (без фронтенда)
docker compose up --build -d

# Проверка статуса
docker-compose ps
```

### 4. Доступ к приложению (DEV)
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **WhisperX**: http://localhost:9100/health
- **MinIO Console**: http://localhost:9003 (minioadmin/minioadmin)
- **Grafana**: http://localhost:3001 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Kibana**: http://localhost:5601
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

### 5. Продакшн (Kubernetes)
- Для продакшн используйте Helm-чарты: `helm/hr-assist/`
- Настройте образы в `values.yaml`, PVC для кэша моделей WhisperX, probes.
- Пример: `helm install hr-assist ./helm/hr-assist -n hr --create-namespace`
 - **OpenMaxIO Console (MinIO GUI)**: http://localhost:9090 (см. env в docker-compose)

## 🔧 Конфигурация

### Переменные окружения
Основные настройки в `backend/src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/hr_assistant
spring.datasource.username=hr_user
spring.datasource.password=hr_password

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672

# MinIO
minio.endpoint=http://localhost:9000
minio.access-key=minioadmin
minio.secret-key=minioadmin

# ML Models
ml.models.path=/opt/models
ml.models.whisper=whisper-small.onnx
ml.models.embeddings=paraphrase-multilingual-MiniLM-L12-v2.onnx
# ... другие модели

# Security
jwt.secret=your-secret-key-here
jwt.expiration=86400000
```

### Антифрод настройки
```properties
antifraud.weights.liveness=0.25
antifraud.weights.face-match=0.2
antifraud.weights.voice-match=0.2
antifraud.weights.text-ai=0.2
antifraud.weights.visibility=0.1
antifraud.weights.devtools=0.05
antifraud.threshold.reject=0.85
antifraud.threshold.review=0.6
```

## 📊 API Документация
### Тестовые пользователи (DEV)
В dev-профиле создаются пользователи (см. `backend/src/main/resources/data.sql`).

- ADMIN
  - email: `admin@hr-assistant.com`
  - username: `admin`
  - password: `admin123`
- HR_MANAGER
  - email: `hr@hr-assistant.com`
  - username: `hr_manager`
  - password: `admin123`
- INTERVIEWER
  - email: `interviewer@hr-assistant.com`
  - username: `interviewer`
  - password: `admin123`

Проверьте авторизацию и роли на защищённых эндпоинтах, а также различия доступа.

### Авторизация в Swagger (получение JWT токена)

1) Получить токен (DEV-пользователи выше):

- PowerShell (Windows):
```powershell
$body = @{ email = 'admin@hr-assistant.com'; password = 'admin123' } | ConvertTo-Json
(Invoke-WebRequest -UseBasicParsing http://localhost:8080/api/v1/auth/login -Method POST -ContentType 'application/json' -Body $body).Content
```
Скопируйте значение поля `token` или `accessToken` из ответа.

- Bash (Linux/macOS):
```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"admin@hr-assistant.com","password":"admin123"}'
```

2) В Swagger нажмите Authorize и вставьте:
```
Bearer YOUR_JWT_TOKEN
```


### Основные эндпойнты

#### Аутентификация
- `POST /api/v1/auth/login` - Вход в систему
- `POST /api/v1/auth/logout` - Выход из системы
- `POST /api/v1/auth/refresh` - Обновление токена

#### Вакансии
- `GET /api/v1/vacancies` - Список вакансий
- `POST /api/v1/vacancies` - Создание вакансии
- `GET /api/v1/vacancies/{id}` - Получение вакансии
- `PUT /api/v1/vacancies/{id}` - Обновление вакансии
- `DELETE /api/v1/vacancies/{id}` - Удаление вакансии

#### Интервью
- `GET /api/v1/interviews` - Список интервью
- `POST /api/v1/interviews` - Создание интервью
- `GET /api/v1/interviews/{id}` - Получение интервью
- `POST /api/v1/interviews/{id}/start` - Запуск интервью
- `POST /api/v1/interviews/{id}/questions/{qid}/upload-chunk` - Загрузка медиа
- `POST /api/v1/interviews/{id}/heartbeat` - Heartbeat
- `GET /api/v1/interviews/{id}/results` - Результаты интервью

#### Администрирование
- `POST /api/v1/admin/models/reload` - Перезагрузка моделей
- `GET /api/v1/admin/models/status` - Статус моделей
- `GET /api/v1/admin/logs` - Логи системы
- `DELETE /api/v1/admin/data/{id}` - Удаление данных (GDPR)

## 🔒 Безопасность

### Роли пользователей
- **ADMIN** - полный доступ к системе
- **HR** - управление вакансиями и интервью
- **INTERVIEWER** - проведение интервью
- **REVIEWER** - просмотр результатов

### Защита данных
- **JWT токены** для аутентификации
- **RBAC** для авторизации
- **TLS** для шифрования трафика
- **Шифрование** данных в БД и хранилище
- **GDPR** поддержка удаления данных

## 📈 Мониторинг

### Метрики Prometheus
- `hr_interviews_started_total` - количество запущенных интервью
- `hr_interviews_completed_total` - количество завершенных интервью
- `hr_transcription_duration_seconds` - время транскрипции
- `hr_analysis_duration_seconds` - время анализа
- `hr_fraud_detected_total` - количество обнаруженного мошенничества

### Дашборды Grafana
- **Обзор системы** - основные метрики
- **Производительность ML** - время обработки моделей
- **Антифрод статистика** - статистика мошенничества
- **Инфраструктура** - состояние сервисов

### Логирование ELK
- **Структурированные логи** в JSON формате
- **Централизованное хранение** в Elasticsearch
- **Поиск и анализ** в Kibana
- **Алерты** на критические события

## 🧪 Тестирование

### Подготовка к тестированию

#### 1. Настройка базы данных
```bash
# Создание тестовой базы данных
docker-compose exec postgres psql -U hr_user -c "CREATE DATABASE hr_assistant_test;"

# Запуск миграций
docker-compose exec backend java -jar app.jar --spring.profiles.active=test
```

#### 2. Настройка тестовых данных
```bash
# Копирование тестовых данных
cp env.example .env.test
# Отредактируйте .env.test для тестовой среды
```

### Запуск тестов

#### Backend тесты
```bash
cd backend

# Unit тесты
mvn test

# Интеграционные тесты
mvn test -Dtest=*IntegrationTest

# Тесты с покрытием
mvn test jacoco:report
```

#### Frontend тесты
```bash
cd frontend

# Unit тесты
npm test

# E2E тесты
npm run test:e2e

# Тесты с покрытием
npm run test:coverage
```

#### Интеграционные тесты
```bash
# Запуск тестовой среды
docker-compose -f docker-compose.test.yml up -d

# Запуск тестов
docker-compose -f docker-compose.test.yml exec backend mvn test

# Остановка тестовой среды
docker-compose -f docker-compose.test.yml down
```

### Тестовые данные

#### Создание тестовых пользователей
```bash
# Администратор
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@hr-assistant.com","password":"admin123"}'

# HR менеджер
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"hr@hr-assistant.com","password":"admin123"}'

# Интервьюер
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"interviewer1@hr-assistant.com","password":"admin123"}'
```

#### Создание тестовых вакансий
```bash
# Создание вакансии Java Developer
curl -X POST http://localhost:8080/api/v1/vacancies \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "title": "Senior Java Developer",
    "description": "We are looking for an experienced Java developer",
    "requirements": "5+ years of Java experience, Spring Framework, REST APIs",
    "salaryMin": 80000,
    "salaryMax": 120000,
    "location": "Remote",
    "employmentType": "FULL_TIME",
    "experienceLevel": "SENIOR"
  }'
```

#### Создание тестового интервью
```bash
# Создание интервью
curl -X POST http://localhost:8080/api/v1/interviews \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "candidateName": "Иван Иванов",
    "candidateEmail": "ivan@example.com",
    "candidatePhone": "+7-999-123-45-67",
    "vacancyId": 1
  }'
```

### Тестирование ML моделей

#### Тестирование транскрипции
```bash
# Загрузка тестового аудио
curl -X POST http://localhost:8080/api/v1/interviews/1/questions/1/upload-chunk \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@test-audio.wav" \
  -F "chunkIndex=0" \
  -F "isFinal=true"
```

#### Тестирование антифрода
```bash
# Отправка heartbeat
curl -X POST http://localhost:8080/api/v1/interviews/1/heartbeat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "timestamp": 1640995200000,
    "browserInfo": "Chrome 96.0.4664.110",
    "screenResolution": "1920x1080",
    "timezone": "Europe/Moscow"
  }'
```

### Тестирование производительности

#### Нагрузочное тестирование
```bash
# Установка Apache Bench
sudo apt-get install apache2-utils

# Тест создания интервью
ab -n 100 -c 10 -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -p test-interview.json \
  http://localhost:8080/api/v1/interviews

# Тест получения результатов
ab -n 1000 -c 50 -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/v1/interviews/1/results
```

#### Тестирование ML производительности
```bash
# Тест транскрипции
time curl -X POST http://localhost:8080/api/v1/interviews/1/questions/1/upload-chunk \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@test-audio.wav" \
  -F "isFinal=true"
```

### Автоматизированное тестирование

#### CI/CD Pipeline
```yaml
# .github/workflows/test.yml
name: Tests
on: [push, pull_request]
jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run tests
        run: cd backend && mvn test
      
  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up Node.js
        uses: actions/setup-node@v2
        with:
          node-version: '18'
      - name: Run tests
        run: cd frontend && npm test
```

### Тестовые сценарии

#### 1. Полный цикл интервью
1. Создание интервью
2. Запуск интервью
3. Загрузка аудио/видео ответов
4. Получение результатов анализа
5. Проверка антифрод данных

#### 2. Тестирование безопасности
1. Попытка доступа без авторизации
2. Попытка доступа с неверным токеном
3. Тестирование RBAC
4. Тестирование CORS

#### 3. Тестирование отказоустойчивости
1. Отключение Redis
2. Отключение PostgreSQL
3. Отключение MinIO
4. Перегрузка системы

### Отчеты о тестировании

#### Покрытие кода
```bash
# Backend покрытие
cd backend
mvn jacoco:report
open target/site/jacoco/index.html

# Frontend покрытие
cd frontend
npm run test:coverage
open coverage/lcov-report/index.html
```

#### Метрики качества
- **Покрытие кода**: >80%
- **Время выполнения тестов**: <5 минут
- **Успешность тестов**: 100%
- **Производительность**: <2 сек на запрос

## 🚀 Развертывание в продакшн

### 1. Подготовка сервера
```bash
# Установка Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Установка Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

### 2. Конфигурация продакшн
```bash
# Копирование конфигурации
cp docker-compose.prod.yml docker-compose.yml

# Настройка переменных окружения
cp .env.example .env
# Отредактируйте .env файл
```

### 3. Запуск
```bash
# Запуск в продакшн режиме
docker-compose -f docker-compose.yml up -d

# Проверка логов
docker-compose logs -f backend
```

### 4. SSL сертификаты
```bash
# Использование Let's Encrypt
certbot --nginx -d your-domain.com

# Или самоподписанные сертификаты
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes
```

## 🔧 Устранение неполадок

### Частые проблемы

#### Модели не загружаются
```bash
# Проверка прав доступа
ls -la /opt/models/
sudo chown -R 1000:1000 /opt/models/

# Проверка логов
docker-compose logs backend | grep -i model
```

#### Проблемы с производительностью
```bash
# Мониторинг ресурсов
docker stats

# Увеличение памяти для JVM
export JAVA_OPTS="-Xmx4g -Xms2g"
```

#### Проблемы с сетью
```bash
# Проверка подключений
docker-compose exec backend ping postgres
docker-compose exec backend ping redis
```

### Логи и отладка
```bash
# Логи всех сервисов
docker-compose logs

# Логи конкретного сервиса
docker-compose logs backend
docker-compose logs frontend

# Режим отладки
docker-compose -f docker-compose.debug.yml up
```

## 📄 Лицензия

Этот проект лицензирован под MIT License - см. файл [LICENSE](LICENSE) для деталей.


**HR Assistant** - Современное решение для автоматизации процесса интервью с кандидатами.

## 🎙 WhisperX (ASR) — распознавание речи

Мы используем внешний сервис WhisperX для транскрибации с точными тайм-кодами слов и опциональной диаризацией.

- Репозиторий WhisperX: https://github.com/m-bain/whisperX
- Здоровье сервиса: GET http://localhost:9000/health → {"status":"ok"}

Запуск (CPU):
```
docker compose up --build -d whisperx
```

Использование через наш бэкенд (рекомендуется):
- Эндпоинт: POST /api/asr/transcribe (Swagger описан на русском)
- Формат: multipart/form-data, поле file (wav/mp3/mp4)
- Параметры (опционально): language, model_size (по умолчанию large-v2), batch_size, compute_type (float16|int8), diarize (true|false), min_speakers, max_speakers

Пример запроса:
```
curl -X POST "http://localhost:8080/api/v1/asr/transcribe" \
  -H "Authorization: Bearer YOUR_JWT" \
  -F "file=@test.wav" \
  -F "language=ru" \
  -F "compute_type=int8" \
  -F "diarize=false"
```

Переменные окружения:
- Backend: WHISPERX_URL (dev: http://localhost:9000, prod: http://whisperx:9000)
- Frontend: VITE_WHISPERX_URL (обычно http://localhost:9000)

Заметка о моделях:
- Локальная папка `models/` предназначена для других ML (эмбеддинги, антифрод и т.п.). WhisperX сам загружает свои веса в контейнер; локальные ONNX для Whisper не требуются. Папку можно удалить, если она не используется другими компонентами.

## ▶️ Запуск всех контейнеров одной командой

```bash
docker compose up --build -d
```

Полезные команды:
- Пересобрать и перезапустить: `docker compose up --build -d`
- Остановить: `docker compose down`
- Логи сервиса: `docker compose logs -f backend` (или `whisperx`, `postgres`, `redis`, `minio`)

## 🧠 Семантические эмбеддинги (SBERT) и объяснения (LLM)

### SBERT Embedding Service
- Модель: `sentence-transformers/all-MiniLM-L6-v2`
- Порт: 8088 (хост) → 8080 (контейнер)
- Эндпойнты:
  - POST `/embed` → `{ "embedding": number[] }`
  - POST `/similarity` → `{ "similarity": number }`
- Рекомендации: хранить векторы вакансий в PostgreSQL с pgvector, кеш в Redis, лимитировать API через Nginx.

Пример (Windows PowerShell):
```powershell
$headers = @{ 'Content-Type' = 'application/json' }
$body = '{"text1":"Java Spring","text2":"Spring Boot developer"}'
Invoke-RestMethod -Uri http://localhost:8088/similarity -Method Post -Headers $headers -Body $body
```

### LLM Service (llama.cpp)
- Модель: Mistral-7B-Instruct или LLaMA 2 7B (quantized GGUF, 4bit)
- Порт: 8090 (хост) → 8090 (контейнер)
- Эндпойнт: POST `/generate` → `{ "output": string }`
- Локальное хранение модели на volume `./models/llm:/models:ro`

Пример запроса (Windows PowerShell):
```powershell
$headers = @{ 'Content-Type' = 'application/json' }
$body = '{"prompt":"Вакансия: Senior Java Developer\nОтвет кандидата: \"5 лет Java, Spring Boot, Kafka\"\nТребования: \"3+ лет Java, Spring, Kafka, Docker\"\nСформируй объяснение, подходит ли кандидат."}'
Invoke-RestMethod -Uri http://localhost:8090/generate -Method Post -Headers $headers -Body $body
```

### Эндпойнт бэкенда для объяснений
- POST `/api/nlp/explanation` — принимает `vacancy`, `answer`, `requirements` и возвращает объяснение (Swagger описание на русском).

### Системные требования (рекомендации)
- SBERT (`sbert-embed`): CPU 1 vCPU, RAM 2 GB
- LLM (`llm`): CPU 6–8 vCPU, RAM 8–12 GB (7B Q4_K_M)
- Backend: CPU 1–2 vCPU, RAM 1–2 GB
- Порты: Backend 8081, SBERT 8088, LLM 8090 — конфликтов нет

## 🛡 DetectGPT (антифрод по тексту)

- Сервис: `detectgpt` (FastAPI)
- Модель: `roberta-base-openai-detector`
- Порт: 8092
- Эндпойнт: POST `/detect` → `{ ai_probability: number }`
- Интеграция: после каждого ответа кандидата вызывать `/detect`; если `ai_probability > 0.8`, помечать ответ как подозрительный (уведомление рекрутеру, без жёсткой блокировки). Рекомендуется комбинировать с анализом стиля речи.

## 🧠 Метки соответствия (Supervised Learning)

Отдельный датасет для обучения классификатора «Подходит/Не подходит»:
```
candidate_id,job_id,similarity_score,decision,explanation
```
Где `decision` ∈ {1,0}. Данные можно синтетически сгенерировать из CSV с кандидатами и вакансиями.

## 📚 Датасеты

- DetectGPT: `backend/src/main/resources/ml-data/training/ai_vs_human_detectgpt.csv` (формат: `answer_text,label(human|ai),role,grade`) — для обучения детектора «скриптовых» ответов.
- Видео-антифрод: LFW, CelebA, DeepFakeDetection (формат: `video_file,label(real|fake),candidate_id`).
- Эмоции и soft skills: FER2013, AffectNet (формат: `image/video,emotion_label`).

## 🎥 Антифрод по видео (DeepFace + MediaPipe)

- Сервис: `video-antifraud` (FastAPI)
- Порт: 8091
- Эндпойнты:
  - POST `/verify` — принимает фото/видео (multipart `file`), возвращает `{ is_match: boolean, confidence: number }`
  - POST `/liveness` — принимает короткое видео (multipart `file`), возвращает `{ is_live: boolean, confidence: number }`
- Best practices:
  - Использовать GPU, если доступно (nvidia-docker, CUDA базовый образ)
  - Сохранять только метаданные проверки (true/false, confidence, timestamps), не хранить полный ролик
  - Минимизировать latency до ~3 сек (выбирать низкую частоту кадров/короткие клипы)