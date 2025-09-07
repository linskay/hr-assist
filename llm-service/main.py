from fastapi import FastAPI
from pydantic import BaseModel
from typing import Optional
from llama_cpp import Llama
import os

MODEL_PATH = os.environ.get("LLM_MODEL_PATH", "/models/mistral-7b-instruct.Q4_K_M.gguf")
CTX_SIZE = int(os.environ.get("LLM_CTX_SIZE", "2048"))
N_THREADS = int(os.environ.get("LLM_THREADS", "6"))

app = FastAPI(title="LLM Service (llama.cpp)", description="Генерация объяснения пригодности кандидата", version="1.0.0")

llm = Llama(model_path=MODEL_PATH, n_ctx=CTX_SIZE, n_threads=N_THREADS, verbose=False)

class GenerateRequest(BaseModel):
    prompt: str
    max_tokens: Optional[int] = 512
    temperature: Optional[float] = 0.3

class GenerateResponse(BaseModel):
    output: str

@app.post("/generate", response_model=GenerateResponse, summary="Генерация ответа", description="Принимает текстовый промпт и возвращает сгенерированный ответ LLM.")
def generate(req: GenerateRequest):
    # ограничение длины промпта для безопасности
    safe_prompt = req.prompt[:4000]
    result = llm(
        safe_prompt,
        max_tokens=req.max_tokens,
        temperature=req.temperature,
        stop=["</s>", "</SYS>"]
    )
    text = result["choices"][0]["text"].strip()
    return GenerateResponse(output=text)


