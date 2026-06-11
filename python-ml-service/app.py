"""
Offensive Message Detection — Python ML microservice
Exposes a REST API consumed by the Java Spring Boot backend.
"""
from flask import Flask, request, jsonify
from pymongo import MongoClient
from datetime import datetime, timezone
import os

from model.predictor import predict

app = Flask(__name__)

MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017/")
DB_NAME   = os.getenv("DB_NAME", "offensive_detector")

client = MongoClient(MONGO_URI)
db     = client[DB_NAME]
col    = db["ml_analysis_logs"]


# ── Health ────────────────────────────────────────────────────────────────────
@app.route("/health", methods=["GET"])
def health():
    return jsonify({"status": "ok", "service": "ml-detection"}), 200


# ── Single prediction ─────────────────────────────────────────────────────────
@app.route("/api/ml/analyze", methods=["POST"])
def analyze():
    body = request.get_json(silent=True) or {}
    text = body.get("text", "").strip()

    if not text:
        return jsonify({"error": "Field 'text' is required"}), 400

    result = predict(text)
    result["original_text"] = text
    result["analyzed_at"]   = datetime.now(timezone.utc).isoformat()

    col.insert_one({**result, "source": body.get("source", "api")})
    result.pop("_id", None)

    return jsonify(result), 200


# ── Batch prediction ──────────────────────────────────────────────────────────
@app.route("/api/ml/analyze/batch", methods=["POST"])
def analyze_batch():
    body  = request.get_json(silent=True) or {}
    texts = body.get("texts", [])

    if not texts or not isinstance(texts, list):
        return jsonify({"error": "Field 'texts' must be a non-empty list"}), 400
    if len(texts) > 100:
        return jsonify({"error": "Batch limit is 100 messages"}), 400

    results = []
    docs    = []
    ts      = datetime.now(timezone.utc).isoformat()

    for item in texts:
        text = (item.get("text", "") if isinstance(item, dict) else str(item)).strip()
        if not text:
            results.append({"error": "empty text"})
            continue
        r = predict(text)
        r.update({"original_text": text, "analyzed_at": ts})
        docs.append({**r, "source": "batch"})
        results.append(r)

    if docs:
        col.insert_many(docs)

    return jsonify({"results": results, "total": len(results)}), 200


# ── Statistics ────────────────────────────────────────────────────────────────
@app.route("/api/ml/stats", methods=["GET"])
def stats():
    total     = col.count_documents({})
    offensive = col.count_documents({"is_offensive": True})
    normal    = col.count_documents({"is_offensive": False})
    high      = col.count_documents({"severity": "high"})
    medium    = col.count_documents({"severity": "medium"})
    low       = col.count_documents({"severity": "low"})

    return jsonify({
        "total_analyzed": total,
        "offensive_count": offensive,
        "normal_count": normal,
        "offensive_rate": round(offensive / total, 4) if total else 0,
        "by_severity": {"high": high, "medium": medium, "low": low},
    }), 200


if __name__ == "__main__":
    port = int(os.getenv("ML_PORT", 5001))
    print(f"ML service running on port {port}")
    app.run(host="0.0.0.0", port=port, debug=False)
