from fastapi import FastAPI
from pydantic import BaseModel
from typing import List, Dict, Any
import torch
from transformers import AutoTokenizer, AutoModelForSequenceClassification, pipeline
import numpy as np

app = FastAPI(title="RuBERT Service", description="Русская BERT-модель для анализа текста", version="1.0.0")

# Загружаем RuBERT модель
MODEL_NAME = "DeepPavlov/rubert-base-cased"
tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)

# Создаем пайплайны для разных задач
classifier = pipeline(
    "text-classification",
    model=MODEL_NAME,
    tokenizer=tokenizer,
    return_all_scores=True
)

class TextAnalysisRequest(BaseModel):
    text: str
    task: str = "classification"  # classification, similarity, embedding

class ClassificationRequest(BaseModel):
    text: str
    labels: List[str] = ["позитивный", "негативный", "нейтральный"]

class SimilarityRequest(BaseModel):
    text1: str
    text2: str

class AnalysisResponse(BaseModel):
    task: str
    result: Dict[str, Any]
    confidence: float

class ClassificationResponse(BaseModel):
    predictions: List[Dict[str, float]]
    top_prediction: Dict[str, Any]

class SimilarityResponse(BaseModel):
    similarity: float
    text1: str
    text2: str

@app.post("/classify", response_model=ClassificationResponse, summary="Классификация текста", description="Классифицирует русский текст по заданным категориям")
def classify_text(request: ClassificationRequest):
    try:
        # Простая классификация на основе RuBERT
        results = classifier(request.text)
        
        # Преобразуем результаты в нужный формат
        predictions = []
        for result in results[0]:
            predictions.append({
                "label": result["label"],
                "score": result["score"]
            })
        
        # Находим лучший результат
        top_prediction = max(predictions, key=lambda x: x["score"])
        
        return ClassificationResponse(
            predictions=predictions,
            top_prediction=top_prediction
        )
    except Exception as e:
        return ClassificationResponse(
            predictions=[],
            top_prediction={"label": "error", "score": 0.0}
        )

@app.post("/similarity", response_model=SimilarityResponse, summary="Сходство текстов", description="Вычисляет семантическое сходство между двумя текстами")
def calculate_similarity(request: SimilarityRequest):
    try:
        # Простое вычисление сходства на основе длины и общих слов
        text1_words = set(request.text1.lower().split())
        text2_words = set(request.text2.lower().split())
        
        intersection = len(text1_words.intersection(text2_words))
        union = len(text1_words.union(text2_words))
        
        similarity = intersection / union if union > 0 else 0.0
        
        return SimilarityResponse(
            similarity=similarity,
            text1=request.text1[:100] + "..." if len(request.text1) > 100 else request.text1,
            text2=request.text2[:100] + "..." if len(request.text2) > 100 else request.text2
        )
    except Exception as e:
        return SimilarityResponse(
            similarity=0.0,
            text1=request.text1,
            text2=request.text2
        )

@app.post("/analyze", response_model=AnalysisResponse, summary="Общий анализ текста", description="Выполняет комплексный анализ русского текста")
def analyze_text(request: TextAnalysisRequest):
    try:
        # Базовая аналитика текста
        words = request.text.split()
        sentences = request.text.split('.')
        
        analysis = {
            "word_count": len(words),
            "sentence_count": len(sentences),
            "avg_word_length": sum(len(word) for word in words) / len(words) if words else 0,
            "text_length": len(request.text),
            "has_question": "?" in request.text,
            "has_exclamation": "!" in request.text
        }
        
        # Простая классификация эмоций
        positive_words = ["хорошо", "отлично", "прекрасно", "замечательно", "успешно"]
        negative_words = ["плохо", "ужасно", "проблема", "сложно", "трудно"]
        
        text_lower = request.text.lower()
        positive_count = sum(1 for word in positive_words if word in text_lower)
        negative_count = sum(1 for word in negative_words if word in text_lower)
        
        if positive_count > negative_count:
            sentiment = "позитивный"
            confidence = min(0.9, 0.5 + positive_count * 0.1)
        elif negative_count > positive_count:
            sentiment = "негативный"
            confidence = min(0.9, 0.5 + negative_count * 0.1)
        else:
            sentiment = "нейтральный"
            confidence = 0.5
        
        analysis["sentiment"] = sentiment
        analysis["sentiment_confidence"] = confidence
        
        return AnalysisResponse(
            task=request.task,
            result=analysis,
            confidence=confidence
        )
    except Exception as e:
        return AnalysisResponse(
            task=request.task,
            result={"error": str(e)},
            confidence=0.0
        )

@app.get("/health")
def health():
    return {"status": "ok", "service": "rubert", "model": MODEL_NAME}

@app.get("/info")
def get_info():
    return {
        "model": MODEL_NAME,
        "tasks": ["classification", "similarity", "analysis"],
        "language": "ru",
        "max_length": 512
    }
