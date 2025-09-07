from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
from sentence_transformers import SentenceTransformer
import numpy as np

app = FastAPI(title="SBERT Embedding Service", description="Сервис эмбеддингов all-MiniLM-L6-v2", version="1.0.0")

model = SentenceTransformer("sentence-transformers/all-MiniLM-L6-v2")

class EmbedRequest(BaseModel):
    text: str

class EmbedResponse(BaseModel):
    embedding: List[float]

class SimilarityRequest(BaseModel):
    text1: str
    text2: str

class SimilarityResponse(BaseModel):
    similarity: float

@app.post("/embed", response_model=EmbedResponse, summary="Эмбеддинг текста", description="Принимает текст и возвращает вектор-эмбеддинг.")
def embed(req: EmbedRequest):
    vec = model.encode(req.text, normalize_embeddings=True).tolist()
    return EmbedResponse(embedding=vec)

@app.post("/similarity", response_model=SimilarityResponse, summary="Косинусное сходство", description="Принимает 2 текста и возвращает cosine similarity их эмбеддингов.")
def similarity(req: SimilarityRequest):
    v1 = model.encode(req.text1, normalize_embeddings=True)
    v2 = model.encode(req.text2, normalize_embeddings=True)
    sim = float(np.dot(v1, v2))
    return SimilarityResponse(similarity=sim)


