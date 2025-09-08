from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from pydantic import BaseModel
from typing import Optional
import os
import uuid

app = FastAPI(title="TTS Service", version="0.1.0")


class SynthesisRequest(BaseModel):
    text: str
    speaker: Optional[str] = None
    speed: Optional[float] = 1.0


OUTPUT_DIR = os.environ.get("TTS_OUTPUT_DIR", "/data/tts")
MODEL_NAME = os.environ.get("TTS_MODEL", "tts_models/ru/v3_1/fast_pitch")

os.makedirs(OUTPUT_DIR, exist_ok=True)


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}


@app.post("/synthesize")
def synthesize(req: SynthesisRequest):
    try:
        from TTS.api import TTS  # Lazy import to speed up startup/health
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to import Coqui TTS: {e}")

    try:
        tts = TTS(model_name=MODEL_NAME, progress_bar=False, gpu=False)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to load model {MODEL_NAME}: {e}")

    basename = f"tts_{uuid.uuid4().hex}.wav"
    out_path = os.path.join(OUTPUT_DIR, basename)

    try:
        # Coqui TTS api saves to file directly
        tts.tts_to_file(text=req.text, file_path=out_path, speaker=req.speaker, speed=req.speed)
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Synthesis error: {e}")

    return {"path": basename}


@app.get("/audio/{filename}")
def get_audio(filename: str):
    path = os.path.join(OUTPUT_DIR, filename)
    if not os.path.isfile(path):
        raise HTTPException(status_code=404, detail="File not found")
    return FileResponse(path, media_type="audio/wav")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=int(os.environ.get("PORT", 8100)))


