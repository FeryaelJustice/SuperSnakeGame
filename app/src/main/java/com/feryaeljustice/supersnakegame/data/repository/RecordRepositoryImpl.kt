package com.feryaeljustice.supersnakegame.data.repository

import com.feryaeljustice.supersnakegame.domain.repository.RecordRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecordRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
    ) : RecordRepository {
        companion object {
            private const val COLLECTION_NAME = "records"
            private const val FIELD_SCORE = "score"
        }

        private val col = firestore.collection(COLLECTION_NAME)

        override suspend fun getRecordForUser(userId: String): Int? {
            val doc = col.document(userId).get().await()
            return doc.getLong(FIELD_SCORE)?.toInt()
        }

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
    }
