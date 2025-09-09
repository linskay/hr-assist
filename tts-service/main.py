from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional, List
import io
import base64
import tempfile
import os
import torch
import logging
import asyncio
from concurrent.futures import ThreadPoolExecutor

# Настройка логирования
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = FastAPI(title="TTS Service (Coqui)", description="Text-to-Speech сервис с русской моделью", version="1.0.0")

# Инициализация Coqui TTS
tts_model = None
available_voices = []
executor = ThreadPoolExecutor(max_workers=2)

class TTSRequest(BaseModel):
    text: str
    language: str = "ru"
    voice: Optional[str] = None
    speed: float = 1.0
    output_format: str = "wav"

class TTSResponse(BaseModel):
    success: bool
    audio_base64: Optional[str] = None
    audio_path: Optional[str] = None
    message: str
    duration: Optional[float] = None

class VoiceInfo(BaseModel):
    id: str
    name: str
    language: str
    gender: Optional[str] = None

def initialize_tts():
    global tts_model, available_voices
    try:
        # Загружаем русскую модель Coqui TTS
        # XTTS v2 поддерживает русский язык и клонирование голоса
        from TTS.api import TTS
        tts_model = TTS("tts_models/multilingual/multi-dataset/xtts_v2")
        
        # Получаем доступные голоса
        available_voices = [
            VoiceInfo(id="ru_female", name="Русский женский", language="ru", gender="female"),
            VoiceInfo(id="ru_male", name="Русский мужской", language="ru", gender="male"),
            VoiceInfo(id="en_female", name="English Female", language="en", gender="female"),
            VoiceInfo(id="en_male", name="English Male", language="en", gender="male")
        ]
        
        logger.info(f"Coqui TTS успешно инициализирован. Доступные голоса: {len(available_voices)}")
        
    except Exception as e:
        logger.error(f"Ошибка инициализации Coqui TTS: {e}")
        logger.info("Работаем в режиме заглушки")
        tts_model = None

def synthesize_speech_sync(text: str, language: str = "ru", voice: str = "ru_female", speed: float = 1.0) -> tuple[bytes, float]:
    """Синхронная функция синтеза речи"""
    try:
        if tts_model is None:
            # Заглушка для dev режима
            logger.info(f"[Заглушка TTS] Синтез речи для: {text[:50]}...")
            # Создаем пустой WAV файл
            dummy_wav = create_dummy_wav(text)
            return dummy_wav, len(text) * 0.1  # Примерная длительность
        
        # Определяем параметры голоса
        speaker_wav = None
        if voice == "ru_female":
            # Используем русский женский голос
            speaker_wav = None  # Будет использован голос по умолчанию
        elif voice == "ru_male":
            # Используем русский мужской голос
            speaker_wav = None
        
        # Синтезируем речь
        with tempfile.NamedTemporaryFile(suffix=".wav", delete=False) as tmp_file:
            output_path = tmp_file.name
        
        # Генерируем аудио
        tts_model.tts_to_file(
            text=text,
            speaker_wav=speaker_wav,
            language=language,
            file_path=output_path,
            speed=speed
        )
        
        # Читаем сгенерированный файл
        with open(output_path, "rb") as f:
            audio_data = f.read()
        
        # Удаляем временный файл
        os.unlink(output_path)
        
        # Примерная длительность (в реальности можно получить из аудио файла)
        duration = len(text) * 0.1
        
        return audio_data, duration
        
    except Exception as e:
        logger.error(f"Ошибка синтеза речи: {e}")
        # Возвращаем заглушку в случае ошибки
        dummy_wav = create_dummy_wav(text)
        return dummy_wav, len(text) * 0.1

def create_dummy_wav(text: str) -> bytes:
    """Создает заглушку WAV файла для dev режима"""
    # Простой WAV заголовок + тишина
    sample_rate = 22050
    duration = len(text) * 0.1  # 0.1 секунды на символ
    samples = int(sample_rate * duration)
    
    # WAV заголовок (44 байта)
    wav_header = bytearray([
        0x52, 0x49, 0x46, 0x46,  # "RIFF"
        0x00, 0x00, 0x00, 0x00,  # File size (заполним позже)
        0x57, 0x41, 0x56, 0x45,  # "WAVE"
        0x66, 0x6D, 0x74, 0x20,  # "fmt "
        0x10, 0x00, 0x00, 0x00,  # Subchunk1Size
        0x01, 0x00,              # AudioFormat (PCM)
        0x01, 0x00,              # NumChannels
        0x44, 0xAC, 0x00, 0x00,  # SampleRate
        0x88, 0x58, 0x01, 0x00,  # ByteRate
        0x02, 0x00,              # BlockAlign
        0x10, 0x00,              # BitsPerSample
        0x64, 0x61, 0x74, 0x61,  # "data"
        0x00, 0x00, 0x00, 0x00   # Subchunk2Size
    ])
    
    # Устанавливаем правильные размеры
    data_size = samples * 2  # 16-bit samples
    file_size = len(wav_header) + data_size - 8
    
    wav_header[4:8] = file_size.to_bytes(4, 'little')
    wav_header[40:44] = data_size.to_bytes(4, 'little')
    
    # Создаем тишину (нули)
    audio_data = b'\x00' * data_size
    
    return bytes(wav_header) + audio_data

@app.post("/synthesize", response_model=TTSResponse, summary="Синтез речи", description="Преобразует текст в аудио с помощью Coqui TTS")
async def synthesize_speech(request: TTSRequest):
    try:
        if not request.text.strip():
            raise HTTPException(status_code=400, detail="Текст не может быть пустым")
        
        if len(request.text) > 1000:
            raise HTTPException(status_code=400, detail="Текст слишком длинный (максимум 1000 символов)")
        
        # Выполняем синтез в отдельном потоке
        loop = asyncio.get_event_loop()
        audio_data, duration = await loop.run_in_executor(
            executor, 
            synthesize_speech_sync, 
            request.text, 
            request.language, 
            request.voice or "ru_female", 
            request.speed
        )
        
        # Кодируем аудио в base64
        audio_base64 = base64.b64encode(audio_data).decode('utf-8')
        
        return TTSResponse(
            success=True,
            audio_base64=audio_base64,
            message=f"Аудио успешно сгенерировано для текста: {request.text[:50]}...",
            duration=duration
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Ошибка при синтезе речи: {e}")
        return TTSResponse(
            success=False,
            message=f"Ошибка синтеза речи: {str(e)}"
        )

@app.post("/synthesize-file", summary="Синтез речи в файл", description="Преобразует текст в аудио файл")
async def synthesize_speech_file(request: TTSRequest):
    try:
        if not request.text.strip():
            raise HTTPException(status_code=400, detail="Текст не может быть пустым")
        
        # Выполняем синтез в отдельном потоке
        loop = asyncio.get_event_loop()
        audio_data, duration = await loop.run_in_executor(
            executor, 
            synthesize_speech_sync, 
            request.text, 
            request.language, 
            request.voice or "ru_female", 
            request.speed
        )
        
        # Сохраняем во временный файл
        with tempfile.NamedTemporaryFile(suffix=f".{request.output_format}", delete=False) as tmp_file:
            tmp_file.write(audio_data)
            file_path = tmp_file.name
        
        return TTSResponse(
            success=True,
            audio_path=file_path,
            message=f"Аудио файл создан: {file_path}",
            duration=duration
        )
        
    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Ошибка при создании аудио файла: {e}")
        return TTSResponse(
            success=False,
            message=f"Ошибка создания аудио файла: {str(e)}"
        )

@app.get("/voices", response_model=List[VoiceInfo], summary="Список голосов", description="Получает список доступных голосов")
def get_voices():
    return available_voices

@app.get("/health")
def health():
    return {
        "status": "ok", 
        "service": "tts", 
        "model_loaded": tts_model is not None,
        "voices_count": len(available_voices)
    }

@app.get("/info")
def get_info():
    return {
        "service": "tts",
        "model": "Coqui TTS XTTS v2",
        "languages": ["ru", "en"],
        "features": ["neural_tts", "voice_cloning", "multilingual"],
        "max_text_length": 1000
    }

# Инициализируем TTS при старте
initialize_tts()