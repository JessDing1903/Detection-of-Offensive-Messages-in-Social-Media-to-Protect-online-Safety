# API Reference — Offensive Message Detector

## Java Backend  `http://localhost:8080`

### Messages

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/messages/analyze` | Submit message for analysis |
| GET | `/api/messages` | All stored messages |
| GET | `/api/messages/offensive` | Only flagged messages |
| GET | `/api/messages/severity/{high\|medium\|low}` | Filter by severity |
| GET | `/api/messages/platform/{name}` | Filter by platform |
| GET | `/api/messages/author/{name}` | Filter by author |
| PATCH | `/api/messages/{id}/review` | Mark message reviewed |
| GET | `/api/messages/stats` | Dashboard statistics |

#### Submit a message
```json
POST /api/messages/analyze
{
  "text":     "I hate you so much",
  "author":   "user123",
  "platform": "twitter"
}
```
**Response:**
```json
{
  "id":          "6649…",
  "text":        "I hate you so much",
  "author":      "user123",
  "platform":    "twitter",
  "label":       "offensive",
  "isOffensive": true,
  "confidence":  0.9241,
  "severity":    "high",
  "status":      "FLAGGED",
  "createdAt":   "2026-05-28T10:00:00Z",
  "analyzedAt":  "2026-05-28T10:00:00Z"
}
```

### Reports

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/reports` | User reports a message |
| GET | `/api/reports/open` | Moderator review queue |
| GET | `/api/reports/message/{id}` | Reports for a message |
| PATCH | `/api/reports/{id}/resolve` | Resolve a report |

#### File a report
```json
POST /api/reports
{
  "messageId":  "6649…",
  "reportedBy": "user456",
  "reason":     "HATE_SPEECH"
}
```

---

## Python ML Service  `http://localhost:5001`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/ml/analyze` | Analyze single text |
| POST | `/api/ml/analyze/batch` | Analyze up to 100 texts |
| GET | `/api/ml/stats` | ML engine statistics |
| GET | `/health` | Service health |

#### Batch analysis
```json
POST /api/ml/analyze/batch
{
  "texts": [
    {"text": "You are amazing!"},
    {"text": "I hate you"},
    {"text": "Good morning everyone"}
  ]
}
```

---

## Severity Levels

| Level | Confidence | Action |
|-------|-----------|--------|
| `high` | ≥ 90% | Auto-flag, notify moderator |
| `medium` | 70–89% | Flag for review |
| `low` | < 70% | Log only |
| `none` | — | Normal message |

## Report Reasons

`HATE_SPEECH` · `HARASSMENT` · `THREATS` · `SPAM` · `OTHER`
