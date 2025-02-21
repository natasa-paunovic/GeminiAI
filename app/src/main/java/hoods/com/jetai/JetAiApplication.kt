package hoods.com.jetai

import android.app.Application

class JetAiApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        hoods.com.jetai.Graph.provideContext(this)
    }
}