# Firestore and Data Persistence Guide - Super Snake Game

This document explains the data architecture, Firestore database schema, transaction models, and persistence mechanisms in Super Snake Game.

## 1. Database Model

Super Snake Game utilizes Google Cloud Firestore, a scalable NoSQL cloud document database, to persist player high scores across sessions and devices.

### Schema Architecture

```
Firestore Root
   │
   └── records (Collection)
          │
          ├── {userId_1} (Document)
          │      └── score: 2500 (Number)
          │
          ├── {userId_2} (Document)
          │      └── score: 8400 (Number)
          │
          └── ...
```

- **Collection Name**: `records`
- **Document ID**: The unique Firebase User ID (`user.uid`) issued upon Google Sign-In.
- **Fields**:
  - `score` (Integer / Long): The highest game score recorded for that user.

## 2. Atomic Transactions for High Scores

To prevent race conditions (for instance, if multiple games complete simultaneously or if offline writes sync late), high scores are committed using Cloud Firestore transactions (`firestore.runTransaction`):

```kotlin
override suspend fun saveIfHigher(
    userId: String,
    newScore: Int,
): Int {
    val ref = col.document(userId)
    return firestore
        .runTransaction { tx ->
            val snapshot = tx.get(ref)
            val old = snapshot.getLong(FIELD_SCORE)?.toInt() ?: 0
            val best = maxOf(old, newScore)
            tx.set(ref, mapOf(FIELD_SCORE to best), SetOptions.merge())
            best
        }.await()
}
```

### Transactional Guarantees

1. **Atomicity**: The read (`snapshot = tx.get(ref)`) and write (`tx.set(...)`) occur atomically.
2. **Monotonic Progression**: `val best = maxOf(old, newScore)` guarantees that a lower score will never overwrite a higher historical record.
3. **Merge Options**: `SetOptions.merge()` ensures existing fields remain intact if additional user metadata is introduced in future updates.

## 3. Data Flow and Use Cases

The data layer is decoupled from the UI via dedicated domain Use Cases:

### A. Fetching High Scores (`GetHighScoreUseCase`)

Executed when `SnakeGameViewModel` initializes:
1. Retrieves the current `FirebaseUser` from `AuthRepository`.
2. Calls `recordRepository.getRecordForUser(userId)`.
3. If no record exists yet, defaults to `0`.
4. Populates the `_record` StateFlow to display on the game screen header.

### B. Saving High Scores (`SaveHighScoreUseCase`)

Executed when a game ends (`updated.isGameOver == true`):
1. Verifies that an authenticated user is active.
2. Calls `recordRepository.saveIfHigher(userId, score)`.
3. Returns the highest resulting score, updating the UI in real time.

## 4. Security Rules Recommendation

For production deployment in Firebase Console, the following Cloud Firestore security rules ensure users can only read and write their own records:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /records/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```
