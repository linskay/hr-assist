from fastapi import FastAPI, UploadFile, File, Form
from fastapi.responses import JSONResponse
from typing import Optional
import tempfile
import os
import uvicorn
import whisperx

app = FastAPI(title="WhisperX Service", version="1.0.0")


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}


@app.post("/transcribe")
async def transcribe(
    file: UploadFile = File(...),
    language: Optional[str] = Form(None),
    model_size: str = Form("large-v2"),
    batch_size: int = Form(8),
    compute_type: str = Form("float16"),
    diarize: bool = Form(False),
    min_speakers: Optional[int] = Form(None),
    max_speakers: Optional[int] = Form(None),
):
    with tempfile.TemporaryDirectory() as tmpdir:
        audio_path = os.path.join(tmpdir, file.filename)
        with open(audio_path, "wb") as f:
            f.write(await file.read())

        device = "cuda" if whisperx.utils.get_device() == "cuda" else "cpu"

        model = whisperx.load_model(model_size, device=device, compute_type=compute_type)
        audio = whisperx.load_audio(audio_path)
        result = model.transcribe(audio, batch_size=batch_size)

        align_lang = language or result.get("language")
        model_a, metadata = whisperx.load_align_model(language_code=align_lang, device=device)
        result = whisperx.align(result["segments"], model_a, metadata, audio, device, return_char_alignments=False)

        diarization_segments = None
        if diarize:
            diarizer = whisperx.diarize.DiarizationPipeline(device=device)
            diarization_segments = diarizer(audio_path, min_speakers=min_speakers, max_speakers=max_speakers)
            result = whisperx.assign_word_speakers(diarization_segments, result)

        response = {
            "language": align_lang,
            "segments": result["segments"],
            "diarization": diarization_segments,
        }
        return JSONResponse(response)


if __name__ == "__main__":
    uvicorn.run("main:app", host="0.0.0.0", port=9000, reload=False)


