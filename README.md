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

## 📋 Системные требования

### Аппаратные требования
Для запуска этого проекта требуется значительный объем ресурсов, особенно при одновременной работе всех ML-сервисов.

#### Минимальная конфигурация (только backend, DB, и 1-2 ML сервиса)
- **CPU**: 4+ ядер (Intel i5 / AMD Ryzen 5)
- **RAM**: 8+ GB
- **Диск**: 20 GB свободного места (SSD рекомендуется)

#### Рекомендуемая конфигурация (все сервисы для разработки)
- **CPU**: 8+ ядер (Intel i7 / AMD Ryzen 7)
- **RAM**: 16+ GB
- **Диск**: 50+ GB свободного места (SSD обязательно)
- **GPU**: NVIDIA RTX 3060+ (опционально, для ускорения ML-моделей)

#### Профессиональная конфигурация (для production-like окружения)
- **CPU**: 16+ ядер (Intel i9 / AMD Ryzen 9 / Xeon)
- **RAM**: 32+ GB
- **Диск**: 100+ GB NVMe SSD
- **GPU**: NVIDIA RTX 4080+ / A100 (для ML-ускорения)

### Программные требования
- **Java 17+**
- **Node.js 18+**
- **Docker & Docker Compose**
- **Git**

### Используемые порты
Убедитесь, что следующие порты свободны на вашей машине:
- **Frontend**: `3000`
- **Backend**: `8081`
- **PostgreSQL**: `5432`
- **Redis**: `6379`
- **RabbitMQ**: `5672` (AMQP), `15672` (Management UI)
- **MinIO**: `9002` (API), `9003` (Console)
- **WhisperX**: `9100`
- **SBERT**: `8088`
- **LLM**: `8090`
- **Video Antifraud**: `8091`
- **DetectGPT**: `8092`
- **Avatar**: `8093`
- **TTS**: `8094`
- **RuBERT**: `8095`
- **Prometheus**: `9090`
- **Grafana**: `3001`
- **Elasticsearch**: `9200`
- **Kibana**: `5601`

### Производительность ML-сервисов (ориентировочно)

| Сервис             | CPU (ядра) | RAM (GB) | Время обработки (на CPU)   | GPU Ускорение |
|--------------------|:----------:|:--------:|----------------------------|:-------------:|
| **WhisperX**       |    2-4     |   2-4    | ~2-5 сек / 1 мин аудио     |      Да       |
| **SBERT**          |    1-2     |   1-2    | ~100-500 мс / запрос       |      Да       |
| **LLM (Mistral-7B)** |    6-8     |   8-12   | ~2-10 сек / запрос         |    **x3-5**   |
| **RuBERT**         |    2-4     |   2-4    | ~0.5-2 сек / запрос        |      Да       |
| **DetectGPT**      |    1-2     |   1-2    | ~200-800 мс / запрос       |      Да       |
| **Video Antifraud**|    4-6     |   4-6    | ~3-8 сек / видео           | **Рекомендуется** |
| **TTS (Coqui)**    |    2-4     |   2-4    | ~1-3 сек / предложение     |      Нет      |
| **Avatar (SadTalker)**|    4-6     |   4-8    | ~10-30 сек / аватар        | **Рекомендуется** |

### Скрипты проверки системы

#### Windows (PowerShell)
```powershell
# Проверка версий
java -version
node --version
docker --version
docker-compose --version

# Проверка свободного места на диске C:
Get-WmiObject -Class Win32_LogicalDisk -Filter "DeviceID='C:'" | Select-Object @{Name="FreeSpace(GB)";Expression={[math]::Round($_.FreeSpace/1GB,2)}}

# Проверка общего объема RAM
(Get-WmiObject -Class Win32_ComputerSystem).TotalPhysicalMemory / 1GB
```

#### Linux / macOS (Bash)
```bash
# Проверка версий
java -version
node --version
docker --version
docker-compose --version

# Проверка свободного места
df -h /

# Проверка RAM
free -h  # Linux
sysctl -n hw.memsize | awk '{print $0/1073741824 " GB"}' # macOS

# Проверка количества ядер CPU
nproc # Linux
sysctl -n hw.ncpu # macOS
```

## ⚙️ Запуск и установка

> **Примечание**: `docker-compose` предназначен для быстрой настройки **локального dev-окружения**. Все сервисы запускаются с профилем `dev`.

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
> **Примечание**: Kubernetes (через Helm) является рекомендуемым способом развертывания системы в **production-окружении**.

Платформа готова к развертыванию в Kubernetes с помощью предоставленных Helm-чартов.
```bash
# Установка
helm install hr-assist ./helm/hr-assist -n hr --create-namespace

# Обновление
helm upgrade hr-assist ./helm/hr-assist -n hr
```

## 📄 Лицензия
Этот проект распространяется под лицензией MIT. См. файл `LICENSE` для получения дополнительной информации.
