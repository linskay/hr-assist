FROM python:3.10-slim


WORKDIR /app

# Установка системных зависимостей для Coqui TTS
RUN apt-get update && apt-get install -y \
    build-essential \
    ffmpeg \
    espeak \
    espeak-data \
    libespeak1 \
    libespeak-dev \
    libsndfile1 \
    libsndfile1-dev \
    && rm -rf /var/lib/apt/lists/*

# Установка Python зависимостей
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Создаем директорию для кеша моделей
RUN mkdir -p /root/.local/share/tts

COPY main.py .

EXPOSE 8094

# Переменные окружения для Coqui TTS
ENV TTS_CACHE_DIR=/root/.local/share/tts
ENV CUDA_VISIBLE_DEVICES=""

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8094"]
