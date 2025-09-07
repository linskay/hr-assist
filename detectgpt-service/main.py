from fastapi import FastAPI
from pydantic import BaseModel
from transformers import AutoTokenizer, AutoModelForSequenceClassification
import torch

MODEL_NAME = "roberta-base-openai-detector"

app = FastAPI(title="DetectGPT Service", description="Детекция AI-сгенерированного текста", version="1.0.0")

tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)
model = AutoModelForSequenceClassification.from_pretrained(MODEL_NAME)
model.eval()

class DetectRequest(BaseModel):
    text: str

class DetectResponse(BaseModel):
    ai_probability: float

@app.post("/detect", response_model=DetectResponse, summary="Вероятность AI-генерации", description="Принимает текст и возвращает вероятность того, что он сгенерирован моделью GPT")
def detect(req: DetectRequest):
    inputs = tokenizer(req.text[:4000], return_tensors="pt", truncation=True, max_length=512)
    with torch.no_grad():
        logits = model(**inputs).logits
        probs = torch.softmax(logits, dim=-1).tolist()[0]
    # Модель возвращает [human, gpt] вероятности
    ai_prob = float(probs[1])
    return DetectResponse(ai_probability=ai_prob)


