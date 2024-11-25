package com.ombati.guidecaneapp.presentation.notification


import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ombati.guidecaneapp.data.repositories.storage.GuideCaneRepository
import com.ombati.guidecaneapp.data.repositories.storage.GuideCaneRepositoryImpl
import com.ombati.guidecaneapp.presentation.home.calculateDistance
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit


class CheckLocationProximityWorker (
    appContext: Context,
    params: WorkerParameters,

    ) : CoroutineWorker(appContext, params) {
    private val notificationHelper: NotificationHelper = NotificationHelper(appContext)
    private val guideCaneRepository: GuideCaneRepository = GuideCaneRepositoryImpl(firebaseDB = FirebaseFirestore.getInstance(),Dispatchers.IO)
    val appUserId = FirebaseAuth.getInstance().currentUser?.uid
    override suspend fun doWork(): Result {
        return try {
            checkLocationProximity()

            Result.success()
        } catch (exc: Exception) {
            Log.e("NotificationWorker", "Error in doWork: $exc")
            Result.failure()
        }
    }

    private fun checkLocationProximity() {
        Log.d("NotificationWorker", "DoWork called")
        appUserId?.let {
            guideCaneRepository.fetchGuideCaneUsersForCurrentUser(appUserId) { result ->
                when (result) {
                    is com.ombati.guidecaneapp.common.Result.Failure -> {
                        Log.e("NotificationWorker", "Failed to fetch GuideCane users")
                    }
                    is com.ombati.guidecaneapp.common.Result.Success -> {
                        val users = result.data

                        val farFromGeoFencingUsers = mutableListOf<Pair<String, Float>>()
                        users.forEach { user ->
                            val geoFencingLatitude = user.geoFencingLatitude
                            val geoFencingLongitude = user.geoFencingLongitude

                            if (geoFencingLatitude != 0.0 && geoFencingLongitude != 0.0) {
                                val distance = calculateDistance(
                                    user.latitude,
                                    user.longitude,
                                    geoFencingLatitude,
                                    geoFencingLongitude
                                )
                                if (distance > 50) {
                                    Log.d("NotificationWorker", "User ${user.id} is more than 50 meters from the geofencing point.")
                                    farFromGeoFencingUsers.add(user.id to distance)
                                } else {
                                    Log.d("NotificationWorker", "User ${user.id} is within 50 meters of the geofencing point.")
                                }
                            } else {
                                Log.d("NotificationWorker", "Invalid geofencing coordinates for user: ${user.id}")
                            }
                        }

                        if (farFromGeoFencingUsers.isNotEmpty()) {
                            val userDetails = farFromGeoFencingUsers.joinToString("\n") {
                                "User ID: ${it.first}, Distance: ${it.second}M"
                            }

                            Log.d("NotificationWorker", "Sending geofencing alert for users: ${farFromGeoFencingUsers.map { it.first }}")

                            notificationHelper.showNotification(
                                "Geofencing Alert",
                                "The following users are more than 50 meters from the geofencing point:\n$userDetails"
                            )
                        } else {
                            Log.d("NotificationWorker", "All users are within 50 meters of the geofencing point.")
                            notificationHelper.cancelNotification()
                        }
                    }
                }
            }
        }
    }






    companion object {
        fun startSyncing(context: Context) {
            val constraints =
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            val startSyncingRequest =
                PeriodicWorkRequestBuilder<NotificationWorker>(
                    repeatInterval = 1,
                    repeatIntervalTimeUnit = TimeUnit.MINUTES,
                )
                    .setConstraints(constraints)
                    .build()
            Log.d("NotificationWorker","Start syncing")

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "Checking Geofencing logic",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,//.Keep
                startSyncingRequest,
            )
        }
    }
}

