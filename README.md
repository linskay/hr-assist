# HR Assistant — AI-Powered Interview Platform

**Комплексная Open-Source платформа для автоматизации технических интервью с использованием AI.**

Этот проект представляет собой сложную микросервисную систему, предназначенную для проведения, анализа и оценки собеседований. Она включает в себя транскрипцию речи, семантический анализ, антифрод-механизмы и генерацию ИИ-объяснений для рекрутеров.

![Java](https://img.shields.io/badge/Java-17-blue.svg?style=for-the-badge&logo=openjdk)
![Spring](https://img.shields.io/badge/Spring_Boot-3.2-success.svg?style=for-the-badge&logo=spring)
![Python](https://img.shields.io/badge/Python-3.9-blue.svg?style=for-the-badge&logo=python)
![React](https://img.shields.io/badge/React-18-blue.svg?style=for-the-badge&logo=react)
![Docker](https://img.shields.io/badge/Docker-blue.svg?style=for-the-badge&logo=docker)
![Kubernetes](https.img.shields.io/badge/Kubernetes-blue.svg?style=for-the-badge&logo=kubernetes)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg?style=for-the-badge&logo=postgresql)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-red.svg?style=for-the-badge&logo=rabbitmq)
![Redis](https://img.shields.io/badge/Redis-red.svg?style=for-the-badge&logo=redis)
![MinIO](https://img.shields.io/badge/MinIO-red.svg?style=for-the-badge&logo=minio)
![Prometheus](https://img.shields.io/badge/Prometheus-orange.svg?style=for-the-badge&logo=prometheus)
![Grafana](https://img.shields.io/badge/Grafana-orange.svg?style=for-the-badge&logo=grafana)
![ELK](https://img.shields.io/badge/ELK_Stack-orange.svg?style=for-the-badge&logo=elasticsearch)
![License](https://img.shields.io/badge/License-MIT-green.svg?style=for-the-badge)

---

> ### ⚠️ **Важное замечание**
> Этот проект в первую очередь является демонстрацией **масштабируемой, событийно-ориентированной архитектуры** и **конвейера обработки данных (data pipeline)** для сложных AI/ML задач. Фронтенд-часть (UI) является функциональной, но не была в фокусе разработки. Основное внимание уделено бэкенду, микросервисам, инфраструктуре и обработке данных.

## 🚀 Ключевые возможности

- **Проведение интервью**: Запись аудио и видео кандидатов.
- **Авто-транскрипция**: Высокоточная расшифровка речи с помощью **WhisperX**.
- **Семантический анализ**: Сравнение ответов кандидата с требованиями вакансии через **SBERT**.
- **Классификация**: Оценка соответствия кандидата с помощью **RuBERT**.
- **AI-объяснения**: Генерация развернутых отчетов для HR с помощью **LLM (Mistral-7B)**.
- **Антифрод-система**:
  - **Текст**: Детекция текста, сгенерированного ИИ (**DetectGPT**).
  - **Видео**: Верификация личности и детекция "живости" (**DeepFace/MediaPipe**).
- **Синтез речи**: Озвучивание текста на русском языке (**Coqui TTS**).
- **Генерация аватара**: Создание анимированного аватара по фото (**SadTalker**).

## 🏗️ Архитектура системы

Диаграмма ниже иллюстрирует взаимодействие между компонентами системы.

```mermaid
graph TD
    subgraph " "
        direction LR
        A[User]
    end

    subgraph "User Interface"
        direction LR
        Frontend[Frontend<br/>(React, Vite)]
    end

    subgraph "Core Backend"
        direction LR
        Backend[Backend API<br/>(Spring Boot, Java 17)]
    end

    subgraph "Data & Messaging"
        Postgres[(PostgreSQL)]
        Redis[(Redis)]
        MinIO[(MinIO S3)]
        RabbitMQ[(RabbitMQ)]
    end

    subgraph "AI/ML Microservices (Python)"
        WhisperX[WhisperX<br/>ASR]
        SBERT[SBERT<br/>Embeddings]
        LLM[LLM<br/>Mistral-7B]
        RuBERT[RuBERT<br/>Classifier]
        DetectGPT[DetectGPT<br/>AI Text]
        VideoAntifraud[Video Antifraud<br/>Face/Liveness]
        TTS[Coqui TTS<br/>Speech Synthesis]
        Avatar[SadTalker<br/>Avatar Gen]
    end

    subgraph "Monitoring & Observability"
        direction LR
        Prometheus[Prometheus]
        Grafana[Grafana]
        ELK[ELK Stack<br/>(Elastic, Logstash, Kibana)]
    end

    A --> Frontend
    Frontend <--> Backend

    Backend --> Postgres
    Backend --> Redis
    Backend --> MinIO
    Backend --> RabbitMQ

    Backend --> WhisperX
    Backend --> SBERT
    Backend --> LLM
    Backend --> RuBERT
    Backend --> DetectGPT
    Backend --> VideoAntifraud
    Backend --> TTS
    Backend --> Avatar

    Backend --> Prometheus
    Backend --> ELK
    WhisperX --> Prometheus
    SBERT --> Prometheus
    LLM --> Prometheus
    RuBERT --> Prometheus
    DetectGPT --> Prometheus
    VideoAntifraud --> Prometheus
    TTS --> Prometheus
    Avatar --> Prometheus

    Prometheus --> Grafana
```

## 📦 Структура проекта

```
hr-assist/
  backend/                   # Spring Boot API (Java 17)
  frontend/                  # React UI (TypeScript, Vite)
  avatar-service/            # SadTalker аватар
  detectgpt-service/         # DetectGPT антифрод
  embed-service/             # SBERT эмбеддинги
  llm-service/               # LLM (Mistral-7B)
  rubert-service/            # RuBERT анализ текста
  tts-service/               # Coqui TTS
  video-antifraud-service/   # DeepFace + MediaPipe
  whisperx-service/          # ASR (WhisperX)
  models/                    # ML модели (ONNX, GGUF, etc.)
  monitoring/                # Prometheus, Grafana, ELK
  helm/                      # Helm-чарты для Kubernetes
  docker-compose.yml         # Оркестрация для локального запуска
```

## ⚙️ Запуск и установка

### Требования
- **Java 17+**
- **Node.js 18+**
- **Docker & Docker Compose**
- **Git**

### Быстрый старт
```bash
# 1. Клонировать репозиторий
git clone <repository-url>
cd hr-assist

# 2. Создать .env файл из примера
cp env.example .env

# 3. Запустить все сервисы
docker-compose up --build -d

# 4. Проверить статус контейнеров
docker-compose ps
```

### Доступ к сервисам
- **Frontend**: `http://localhost:3000`
- **Backend API**: `http://localhost:8081/api/v1`
- **Swagger UI**: `http://localhost:8081/api/v1/swagger-ui/index.html`
- **Grafana**: `http://localhost:3001` (admin/admin)
- **Prometheus**: `http://localhost:9090`
- **MinIO Console**: `http://localhost:9003` (minioadmin/minioadmin)
- **RabbitMQ**: `http://localhost:15672` (guest/guest)
- **Kibana**: `http://localhost:5601`

## 🧪 Тестирование

### Тестовые пользователи
- **ADMIN**: `admin@hr-assistant.com` / `admin123`
- **HR**: `hr@hr-assistant.com` / `admin123`

### Пример API запроса (через cURL)
```bash
# 1. Получить JWT токен
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@hr-assistant.com","password":"admin123"}' | jq -r .token)

# 2. Тест транскрипции
curl -X POST http://localhost:8081/api/v1/asr/transcribe \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@path/to/your/test.wav"
```

## 🚀 Развертывание в Kubernetes
Платформа готова к развертыванию в Kubernetes с помощью предоставленных Helm-чартов.
```bash
# Установка
helm install hr-assist ./helm/hr-assist -n hr --create-namespace

# Обновление
helm upgrade hr-assist ./helm/hr-assist -n hr
```

## 📄 Лицензия
Этот проект распространяется под лицензией MIT. См. файл `LICENSE` для получения дополнительной информации.
