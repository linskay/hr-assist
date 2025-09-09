
from fastapi import FastAPI, UploadFile, File, Form
from pydantic import BaseModel
from typing import Optional
import tempfile
import os
import subprocess
import json

app = FastAPI(title="Avatar Service", description="Генерация говорящих голов с помощью SadTalker", version="1.0.0")

class AvatarRequest(BaseModel):
    image_path: str
    audio_path: str
    output_path: Optional[str] = None

class AvatarResponse(BaseModel):
    success: bool
    output_path: str
    message: str

@app.post("/generate", response_model=AvatarResponse, summary="Генерация аватара", description="Создает говорящую голову из изображения и аудио")
async def generate_avatar(
    image: UploadFile = File(...),
    audio: UploadFile = File(...),
    output_format: str = Form("mp4")
):
    try:
        with tempfile.TemporaryDirectory() as tmpdir:
            # Сохраняем загруженные файлы
            image_path = os.path.join(tmpdir, f"input_image.{image.filename.split('.')[-1]}")
            audio_path = os.path.join(tmpdir, f"input_audio.{audio.filename.split('.')[-1]}")
            output_path = os.path.join(tmpdir, f"output.{output_format}")
            
            with open(image_path, "wb") as f:
                f.write(await image.read())
            
            with open(audio_path, "wb") as f:
                f.write(await audio.read())
            
            # Заглушка для dev режима (реальный SadTalker требует GPU и много зависимостей)
            # В реальной реализации здесь был бы вызов SadTalker
            dummy_output = f"avatar_generated_{os.path.basename(output_path)}"
            
            return AvatarResponse(
                success=True,
                output_path=dummy_output,
                message="[Заглушка Avatar] Аватар сгенерирован (режим разработки)"
            )
            
    except Exception as e:
        return AvatarResponse(
            success=False,
            output_path="",
            message=f"Ошибка генерации аватара: {str(e)}"
        )

@app.get("/health")
def health():
    return {"status": "ok", "service": "avatar"}
