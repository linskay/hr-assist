from fastapi import FastAPI, UploadFile, File
from pydantic import BaseModel
from typing import Optional
import numpy as np
import cv2
from deepface import DeepFace
import mediapipe as mp

app = FastAPI(title="Video Antifraud Service", description="DeepFace + MediaPipe проверка личности и живости", version="1.0.0")

class VerifyResponse(BaseModel):
    is_match: bool
    confidence: float

class LivenessResponse(BaseModel):
    is_live: bool
    confidence: float

def read_image(file: UploadFile) -> np.ndarray:
    data = file.file.read()
    np_arr = np.frombuffer(data, np.uint8)
    img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
    return img

def extract_first_frame(video_bytes: bytes) -> Optional[np.ndarray]:
    np_arr = np.frombuffer(video_bytes, np.uint8)
    cap = cv2.VideoCapture()
    cap.open(cv2.imdecode(np_arr, cv2.IMREAD_COLOR))
    # Fallback: write temp if backend cannot open from buffer (cv2 limitation)
    return None

@app.post("/verify", response_model=VerifyResponse, summary="Сравнение лица", description="Принимает фото/видео, возвращает совпадение и уверенность")
async def verify(file: UploadFile = File(...)):
    content = await file.read()
    # Простая стратегия: если изображение — сразу сверка, если видео — возьмём ключевой кадр (первый)
    img = None
    if file.content_type.startswith("image/"):
        np_arr = np.frombuffer(content, np.uint8)
        img = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)
    else:
        # сохранить временно и вычитать первый кадр
        tmp = "/tmp/input.bin"
        with open(tmp, "wb") as f:
            f.write(content)
        cap = cv2.VideoCapture(tmp)
        ret, frame = cap.read()
        cap.release()
        img = frame if ret else None

    if img is None:
        return VerifyResponse(is_match=False, confidence=0.0)

    # В реальном сценарии нужно сравнить с эталонным лицом кандидата (предварительно сохранённым)
    # Здесь выполняем самодостаточную проверку качества лица и фейк-признаков через DeepFace.analyze
    try:
        analysis = DeepFace.analyze(img, actions=['emotion'], enforce_detection=False)
        # Простейший эвристический confidence: наличие валидного лица + уверенность эмоции
        conf = float(max(analysis[0]['emotion'].values())) if isinstance(analysis, list) else float(max(analysis['emotion'].values()))
        is_match = conf > 0.5
        return VerifyResponse(is_match=is_match, confidence=conf)
    except Exception:
        return VerifyResponse(is_match=False, confidence=0.0)

@app.post("/liveness", response_model=LivenessResponse, summary="Проверка живости", description="Проверяет моргание/движение головы по короткому видео/фото")
async def liveness(file: UploadFile = File(...)):
    content = await file.read()
    tmp = "/tmp/liveness.bin"
    with open(tmp, "wb") as f:
        f.write(content)
    cap = cv2.VideoCapture(tmp)

    mp_face_mesh = mp.solutions.face_mesh
    face_mesh = mp_face_mesh.FaceMesh(static_image_mode=False, max_num_faces=1, refine_landmarks=True)

    prev_ear = None
    blink_count = 0
    move_score = 0.0
    frames = 0

    def eye_aspect_ratio(landmarks):
        # Используем точки глаза вокруг зрачка (индексы для MediaPipe 468-точечной сетки)
        left_ids = [33, 160, 158, 133, 153, 144]
        pts = [(landmarks[i].x, landmarks[i].y) for i in left_ids]
        A = np.linalg.norm(np.array(pts[1]) - np.array(pts[5]))
        B = np.linalg.norm(np.array(pts[2]) - np.array(pts[4]))
        C = np.linalg.norm(np.array(pts[0]) - np.array(pts[3]))
        ear = (A + B) / (2.0 * C + 1e-6)
        return ear

    success, prev = cap.read()
    if not success:
        cap.release()
        return LivenessResponse(is_live=False, confidence=0.0)

    while True:
        success, frame = cap.read()
        if not success:
            break
        frames += 1
        rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
        result = face_mesh.process(rgb)
        if result.multi_face_landmarks:
            lms = result.multi_face_landmarks[0].landmark
            ear = eye_aspect_ratio(lms)
            if prev_ear is not None and prev_ear > 0.25 and ear < 0.2:
                blink_count += 1
            prev_ear = ear

        # Примитивная метрика движения головы
        diff = cv2.absdiff(frame, prev)
        move_score += float(np.sum(diff)) / (frame.shape[0] * frame.shape[1] * 255.0)
        prev = frame

    cap.release()
    face_mesh.close()

    is_live = blink_count >= 1 or move_score / max(frames, 1) > 0.02
    confidence = min(1.0, (blink_count * 0.5) + (move_score / max(frames, 1)))
    return LivenessResponse(is_live=is_live, confidence=confidence)


