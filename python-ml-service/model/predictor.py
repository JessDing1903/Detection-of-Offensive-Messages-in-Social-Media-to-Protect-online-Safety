"""Wraps the trained pipeline with confidence scoring and pattern boosting."""
import sys
import os
sys.path.insert(0, os.path.dirname(os.path.dirname(__file__)))

from model.trainer import load_model
from preprocessor import preprocess, has_pattern_match

_model = None


def get_model():
    global _model
    if _model is None:
        _model = load_model()
    return _model


def predict(text: str) -> dict:
    model = get_model()
    processed = preprocess(text)

    proba = model.predict_proba([processed])[0]
    confidence = float(max(proba))
    label_idx = int(model.predict([processed])[0])

    # Boost confidence when hard-coded offensive patterns also fire
    if label_idx == 1 or has_pattern_match(text):
        label_idx = 1
        confidence = max(confidence, 0.75)

    severity = _severity(confidence, label_idx)

    return {
        "label": "offensive" if label_idx == 1 else "normal",
        "is_offensive": bool(label_idx == 1),
        "confidence": round(confidence, 4),
        "severity": severity,
        "processed_text": processed,
    }


def _severity(confidence: float, label: int) -> str:
    if label == 0:
        return "none"
    if confidence >= 0.90:
        return "high"
    if confidence >= 0.70:
        return "medium"
    return "low"
