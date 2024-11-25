package com.ombati.guidecaneapp.data.repositories.storage

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.ombati.guidecaneapp.common.Result
import com.ombati.guidecaneapp.common.convertDateFormat
import com.ombati.guidecaneapp.data.model.EmergencyStatus
import com.ombati.guidecaneapp.data.model.GuideCaneUser
import com.ombati.guidecaneapp.data.model.History
import com.ombati.guidecaneapp.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import javax.inject.Inject

class GuideCaneRepositoryImpl @Inject constructor(
    private val firebaseDB: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : GuideCaneRepository {

    override fun fetchGuideCaneUsersForCurrentUser(
        appUserId: String,
        onGuideCaneUsersUpdated: (Result<List<GuideCaneUser>>) -> Unit
    ) {
        firebaseDB.collection("AppUser")
            .document(appUserId)
            .addSnapshotListener { appUserSnapshot, error ->
                if (error != null || appUserSnapshot == null) {
                    onGuideCaneUsersUpdated(Result.Failure(Exception("Error fetching AppUser or appUserSnapshot is null")))
                    return@addSnapshotListener
                }

                val guideCaneUserIds = appUserSnapshot.get("guideCaneUsers") as? List<String>
                    ?: return@addSnapshotListener onGuideCaneUsersUpdated(Result.Failure(Exception("guideCaneUsers not found or empty")))

                if (guideCaneUserIds.isEmpty()) {
                    onGuideCaneUsersUpdated(Result.Success(emptyList()))
                    return@addSnapshotListener
                }

                firebaseDB.collection("GuideCane")
                    .whereIn(FieldPath.documentId(), guideCaneUserIds)
                    .addSnapshotListener { guideCaneUserSnapshots, error ->
                        if (error != null || guideCaneUserSnapshots == null) {
                            onGuideCaneUsersUpdated(Result.Failure(Exception("Error fetching GuideCane users")))
                            return@addSnapshotListener
                        }

                        val guideCaneUsers = guideCaneUserSnapshots.documents.map { document ->
                            val emergencyStatus = when (val status = document.getString("emergency_status")) {
                                null, "string" -> EmergencyStatus.Normal.name
                                else -> status
                            }

                            GuideCaneUser(
                                id = document.id,
                                batteryLevel = document.getString("battery_level") ?: "",
                                caregiverNumber = document.getString("caregiver_number") ?: "",
                                emergencyStatus = emergencyStatus,
                                geoFencingLatitude = document.getDouble("geo_fen_lat") ?: 0.0,
                                geoFencingLongitude = document.getDouble("geo_fen_lon") ?: 0.0,
                                latitude = document.getDouble("latitude") ?: 0.0,
                                longitude = document.getDouble("longitude") ?: 0.0,
                                securityKey = document.getString("security_key") ?: "",
                                smartCaneId = document.getString("smart_cane_id") ?: ""
                            )
                        }

                        onGuideCaneUsersUpdated(Result.Success(guideCaneUsers))
                    }
            }
    }

    override suspend fun removeSmartCaneIdFromAppUser(smartCaneId: String): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val appUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@withContext Result.Failure(IllegalStateException("User is not authenticated"))
                val appUserDocRef = firebaseDB.collection("AppUser").document(appUserId)


                firebaseDB.runTransaction { transaction ->
                    val appUserSnapshot = transaction.get(appUserDocRef)
                    val guideCaneIds = appUserSnapshot.get("guideCaneUsers") as? MutableList<String> ?: mutableListOf()

                    if (guideCaneIds.contains(smartCaneId)) {
                        guideCaneIds.remove(smartCaneId)
                    } else {
                        throw IllegalArgumentException("The smartCaneId $smartCaneId does not exist in the guideCaneUsers array.")
                    }


                    transaction.update(appUserDocRef, "guideCaneUsers", guideCaneIds)
                }.await()

                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Log.e("removeSmartCaneIdFromAppUser", "Error: $e")
            Result.Failure(e)
        }
    }


    override suspend fun fetchGuideCaneUser(smartCaneId: String): Result<GuideCaneUser> {
        return try {
            val documentSnapshot = firebaseDB.collection("GuideCane")
                .document(smartCaneId)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val emergencyStatus = when (val status = documentSnapshot.getString("emergency_status")) {
                    null, "string" -> EmergencyStatus.Normal.name
                    else -> status
                }


                val guideCaneUser = GuideCaneUser(
                    id = documentSnapshot.id,
                    batteryLevel = documentSnapshot.getString("battery_level") ?: "",
                    caregiverNumber = documentSnapshot.getString("caregiver_number") ?: "",
                    emergencyStatus = emergencyStatus,
                    geoFencingLatitude = documentSnapshot.getDouble("geo_fen_lat") ?: 0.0,
                    geoFencingLongitude = documentSnapshot.getDouble("geo_fen_lon") ?: 0.0,
                    latitude = documentSnapshot.getDouble("latitude") ?: 0.0,
                    longitude = documentSnapshot.getDouble("longitude") ?: 0.0,
                    securityKey = documentSnapshot.getString("security_key") ?: "",
                    smartCaneId = documentSnapshot.getString("smart_cane_id") ?: ""
                )

                Result.Success(guideCaneUser)
            } else {
                Result.Failure(Exception("Document does not exist"))
            }
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }



    override suspend fun updateGuideCaneUser(smartCaneId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                withTimeout(10000L) {
                    firebaseDB.collection("GuideCane")
                        .document(smartCaneId)
                        .update(updates)
                        .await()
                }
                Result.Success(Unit) // Update succeeded
            }
        } catch (e: TimeoutCancellationException) {
            Log.e("updateGuideCaneUser", "Timeout: $e")
            Result.Failure(Exception("Timeout or network issue"))
        } catch (e: Exception) {
            Log.e("updateGuideCaneUser", "Error: $e")
            Result.Failure(e)
        }
    }


    override suspend fun updateAppUser(appUserId: String, smartCaneId: String, securityKey: String?, add: Boolean): Result<Unit> {
        return try {
            withContext(ioDispatcher) {
                val guideCaneDocRef = firebaseDB.collection("GuideCane").document(smartCaneId)

                val snapshot = guideCaneDocRef.get().await()

                if (!snapshot.exists()) {
                    return@withContext Result.Failure(IllegalArgumentException("GuideCane document does not exist"))
                }

                val storedSecurityKey = snapshot.getString("security_key")
                val storedSmartCaneId = snapshot.getString("smart_cane_id") ?: ""

                if (storedSecurityKey != securityKey) {
                    return@withContext Result.Failure(IllegalArgumentException("Invalid Smart Cane ID or Security Key"))
                }

                if (storedSmartCaneId != smartCaneId) {
                    return@withContext Result.Failure(IllegalArgumentException("Smart Cane ID does not match"))
                }

                val appUserDocRef = firebaseDB.collection("AppUser").document(appUserId)

                firebaseDB.runTransaction { transaction ->
                    val appUserSnapshot = transaction.get(appUserDocRef)
                    val guideCaneIds = appUserSnapshot.get("guideCaneUsers") as? MutableList<String> ?: mutableListOf()

                    if (add) {
                        if (guideCaneIds.contains(smartCaneId)) {
                            throw IllegalArgumentException("The smartCaneId $smartCaneId already exists in the guideCaneUsers array.")
                        } else {
                            guideCaneIds.add(smartCaneId)
                        }
                    } else {
                        guideCaneIds.remove(smartCaneId)
                    }

                    transaction.update(appUserDocRef, "guideCaneUsers", guideCaneIds)
                }.await()

                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Log.e("updateAppUser", "Error: $e")
            Result.Failure(e)
        }
    }

    override suspend fun fetchUserHistory(smartCaneId: String): Result<List<History>> {
        return try {
            withContext(ioDispatcher) {
                Log.d("fetchUserHistory", "Fetching history for smartCaneId: $smartCaneId")
                val querySnapshot = withTimeoutOrNull(10000L) {
                    firebaseDB.collection("history")
                        .whereEqualTo("smart_cane_id", smartCaneId)
                        .get()
                        .await()
                }

                querySnapshot?.let { snapshot ->
                    Log.d("fetchUserHistory", "Query successful, processing documents...")
                    val historyList = snapshot.documents.mapNotNull { document ->
                        val documentId = document.getString("document_id") ?: ""
                        val smartCaneId = document.getString("smart_cane_id") ?: ""
                        val latitude = document.getDouble("latitude") ?: 0.0
                        val longitude = document.getDouble("longitude") ?: 0.0
                        val timestamp = document.getTimestamp("date") // Retrieve as Timestamp


                        val formattedDate = if (timestamp != null) {
                            convertDateFormat(timestamp.toDate())
                        } else {
                            "Invalid date format"
                        }


                        val history = History(
                            date = formattedDate,
                            documentId = documentId,
                            latitude = latitude,
                            longitude = longitude,
                            smartCaneId = smartCaneId
                        )
                        Log.d("fetchUserHistory", "Mapped History object: $history")
                        history
                    }
                    Log.d("fetchUserHistory", "Returning history list with ${historyList.size} items.")
                    Result.Success(historyList)
                } ?: run {
                    Log.e("fetchUserHistory", "Timeout or network issue occurred.")
                    Result.Failure(Exception("Timeout or network issue"))
                }
            }
        } catch (e: Exception) {
            Log.e("fetchUserHistory", "Error: $e")
            Result.Failure(e)
        }
    }


}
