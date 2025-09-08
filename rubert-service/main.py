from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import Optional
import torch
from transformers import AutoTokenizer, AutoModel
import os

MODEL_NAME = os.environ.get("RUBERT_MODEL", "DeepPavlov/rubert-base-cased")
DEVICE = "cuda" if torch.cuda.is_available() else "cpu"

app = FastAPI(title="ruBERT Similarity Service", version="0.1.0")

tokenizer = AutoTokenizer.from_pretrained(MODEL_NAME)
model = AutoModel.from_pretrained(MODEL_NAME).to(DEVICE)
model.eval()


class PairRequest(BaseModel):
    answer: str
    requirement: str


def mean_pooling(last_hidden_state, attention_mask):
    mask = attention_mask.unsqueeze(-1).expand(last_hidden_state.size()).float()
    masked = last_hidden_state * mask
    summed = torch.sum(masked, dim=1)
    counts = torch.clamp(mask.sum(dim=1), min=1e-9)
    return summed / counts


def embed(texts):
    with torch.no_grad():
        encoded = tokenizer(texts, padding=True, truncation=True, max_length=256, return_tensors="pt").to(DEVICE)
        out = model(**encoded)
        emb = mean_pooling(out.last_hidden_state, encoded["attention_mask"])  # [bs, hidden]
        emb = torch.nn.functional.normalize(emb, p=2, dim=1)
        return emb.cpu()


@app.get("/health")
def health():
    return {"status": "ok", "device": DEVICE}


@app.post("/score")
def score(req: PairRequest):
    try:
        embs = embed([req.answer, req.requirement])
        sim = torch.matmul(embs[0], embs[1]).item()  # cosine since normalized
        # Map similarity to {0, 0.5, 1}
        if sim >= 0.8:
            score = 1.0
        elif sim >= 0.5:
            score = 0.5
        else:
            score = 0.0
        return {"similarity": sim, "score": score}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=int(os.environ.get("PORT", 8093)))


