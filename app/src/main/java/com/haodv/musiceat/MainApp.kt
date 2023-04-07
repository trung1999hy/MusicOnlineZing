package com.haodv.musiceat

import android.app.Application
import android.content.Context
import android.media.AudioManager
import com.haodv.musiceat.utils.Preference

class MainApp : Application() {
    var preference: Preference? = null
    private lateinit var audioManager  : AudioManager

    override fun onCreate() {
        super.onCreate()
        instance = this
         audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        preference = Preference.buildInstance(this)
        if (preference?.firstInstall == false) {
            preference?.firstInstall = true
            preference?.setValueCoin(100)
        }
    }
    fun getAudioManager (): AudioManager = audioManager


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