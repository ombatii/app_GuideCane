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
import com.ombati.guidecaneapp.common.Result
import com.ombati.guidecaneapp.data.repositories.storage.GuideCaneRepository
import com.ombati.guidecaneapp.data.repositories.storage.GuideCaneRepositoryImpl
import com.ombati.guidecaneapp.presentation.home.calculateDistance
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit


class NotificationWorker (
    appContext: Context,
    params: WorkerParameters,

) : CoroutineWorker(appContext, params) {
    private val notificationHelper: NotificationHelper = NotificationHelper(appContext)
    private val guideCaneRepository: GuideCaneRepository = GuideCaneRepositoryImpl(firebaseDB = FirebaseFirestore.getInstance(),Dispatchers.IO)
    override suspend fun doWork(): Result {
        return try {
            checkBatteryStatus()
            checkEmergencyStatus()
            checkLocationProximity()

            Result.success()
        } catch (exc: Exception) {
            Log.e("NotificationWorker", "Error in doWork: $exc")
            Result.failure()
        }
    }

    private fun checkBatteryStatus() {
        val appUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (appUserId == null) {
            return
        }

        appUserId?.let {
            guideCaneRepository.fetchGuideCaneUsersForCurrentUser(appUserId) { result ->
                when (result) {
                    is com.ombati.guidecaneapp.common.Result.Failure -> {
                    }
                    is com.ombati.guidecaneapp.common.Result.Success -> {
                        val users = result.data
                        val lowBatteryUsers = mutableListOf<Pair<String, Int>>()


                        users.forEach { user ->
                            val batteryLevel = user.batteryLevel.toIntOrNull()
                            if (batteryLevel != null && batteryLevel < 15) {
                                lowBatteryUsers.add(user.id to batteryLevel)
                            } else if (batteryLevel == null) {
                                Log.e("NotificationWorker", "Invalid battery level for user: ${user.id}")
                            }
                        }

                        if (lowBatteryUsers.isNotEmpty()) {
                            val userBatteryDetails = lowBatteryUsers.joinToString("\n") {
                                "User ID: ${it.first}, Battery Level: ${it.second}%"
                            }
                            notificationHelper.showNotification(
                                "Low Battery Alerts",
                                "The following users have low battery:\n$userBatteryDetails"
                            )
                        } else {
                            notificationHelper.cancelNotification()
                        }
                    }
                }
            }
        }
    }

    private fun checkEmergencyStatus() {
        val appUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (appUserId == null) {
            return
        }

        appUserId?.let {
            guideCaneRepository.fetchGuideCaneUsersForCurrentUser(appUserId) { result ->
                when (result) {
                    is com.ombati.guidecaneapp.common.Result.Failure -> {
                    }
                    is com.ombati.guidecaneapp.common.Result.Success -> {
                        val users = result.data
                        val alertUsers = users.filter { user -> user.emergencyStatus == "Alert" }


                        if (alertUsers.isNotEmpty()) {
                            val userAlertDetails = alertUsers.joinToString("\n") {
                                "User ID: ${it.id}, Status: ${it.emergencyStatus}"
                            }

                            Log.d("NotificationWorker", "Sending emergency notification for users: ${alertUsers.map { it.id }}")

                            notificationHelper.showNotification(
                                "Emergency Alerts",
                                "The following users are in an emergency state:\n$userAlertDetails"
                            )
                        } else {
                            Log.d("NotificationWorker", "No users with emergency status 'Alert'.")
                            notificationHelper.cancelNotification()
                        }
                    }
                }
            }
        }
    }



    private fun checkLocationProximity() {
        Log.d("NotificationWorker", "DoWork called")
        val appUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (appUserId == null) {
            Log.d("NotificationWorker", "User is not authenticated.")
            return
        }

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
                WORK_NAME,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,//.Keep
                startSyncingRequest,
            )
        }
    }
}

const val WORK_NAME = "checking"