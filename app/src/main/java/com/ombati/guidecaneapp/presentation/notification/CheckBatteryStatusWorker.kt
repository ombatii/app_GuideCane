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


class CheckBatteryStatusWorker (
    appContext: Context,
    params: WorkerParameters,

    ) : CoroutineWorker(appContext, params) {
    private val notificationHelper: NotificationHelper = NotificationHelper(appContext)
    private val guideCaneRepository: GuideCaneRepository = GuideCaneRepositoryImpl(firebaseDB = FirebaseFirestore.getInstance(),Dispatchers.IO)
    val appUserId = FirebaseAuth.getInstance().currentUser?.uid
    override suspend fun doWork(): Result {
        return try {
            checkBatteryStatus()
            Result.success()
        } catch (exc: Exception) {
            Log.e("NotificationWorker", "Error in doWork: $exc")
            Result.failure()
        }
    }

    private fun checkBatteryStatus() {
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
                "checking battery status",
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,//.Keep
                startSyncingRequest,
            )
        }
    }
}

