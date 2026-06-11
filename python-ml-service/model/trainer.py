"""Train and save the offensive message classifier."""
import os
import pickle
import numpy as np
from sklearn.pipeline import Pipeline
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.metrics import classification_report

MODEL_PATH = os.path.join(os.path.dirname(__file__), "classifier.pkl")

# Balanced seed dataset — expand with real labeled data for production
SEED_DATA = [
    # --- offensive ---
    ("I hate you so much, you are worthless", 1),
    ("Go kill yourself loser", 1),
    ("You are the ugliest person alive", 1),
    ("Shut up you stupid idiot", 1),
    ("Nobody likes you, you freak", 1),
    ("You are a complete failure and should disappear", 1),
    ("Stop being such a dumb moron", 1),
    ("I will find you and hurt you", 1),
    ("You disgusting piece of trash", 1),
    ("Die in a fire you worthless human", 1),
    ("You make me sick with your stupidity", 1),
    ("Everyone hates your ugly face", 1),
    ("You are absolutely pathetic and useless", 1),
    ("I despise you, go away forever", 1),
    ("Such a loser, no one cares about you", 1),
    # --- normal ---
    ("I love spending time with my friends", 0),
    ("The weather is beautiful today", 0),
    ("Can you help me with my homework please", 0),
    ("This movie was really enjoyable", 0),
    ("I am excited about the upcoming holiday", 0),
    ("Great job on your presentation today", 0),
    ("Let us meet for coffee this weekend", 0),
    ("I just finished reading an amazing book", 0),
    ("Happy birthday, hope you have a wonderful day", 0),
    ("The team worked really hard on this project", 0),
    ("I appreciate your kind words and support", 0),
    ("Looking forward to seeing you tomorrow", 0),
    ("This recipe turned out absolutely delicious", 0),
    ("We should collaborate more often", 0),
    ("Congratulations on your achievement", 0),
]


def train_and_save():
    texts, labels = zip(*SEED_DATA)
    X_train, X_test, y_train, y_test = train_test_split(
        texts, labels, test_size=0.2, random_state=42, stratify=labels
    )

    pipeline = Pipeline([
        ("tfidf", TfidfVectorizer(ngram_range=(1, 2), max_features=10000, sublinear_tf=True)),
        ("clf", LogisticRegression(C=1.0, max_iter=1000, class_weight="balanced")),
    ])

    pipeline.fit(X_train, y_train)

    cv_scores = cross_val_score(pipeline, texts, labels, cv=5, scoring="f1")
    print(f"Cross-val F1: {np.mean(cv_scores):.3f} ± {np.std(cv_scores):.3f}")
    print(classification_report(y_test, pipeline.predict(X_test), target_names=["Normal", "Offensive"]))

    with open(MODEL_PATH, "wb") as f:
        pickle.dump(pipeline, f)

    print(f"Model saved to {MODEL_PATH}")
    return pipeline


def load_model():
    if not os.path.exists(MODEL_PATH):
        print("Model not found — training now...")
        return train_and_save()
    with open(MODEL_PATH, "rb") as f:
        return pickle.load(f)


if __name__ == "__main__":
    train_and_save()
