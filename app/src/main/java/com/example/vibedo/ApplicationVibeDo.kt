package com.example.vibedo

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ApplicationVibeDo: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}