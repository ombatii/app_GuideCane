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
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.TimeUnit


class CheckEmergencyStatusWorker (
    appContext: Context,
    params: WorkerParameters,

    ) : CoroutineWorker(appContext, params) {
    private val notificationHelper: NotificationHelper = NotificationHelper(appContext)
    private val guideCaneRepository: GuideCaneRepository = GuideCaneRepositoryImpl(firebaseDB = FirebaseFirestore.getInstance(),Dispatchers.IO)
    val appUserId = FirebaseAuth.getInstance().currentUser?.uid
    override suspend fun doWork(): Result {
        return try {
            checkEmergencyStatus()

            Result.success()
        } catch (exc: Exception) {
            Log.e("NotificationWorker", "Error in doWork: $exc")
            Result.failure()
        }
    }


    private fun checkEmergencyStatus() {
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
                "Checking Emergency Status",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,//.Keep
                startSyncingRequest,
            )
        }
    }
}
