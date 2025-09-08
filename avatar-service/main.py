from fastapi import FastAPI, HTTPException
from fastapi.responses import FileResponse
from pydantic import BaseModel
from typing import Optional
import os
import uuid

app = FastAPI(title="Avatar Service", version="0.1.0")


class AvatarRequest(BaseModel):
    audio_url: Optional[str] = None
    audio_path: Optional[str] = None
    driving_audio_wav: Optional[str] = None
    face_image_path: Optional[str] = None


OUTPUT_DIR = os.environ.get("AVATAR_OUTPUT_DIR", "/data/avatar")
os.makedirs(OUTPUT_DIR, exist_ok=True)


@app.get("/health")
def health() -> dict:
    return {"status": "ok"}


@app.post("/generate")
def generate(req: AvatarRequest):
    # Placeholder implementation with Kokoro expected pipeline.
    # For now, just return a stub mp4 path to be replaced when model wired.
    filename = f"avatar_{uuid.uuid4().hex}.mp4"
    out_path = os.path.join(OUTPUT_DIR, filename)
    # Create empty file as stub
    open(out_path, "wb").close()
    return {"path": filename}


@app.get("/video/{filename}")
def get_video(filename: str):
    path = os.path.join(OUTPUT_DIR, filename)
    if not os.path.isfile(path):
        raise HTTPException(status_code=404, detail="File not found")
    return FileResponse(path, media_type="video/mp4")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=int(os.environ.get("PORT", 8200)))


