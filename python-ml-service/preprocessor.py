import re
import string
import nltk
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer

nltk.download("stopwords", quiet=True)
nltk.download("punkt", quiet=True)

stemmer = PorterStemmer()
stop_words = set(stopwords.words("english"))

OFFENSIVE_PATTERNS = [
    r"\b(hate|kill|die|stupid|idiot|dumb|ugly|loser|freak)\b",
    r"\b(bully|harass|threaten|abuse|attack)\b",
]


def clean_text(text: str) -> str:
    text = text.lower()
    text = re.sub(r"http\S+|www\S+", "", text)       # remove URLs
    text = re.sub(r"@\w+|#\w+", "", text)             # remove mentions/hashtags
    text = re.sub(r"[^a-z\s]", "", text)              # keep letters only
    text = re.sub(r"\s+", " ", text).strip()
    return text


def tokenize_and_stem(text: str) -> str:
    tokens = text.split()
    tokens = [stemmer.stem(t) for t in tokens if t not in stop_words and len(t) > 2]
    return " ".join(tokens)


def preprocess(text: str) -> str:
    return tokenize_and_stem(clean_text(text))


def has_pattern_match(text: str) -> bool:
    text_lower = text.lower()
    return any(re.search(p, text_lower) for p in OFFENSIVE_PATTERNS)
