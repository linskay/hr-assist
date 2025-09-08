FROM python:3.10-slim

ENV PYTHONUNBUFFERED=1 \
    PIP_NO_CACHE_DIR=1 \
    TRANSFORMERS_CACHE=/root/.cache/huggingface \
    HF_HOME=/root/.cache/huggingface \
    TORCH_HOME=/root/.cache/torch

RUN apt-get update && apt-get install -y --no-install-recommends \
        git \
        ffmpeg \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements.txt ./
RUN pip install --upgrade pip && pip install -r requirements.txt

COPY main.py ./

ENV TTS_OUTPUT_DIR=/data/tts \
    TTS_MODEL=tts_models/ru/v3_1/fast_pitch \
    PORT=8100

EXPOSE 8100

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8100"]


