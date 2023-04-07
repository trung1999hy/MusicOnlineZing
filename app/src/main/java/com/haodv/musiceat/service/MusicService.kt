package com.haodv.musiceat.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.*
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.haodv.musiceat.MainActivity
import com.haodv.musiceat.MainApp
import com.haodv.musiceat.R
import com.haodv.musiceat.model.Song
import kotlin.random.Random


class MusicService : Service() {
    private var position = 1
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = Handler()
    private var runnable: Runnable? = null
    private var duration = 0
    private val CHANNEL_ID = "Music"
    val notificationId = 996
    private var posMax = 1
    private lateinit var path: String
    private var nextRandom: Boolean = false
    private var context: Context? = null
    private lateinit var listSong: ArrayList<Song>
    private lateinit var mediaSessionCompat: MediaSessionCompat
    private lateinit var notificationCompat: NotificationCompat.Builder
    private lateinit var notificationManager: NotificationManager
    private val binder = MyBinder()
    private lateinit var listenerDuration: ListenerDuration


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val actionMusicReceiver: Int = intent?.getIntExtra("action_music_service", 0) as Int
        if (actionMusicReceiver == 0) {
            listSong = intent?.extras?.get("listSong") as ArrayList<Song>
            position = intent?.extras?.get("position") as Int
            if (listSong.size > 0) {
                path = listSong[position].path.toString()
                openMedia()
                createNotificationChannel(listSong[position])
            }

        }
        handleActionMusic(actionMusicReceiver)

        return START_STICKY
    }

    private fun createNotificationChannel(song: Song) {
        mediaSessionCompat = MediaSessionCompat(this, "tag")
        startForeground(notificationId, build(song, mediaSessionCompat).build())
    }
    fun setListenDuration(listenerDuration: ListenerDuration){
        this.listenerDuration = listenerDuration
    }

    fun build(song: Song, mediaSessionCompat: MediaSessionCompat): NotificationCompat.Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.post_notifi)
            val descriptionText = getString(R.string.channel_post_notifi)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
        notificationCompat = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music)
            .setSilent(true)
            .setSound(null)
            .setContentText(listSong[position].performer)
            .setContentTitle(listSong[position].name)
        if (listSong[position].play) {
            notificationCompat.addAction(
                R.drawable.ic_skip_back,
                "Previous",
                pendingIntent(this, ACTION_PREVIOUS)
            )
                .addAction(R.drawable.ic_pause, "Pause", pendingIntent(this, ACTION_RESUM))
                .addAction(R.drawable.ic_skip_forward, "Next", pendingIntent(this, ACTION_NEXT))
                .addAction(R.drawable.ic_x_black, "Close", pendingIntent(this, ACTION_CLOSE))
        } else {
            notificationCompat.addAction(
                R.drawable.ic_skip_back,
                "Previous",
                pendingIntent(this, ACTION_PREVIOUS)
            )
                .addAction(R.drawable.ic_play, "Play", pendingIntent(this, ACTION_PLAY))
                .addAction(R.drawable.ic_skip_forward, "Next", pendingIntent(this, ACTION_NEXT))
                .addAction(R.drawable.ic_x_black, "Close", pendingIntent(this, ACTION_CLOSE))
        }
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
                    .setMediaSession(mediaSessionCompat.sessionToken)
            )

        song.thumbnail?.let { setImage(it) }
        return notificationCompat
    }

    fun changeNotification() {
        notificationCompat.setContentText(listSong[position].performer)
            .setContentTitle(listSong[position].name)
        notificationCompat.apply {
            if (listSong[position].play) {
                notificationCompat.addAction(
                    R.drawable.ic_skip_back,
                    "Previous",
                    pendingIntent(this@MusicService, ACTION_PREVIOUS)
                )
                addAction(
                    R.drawable.ic_pause,
                    "Pause",
                    pendingIntent(this@MusicService, ACTION_RESUM)
                )
                addAction(
                    R.drawable.ic_skip_forward,
                    "Next",
                    pendingIntent(this@MusicService, ACTION_NEXT)
                )
                addAction(
                    R.drawable.ic_x_black,
                    "Close",
                    pendingIntent(this@MusicService, ACTION_CLOSE)
                )
            } else {
                addAction(
                    R.drawable.ic_skip_back,
                    "Previous",
                    pendingIntent(this@MusicService, ACTION_PREVIOUS)
                )
                addAction(
                    R.drawable.ic_play,
                    "Play",
                    pendingIntent(this@MusicService, ACTION_PLAY)
                )
                addAction(
                    R.drawable.ic_skip_forward,
                    "Next",
                    pendingIntent(this@MusicService, ACTION_NEXT)
                )
                addAction(
                    R.drawable.ic_x_black,
                    "Close",
                    pendingIntent(this@MusicService, ACTION_CLOSE)
                )
            }
        }.setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setShowActionsInCompactView(0, 1, 2)
                .setMediaSession(mediaSessionCompat.sessionToken)
        )
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(1, 2, 3)
                    .setMediaSession(mediaSessionCompat.sessionToken)
            )
        createNotificationChannel(listSong[position])
    }

    fun setImage(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url).placeholder(R.drawable.music)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                ) {
                    notificationCompat.setLargeIcon(resource)
                    notificationManager.notify(notificationId, notificationCompat.build())
                }

            })
    }

    fun handleActionMusic(action: Int) {
        when (action) {
            ACTION_PREVIOUS -> {
                eventPrevious()
                sendActionActivity(ACTION_PREVIOUS)

            }
            ACTION_PLAY -> {
                playPauseMedia()
                sendActionActivity(ACTION_PLAY)


            }
            ACTION_RESUM -> {
                playPauseMedia()
                sendActionActivity(ACTION_RESUM)

            }
            ACTION_NEXT -> {
                eventNext()
                sendActionActivity(ACTION_NEXT)


            }
            ACTION_CLOSE -> {
                stopSelf()
            }
        }
    }

    inner class MyBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }


    fun sendActionActivity(action: Int) {
        val intent = Intent("send_data_to_activity")
        val bundle = Bundle()
        bundle.putSerializable("song", listSong[position])
        bundle.putBoolean("status", mediaPlayer?.isPlaying == true)
        bundle.putInt("action", action)
        intent.putExtras(bundle)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    fun pendingIntent(context: Context, action: Int): PendingIntent {
        val intent = Intent(applicationContext, MusicReceiver::class.java)
        intent.putExtra("action_music", action)
        return PendingIntent.getBroadcast(
            context.applicationContext,
            action,
            intent,
            PendingIntent.FLAG_MUTABLE
        )
    }

    fun openMedia() {
        playAudio(path)
    }

    private fun playAudio(path: String) {
        try {
            if (mediaPlayer != null) releaseMedia()
            initMedia()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(path)
            mediaPlayer!!.prepareAsync()
            mediaPlayer!!.setOnPreparedListener {
                it.start()
                listSong[position].play = true
                createNotificationChannel(listSong.get(position))
                runnable?.let { it1 -> handler?.removeCallbacks(it1) }
                runnable = object : Runnable {
                    override fun run() {
                        if (mediaPlayer != null) {
                            duration = mediaPlayer!!.currentPosition
                            if (mediaPlayer?.duration ?: 0 > 0) {
                                if (duration < mediaPlayer!!.duration) {
                                    handler!!.postDelayed(this, 500)
                                    notificationCompat.setProgress(
                                        100,
                                        duration / mediaPlayer!!.duration * 100,
                                        false
                                    )
                                    listenerDuration?.duration(duration, mediaPlayer!!.duration)
                                    notificationManager.notify(
                                        notificationId,
                                        notificationCompat.build()
                                    )
                                } else handler?.postDelayed({ eventNext() }, 1000)
                            } else {
                                notificationCompat.setProgress(100, 0, false)
                                notificationManager.notify(
                                    notificationId,
                                    notificationCompat.build()
                                )
                                handler!!.postDelayed(this, 500)
                            }
                        }
                    }
                }
                handler!!.post(runnable as Runnable)
            }

        } catch (exception: Exception) {
            eventNext()
        }

    }

    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                mediaPlayer?.start()
                listSong[position].play = true
                listenerDuration!!.event(MainActivity.EVENT_PLAY_PAUSE, listSong.get(position))
                createNotificationChannel(listSong.get(position))
            }
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaPlayer?.pause()
                listSong[position].play = false
                listenerDuration!!.event(MainActivity.EVENT_PLAY_PAUSE, listSong.get(position))
                createNotificationChannel(listSong.get(position))
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                mediaPlayer?.pause()
                listSong[position].play = false
                listenerDuration!!.event(MainActivity.EVENT_PLAY_PAUSE, listSong.get(position))
                createNotificationChannel(listSong.get(position))
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                mediaPlayer?.pause()
                listSong[position].play = false
                listenerDuration!!.event(MainActivity.EVENT_PLAY_PAUSE, listSong.get(position))
                createNotificationChannel(listSong.get(position))

            }
            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE -> {
                mediaPlayer?.start()
                listSong[position].play = true
                listenerDuration!!.event(MainActivity.EVENT_PLAY_PAUSE, listSong.get(position))
                createNotificationChannel(listSong.get(position))

            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    fun playPauseMedia() {
        val songDto = listSong[position]
        val isPlay = songDto.play
        if (isPlay) {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) mediaPlayer!!.pause()
        } else {
            if (mediaPlayer != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    MainApp.newInstance()?.getAudioManager()?.requestAudioFocus(
                        AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                            .setAudioAttributes(
                                AudioAttributes.Builder()
                                    .setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                    .build()
                            )
                            .setAcceptsDelayedFocusGain(true)
                            .setOnAudioFocusChangeListener(audioFocusChangeListener).build()
                    )
                } else {
                    MainApp.newInstance()?.getAudioManager()?.requestAudioFocus(
                        audioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN
                    )
                }
                mediaPlayer?.start()
            }
        }
        listSong[position].play = !isPlay
        listenerDuration!!.event(MainActivity.EVENT_PLAY_PAUSE, listSong.get(position))
        createNotificationChannel(listSong[position])
    }

    fun eventNext() {
        try {
            if (position >= listSong.size - 1) return
            mediaPlayer?.reset();
            if (nextRandom) {
                position = Random.nextInt(0, posMax)
            } else position++
            path = listSong[position].path.toString()
            openMedia()
            listenerDuration!!.event(MainActivity.EVENT_NEXT, listSong.get(position))
            createNotificationChannel(listSong.get(position))

        } catch (ex: Exception) {
        }
    }

    fun getSong(): Song = listSong[position]

    fun setNextRandom(nextRandom: Boolean) {
        this.nextRandom = nextRandom
    }

    fun eventPrevious() {
        try {
            if (position <= 0) return
            mediaPlayer?.reset();
            position--
            path = listSong.get(position).path.toString()
            openMedia()
            listenerDuration!!.event(MainActivity.EVENT_PREVIOUS, listSong.get(position))
            changeNotification()
            createNotificationChannel(listSong.get(position))
        } catch (ex: Exception) {
        }
    }

    private fun initMedia() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }
        }
    }

    fun releaseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        mediaPlayer = null
        listSong = arrayListOf()
        stopForeground(notificationId)
    }

    companion object {
        val ACTION_PREVIOUS = 1
        val ACTION_PLAY = 2
        val ACTION_RESUM = 3
        val ACTION_NEXT = 4
        val ACTION_CLOSE = 5
    }

}
interface ListenerDuration {
    fun duration(duration: Int, timeEnd: Int)
    fun event(keyEvent: String?, songDto: Song?)
    fun endCoin()
}