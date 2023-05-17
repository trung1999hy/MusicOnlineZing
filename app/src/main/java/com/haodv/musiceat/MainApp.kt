package com.haodv.musiceat

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.media.AudioManager
import com.haodv.musiceat.utils.Preference

class MainApp : Application() {
    var preference: Preference? = null
    private lateinit var audioManager: AudioManager

    override fun onCreate() {
        super.onCreate()
        instance = this
         audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        preference = Preference.buildInstance(this)
        if (preference?.firstInstall == false) {
            preference?.firstInstall = true
            preference?.setValueCoin(100)
        }
    }

    fun getAudioManager(): AudioManager = audioManager


    fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            this.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        if (manager != null) {
            for (service in manager.getRunningServices(Int.MAX_VALUE)) {
                if (serviceClass.name == service.service.className) {
                    return true
                }
            }
        }
        return false
    }

    companion object {
        private var instance: MainApp? = null

        @JvmStatic
        fun newInstance(): MainApp? {
            if (instance == null) {
                instance = MainApp()
            }
            return instance
        }
    }
}