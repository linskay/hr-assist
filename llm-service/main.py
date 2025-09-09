from fastapi import FastAPI
from pydantic import BaseModel
from typing import Optional
import os

app = FastAPI(title="LLM Service (llama.cpp)", description="Генерация объяснения пригодности кандидата", version="1.0.0")

# Заглушка без модели для dev режима
MODEL_PATH = os.environ.get("LLM_MODEL_PATH", "/models/mistral-7b-instruct.Q4_K_M.gguf")
llm = None

# Попытка загрузить модель, если файл существует
def load_llm_model():
    global llm
    if os.path.exists(MODEL_PATH):
        try:
            from llama_cpp import Llama
            CTX_SIZE = int(os.environ.get("LLM_CTX_SIZE", "2048"))
            N_THREADS = int(os.environ.get("LLM_THREADS", "6"))
            llm = Llama(model_path=MODEL_PATH, n_ctx=CTX_SIZE, n_threads=N_THREADS, verbose=False)
            print(f"LLM модель загружена: {MODEL_PATH}")
            return True
        except Exception as e:
            print(f"Ошибка загрузки LLM модели: {e}")
            llm = None
            return False
    else:
        print(f"LLM модель не найдена: {MODEL_PATH}. Работаем в режиме заглушки.")
        llm = None
        return False

class GenerateRequest(BaseModel):
    prompt: str
    max_tokens: Optional[int] = 512
    temperature: Optional[float] = 0.3

class GenerateResponse(BaseModel):
    output: str

@app.post("/generate", response_model=GenerateResponse, summary="Генерация ответа", description="Принимает текстовый промпт и возвращает сгенерированный ответ LLM.")
def generate(req: GenerateRequest):
    # Попытка загрузить модель при первом запросе
    if llm is None:
        load_llm_model()
    
    if llm is None:
        # Заглушка для dev режима
        return GenerateResponse(output=f"[Заглушка LLM] Обработан промпт: {req.prompt[:100]}...")
    
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

@app.get("/health")
def health():
    return {"status": "ok", "service": "llm", "model_loaded": llm is not None}


