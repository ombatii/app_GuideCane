package com.ombati.guidecaneapp.di

import android.app.Application
import com.ombati.guidecaneapp.presentation.notification.NotificationWorker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GuideCaneApp : Application() {
    override fun onCreate() {
        super.onCreate()

    }
}
