# HR Assistant — система проведения интервью с AI

Комплексная платформа для интервью: транскрипция речи, семантический анализ соответствия вакансии, антифрод по тексту и видео, генерация объяснений для рекрутера.

## 🚀 Возможности

- **Проведение интервью** с записью аудио/видео
- **Авто-транскрипция** речи (WhisperX)
- **Семантическое сравнение** ответов с требованиями (SBERT)
- **Классификация соответствия** (RuBERT)
- **Объяснения LLM** (Mistral-7B)
- **Антифрод**: DetectGPT (AI-текст), DeepFace/MediaPipe (verify/liveness)
- **TTS синтез** русской речи (Coqui TTS)
- **Аватар сервис** (SadTalker/Kokoro)

## 🧠 ML Модели и Сервисы

### Внешние ML сервисы (Docker)
- **WhisperX** (порт 9100) - транскрипция речи
- **SBERT** (порт 8088) - семантические эмбеддинги
- **LLM** (порт 8090) - генерация объяснений (Mistral-7B)
- **RuBERT** (порт 8095) - анализ русского текста
- **DetectGPT** (порт 8092) - детекция AI-текста
- **Video Antifraud** (порт 8091) - верификация лиц и живости
- **TTS** (порт 8094) - синтез русской речи (Coqui TTS)
- **Avatar** (порт 8093) - генерация аватара (SadTalker)

### Данные обучения
- **Классификатор соответствия**: `it_dataset_combined_10000.csv` (10,000 записей)
- **Детекция живости**: `liveness_detection.csv` (60 записей)
- **Детекция AI**: `ai_text_detection.csv` (40 записей)
- **Антифрод данные**: `antifraud_data.csv` (30 записей)

## 🏗️ Архитектура

```
Frontend (React)  <-->  Backend (Spring Boot)  <-->  ML Services (Docker)
                             |                           |
                    ---------------------------    ---------------------
                    |           |             |    |                   |
                 Postgres     Redis        MinIO   WhisperX  SBERT   LLM
                                                      |        |      |
                                                 RuBERT  DetectGPT  TTS
```

## 📦 Структура проекта

```
hr-assist/
  backend/                   # Spring Boot API (/api/v1)
  frontend/                  # React UI
  whisperx-service/          # ASR (WhisperX)
  embed-service/             # SBERT эмбеддинги
  llm-service/               # LLM (Mistral-7B)
  rubert-service/            # RuBERT анализ текста
  detectgpt-service/         # DetectGPT антифрод
  video-antifraud-service/   # DeepFace+MediaPipe
  tts-service/               # Coqui TTS
  avatar-service/            # SadTalker аватар
  models/                    # локальные веса (опционально)
  monitoring/                # Prometheus/Grafana/ELK
  helm/                      # Helm-чарты
  docker-compose.yml         # Оркестрация
```

## 📋 Требования

### Системные требования
- **Java 17+** (OpenJDK или Oracle JDK)
- **Node.js 18+** (для frontend разработки)
- **Docker 20.10+** и **Docker Compose 2.0+**
- **Git** (для клонирования репозитория)

### Аппаратные требования

#### Минимальная конфигурация (только backend + база данных)
- **CPU**: 4 ядра (Intel i5/AMD Ryzen 5 или лучше)
- **RAM**: 8 GB
- **Диск**: 20 GB свободного места (SSD рекомендуется)
- **Сеть**: стабильное интернет-соединение для загрузки Docker образов

#### Рекомендуемая конфигурация (все ML сервисы)
- **CPU**: 8+ ядер (Intel i7/AMD Ryzen 7 или лучше)
- **RAM**: 16+ GB
- **Диск**: 50+ GB свободного места (SSD обязательно)
- **GPU**: NVIDIA RTX 3060+ (опционально, для ускорения ML)
- **Сеть**: стабильное интернет-соединение

#### Профессиональная конфигурация (продакшн-подобная)
- **CPU**: 16+ ядер (Intel i9/AMD Ryzen 9 или Xeon/EPYC)
- **RAM**: 32+ GB
- **Диск**: 100+ GB NVMe SSD
- **GPU**: NVIDIA RTX 4080+ или A100 (для ML ускорения)
- **Сеть**: гигабитное соединение

### Требования к операционной системе

#### Windows
- **Windows 10/11** (64-bit)
- **WSL2** (рекомендуется для лучшей производительности Docker)
- **Docker Desktop** с включенным WSL2 backend
- **PowerShell 5.1+** или **PowerShell Core 7+**

#### macOS
- **macOS 10.15+** (Catalina или новее)
- **Docker Desktop** для Mac
- **Homebrew** (для установки зависимостей)

#### Linux
- **Ubuntu 20.04+**, **CentOS 8+**, **RHEL 8+** или аналогичные
- **Docker Engine** и **Docker Compose**
- **curl**, **wget**, **git**

### Порты (должны быть свободны)
- **8081** - Backend API
- **3000** - Frontend
- **5432** - PostgreSQL
- **6379** - Redis
- **5672** - RabbitMQ
- **9002-9003** - MinIO
- **8088** - SBERT
- **8090** - LLM
- **8091** - Video Antifraud
- **8092** - DetectGPT
- **8093** - Avatar
- **8094** - TTS
- **8095** - RuBERT
- **9100** - WhisperX
- **9090** - Prometheus
- **3001** - Grafana
- **9200** - Elasticsearch
- **5601** - Kibana
- **15672** - RabbitMQ Management

### Производительность ML сервисов

#### WhisperX (транскрипция)
- **CPU**: 2-4 ядра
- **RAM**: 2-4 GB
- **Время обработки**: ~2-5 сек на 1 минуту аудио

#### SBERT (эмбеддинги)
- **CPU**: 1-2 ядра
- **RAM**: 1-2 GB
- **Время обработки**: ~100-500 мс на запрос

#### LLM (Mistral-7B)
- **CPU**: 6-8 ядер
- **RAM**: 8-12 GB
- **Время обработки**: ~2-10 сек на запрос
- **GPU**: RTX 3060+ (ускоряет в 3-5 раз)

#### RuBERT (анализ текста)
- **CPU**: 2-4 ядра
- **RAM**: 2-4 GB
- **Время обработки**: ~500 мс - 2 сек на запрос

#### DetectGPT (антифрод)
- **CPU**: 1-2 ядра
- **RAM**: 1-2 GB
- **Время обработки**: ~200-800 мс на запрос

#### Video Antifraud
- **CPU**: 4-6 ядер
- **RAM**: 4-6 GB
- **GPU**: RTX 3060+ (рекомендуется)
- **Время обработки**: ~3-8 сек на видео

#### TTS (Coqui TTS)
- **CPU**: 2-4 ядра
- **RAM**: 2-4 GB
- **Время обработки**: ~1-3 сек на предложение

#### Avatar (SadTalker)
- **CPU**: 4-6 ядер
- **RAM**: 4-8 GB
- **GPU**: RTX 3060+ (рекомендуется)
- **Время обработки**: ~10-30 сек на аватар

### Рекомендации по оптимизации

#### Для слабых машин
```bash
# Запуск только основных сервисов
docker-compose up -d postgres redis minio backend

# Запуск ML сервисов по одному
docker-compose up -d whisperx
docker-compose up -d sbert-embed
# и т.д.
```

#### Для мощных машин
```bash
# Запуск всех сервисов одновременно
docker-compose up --build -d

# Мониторинг ресурсов
docker stats
```

#### Настройка Docker
```bash
# Увеличение памяти для Docker Desktop
# Windows/macOS: Settings -> Resources -> Memory -> 8GB+

# Linux: настройка cgroups
echo 'GRUB_CMDLINE_LINUX="cgroup_enable=memory swapaccount=1"' >> /etc/default/grub
sudo update-grub
```

### Проверка готовности системы

#### Скрипт проверки (Windows PowerShell)
```powershell
# Проверка Java
java -version

# Проверка Node.js
node --version

# Проверка Docker
docker --version
docker-compose --version

# Проверка свободного места
Get-WmiObject -Class Win32_LogicalDisk | Select-Object DeviceID, @{Name="Size(GB)";Expression={[math]::Round($_.Size/1GB,2)}}, @{Name="FreeSpace(GB)";Expression={[math]::Round($_.FreeSpace/1GB,2)}}

# Проверка RAM
Get-WmiObject -Class Win32_ComputerSystem | Select-Object TotalPhysicalMemory
```

#### Скрипт проверки (Linux/macOS)
```bash
# Проверка Java
java -version

# Проверка Node.js
node --version

# Проверка Docker
docker --version
docker-compose --version

# Проверка свободного места
df -h

# Проверка RAM
free -h

# Проверка CPU
nproc
lscpu
```

## ⚙️ Запуск

### Быстрый старт (все сервисы)
```bash
# Клонирование
git clone <repository-url>
cd hr-assist

# Запуск всех сервисов
docker-compose up --build -d

# Проверка статуса
docker-compose ps
```

### Запуск отдельных сервисов
```bash
# Только база данных и инфраструктура
docker-compose up -d postgres redis rabbitmq minio

# ML сервисы
docker-compose up -d whisperx sbert-embed llm rubert detectgpt video-antifraud tts avatar

# Backend
docker-compose up -d backend

# Frontend
docker-compose up -d frontend
```

## 🌐 Доступ к сервисам

- **Backend API**: http://localhost:8081/api/v1
- **Swagger UI**: http://localhost:8081/api/v1/swagger-ui/index.html
- **Frontend**: http://localhost:3000
- **WhisperX**: http://localhost:9100
- **SBERT**: http://localhost:8088
- **LLM**: http://localhost:8090
- **RuBERT**: http://localhost:8095
- **DetectGPT**: http://localhost:8092
- **Video Antifraud**: http://localhost:8091
- **TTS**: http://localhost:8094
- **Avatar**: http://localhost:8093

## 🔧 Конфигурация

### Backend (application-dev.properties)
```properties
# Внешние ML сервисы
WHISPERX_URL=http://whisperx:9000
SBERT_URL=http://sbert-embed:8080
LLM_URL=http://llm:8090
RUBERT_URL=http://rubert:8095
DETECTGPT_URL=http://detectgpt:8092
VIDEO_ANTIFRAUD_URL=http://video-antifraud:8091
TTS_URL=http://tts:8094
AVATAR_URL=http://avatar:8093

# Отключение локальных ML моделей (используем внешние сервисы)
ml.models.whisper=
ml.models.embeddings=
ml.models.classifier=
ml.models.voice-verification=
ml.models.face-recognition=
ml.models.liveness=
ml.models.ai-detector=
```

## 🧪 Тестирование

### Тестовые пользователи (DEV)
- **ADMIN**: `admin@hr-assistant.com` / `admin123`
- **HR**: `hr@hr-assistant.com` / `admin123`
- **INTERVIEWER**: `interviewer@hr-assistant.com` / `admin123`

### API тестирование
```bash
# Получение JWT токена
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@hr-assistant.com","password":"admin123"}'

# Тест транскрипции
curl -X POST http://localhost:8081/api/v1/asr/transcribe \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -F "file=@test.wav"

# Тест обучения моделей
curl -X POST http://localhost:8081/api/v1/admin/ml/train \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## 📊 Мониторинг

- **Grafana**: http://localhost:3001 (admin/admin)
- **Prometheus**: http://localhost:9090
- **MinIO Console**: http://localhost:9003 (minioadmin/minioadmin)
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)

## 🚀 Продакшн

### Kubernetes (Helm)
```bash
# Установка через Helm
helm install hr-assist ./helm/hr-assist -n hr --create-namespace

# Обновление
helm upgrade hr-assist ./helm/hr-assist -n hr
```

### Переменные окружения
```bash
# Копирование примера
cp env.example .env

# Редактирование конфигурации
nano .env
```

## 🔒 Безопасность

- **JWT токены** для аутентификации
- **RBAC** для авторизации (ADMIN, HR, INTERVIEWER)
- **TLS** для шифрования трафика
- **GDPR** поддержка удаления данных

## 📈 Производительность

- **Транскрипция**: ~2-5 сек на 1 минуту аудио
- **Анализ соответствия**: ~1-2 сек на ответ
- **Антифрод проверки**: ~3-5 сек на видео
- **TTS синтез**: ~1-3 сек на предложение

## 🛠️ Устранение неполадок

### Частые проблемы
```bash
# Проверка логов
docker-compose logs -f backend
docker-compose logs -f whisperx

# Перезапуск сервиса
docker-compose restart backend

# Очистка и пересборка
docker-compose down
docker-compose up --build -d
```

### Проверка здоровья сервисов
```bash
# Проверка всех ML сервисов
curl http://localhost:9100/health  # WhisperX
curl http://localhost:8088/health  # SBERT
curl http://localhost:8090/health  # LLM
curl http://localhost:8095/health  # RuBERT
curl http://localhost:8092/health  # DetectGPT
curl http://localhost:8091/health  # Video Antifraud
curl http://localhost:8094/health  # TTS
curl http://localhost:8093/health  # Avatar
```

## 📄 Лицензия

MIT License - см. файл [LICENSE](LICENSE) для деталей.

---

**HR Assistant** - Современное решение для автоматизации процесса интервью с кандидатами с использованием AI.