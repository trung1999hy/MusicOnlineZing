package com.trungtv.Onmuzik.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
            val actionMusic: Int = intent?.getIntExtra("action_music", 0) as Int
            val intentService = Intent(context, MusicService::class.java)
            intentService.putExtra("action_music_service", actionMusic)
            context!!.startService(intentService)
    }
}