package com.feryaeljustice.supersnakegame.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.feryaeljustice.supersnakegame.domain.repository.RecordRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepositoryImpl
    @Inject
    constructor(
        private val firestore: FirebaseFirestore,
        @ApplicationContext context: Context,
    ) : RecordRepository {
        companion object {
            private const val COLLECTION_NAME = "records"
            private const val FIELD_SCORE = "score"
            private const val PREFS_NAME = "super_snake_records_prefs"
            private const val KEY_LOCAL_RECORD = "local_high_score"
            private const val PREFIX_USER_CLOUD_CACHE = "user_cloud_record_"
        }

        private val prefs: SharedPreferences =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        private val col = firestore.collection(COLLECTION_NAME)

        override suspend fun getRecordForUser(userId: String): Int? {
            return try {
                val doc = col.document(userId).get().await()
                val score = doc.getLong(FIELD_SCORE)?.toInt()
                if (score != null) {
                    saveCachedRecordForUser(userId, score)
                }
                score
            } catch (_: Exception) {
                null
            }
        }

        override suspend fun saveIfHigher(
            userId: String,
            newScore: Int,
        ): Int {
            val ref = col.document(userId)
            val best =
                firestore
                    .runTransaction { tx ->
                        val snapshot = tx.get(ref)
                        val old = snapshot.getLong(FIELD_SCORE)?.toInt() ?: 0
                        val updated = maxOf(old, newScore)
                        tx.set(ref, mapOf(FIELD_SCORE to updated), SetOptions.merge())
                        updated
                    }.await()
            saveCachedRecordForUser(userId, best)
            return best
        }

        override suspend fun getCachedRecordForUser(userId: String): Int =
            withContext(Dispatchers.IO) {
                prefs.getInt(PREFIX_USER_CLOUD_CACHE + userId, 0)
            }

        override suspend fun saveCachedRecordForUser(userId: String, score: Int): Unit =
            withContext(Dispatchers.IO) {
                val current = prefs.getInt(PREFIX_USER_CLOUD_CACHE + userId, 0)
                val best = maxOf(current, score)
                if (best > current) {
                    prefs.edit { putInt(PREFIX_USER_CLOUD_CACHE + userId, best) }
                }
            }

        override suspend fun getLocalRecord(): Int =
            withContext(Dispatchers.IO) {
                prefs.getInt(KEY_LOCAL_RECORD, 0)
            }

        override suspend fun saveLocalRecordIfHigher(newScore: Int): Int =
            withContext(Dispatchers.IO) {
                val current = prefs.getInt(KEY_LOCAL_RECORD, 0)
                val best = maxOf(current, newScore)
                if (best > current) {
                    prefs.edit { putInt(KEY_LOCAL_RECORD, best) }
                }
                best
            }

        override suspend fun resetLocalRecord(): Unit =
            withContext(Dispatchers.IO) {
                prefs.edit { putInt(KEY_LOCAL_RECORD, 0) }
            }
    }
