package com.haodv.musiceat

import android.app.Activity
import android.app.Application
import com.haodv.musiceat.utils.Preference
import com.haodv.musiceat.utils.SharedPrefsHelper

class MainApp : Application() {
    var preference: Preference? = null

    //    private ApplicationComponent applicationComponent;
    var currentActivity: Activity? = null
    override fun onCreate() {
        super.onCreate()

        preference = Preference.buildInstance(this)
        val sharedPrefsHelper = SharedPrefsHelper(applicationContext)
        if (preference?.firstInstall == false) {
            preference?.firstInstall = true
            preference?.setValueCoin(100)
        }
    }

    companion object {
        private var instance: MainApp? = null
        @JvmStatic
        fun newInstance(): MainApp? {
             if (instance == null){
                instance = MainApp()

            }
             return  instance
        }
    }
}