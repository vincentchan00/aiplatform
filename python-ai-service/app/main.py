from fastapi import FastAPI

from app.api import chat, documents


app = FastAPI(
    title="AI Platform RAG Service",
    description="Tenant-aware document retrieval and question answering",
    version="1.0.0",
)


@app.get("/health", tags=["health"])
def health() -> dict[str, str]:
    return {
        "status": "UP",
        "service": "python-ai-service",
    }


app.include_router(chat.router)
app.include_router(documents.router)
