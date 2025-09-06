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
# Запуск всех сервисов
docker-compose up -d

# Проверка статуса
docker-compose ps
```

### 4. Доступ к приложению
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Grafana**: http://localhost:3001 (admin/admin)
- **Prometheus**: http://localhost:9090
- **Kibana**: http://localhost:5601
- **RabbitMQ Management**: http://localhost:15672 (guest/guest)
- **MinIO Console**: http://localhost:9001 (minioadmin/minioadmin)

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

### Запуск тестов
```bash
# Backend тесты
cd backend
mvn test

# Frontend тесты
cd frontend
npm test

# Интеграционные тесты
docker-compose -f docker-compose.test.yml up --abort-on-container-exit
```

### Тестовые данные
```bash
# Создание тестового пользователя
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123","role":"ADMIN"}'
```

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

## 📚 Дополнительная документация

- [API Reference](docs/api.md)
- [ML Models Guide](docs/models.md)
- [Security Guide](docs/security.md)
- [Monitoring Guide](docs/monitoring.md)
- [Troubleshooting](docs/troubleshooting.md)

## 🤝 Вклад в проект

1. Fork репозитория
2. Создайте feature branch (`git checkout -b feature/amazing-feature`)
3. Commit изменения (`git commit -m 'Add amazing feature'`)
4. Push в branch (`git push origin feature/amazing-feature`)
5. Откройте Pull Request

## 📄 Лицензия

Этот проект лицензирован под MIT License - см. файл [LICENSE](LICENSE) для деталей.

## 📞 Поддержка

- **Email**: support@hr-assistant.com
- **Документация**: https://docs.hr-assistant.com
- **Issues**: https://github.com/your-org/hr-assistant/issues

---

**HR Assistant** - Современное решение для автоматизации процесса интервью с кандидатами.