package com.haodv.musiceat

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import com.haodv.musiceat.MainApp.Companion.newInstance
import com.haodv.musiceat.model.Song

class AudioController(songDtoList: List<Song>, pos: Int) {
    private var pos = 1
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    private var duration = 0
    private var listenerDuration: ListenerDuration? = null
    private val songDtoList: List<Song>
    private var posMax = 1
    private var path : String


    init {
        this.songDtoList = songDtoList
        path = "http://api.mp3.zing.vn/api/streaming/audio/${songDtoList[pos].id}/320"
        this.pos = pos
        posMax = songDtoList.size
        initMedia()
    }

    fun openMedia() {
        playAudio(path)

    }

    private fun playAudio(path: String ) {
        initMedia()
        try {
            if (mediaPlayer!!.isPlaying) {
                releaseMedia()
            }
            initMedia()
            mediaPlayer!!.setDataSource(path)
            mediaPlayer!!.prepareAsync()
            mediaPlayer!!.setOnPreparedListener {
                it.start()
                handler = Handler()
                runnable = object : Runnable {
                    override fun run() {
                        if (mediaPlayer != null) {
                            duration = mediaPlayer!!.currentPosition
                            if (duration < mediaPlayer!!.duration) {
                                handler!!.postDelayed(this, 500)
                                listenerDuration!!.duration(duration , mediaPlayer!!.duration)
                            } else releaseMedia()
                        }
                    }
                }
                handler!!.post(runnable as Runnable)
            }

        } catch (exception: Exception) {
        }
        val mediaPlayer =
            mediaPlayer!!.setOnCompletionListener { player ->
                val coin = newInstance()!!.preference!!.getValueCoin()
                if (coin > 1) newInstance()!!.preference!!.setValueCoin(coin - 1)
                player.reset()
                updateEvent(MainActivity.EVENT_NEXT)
            }
    }

    fun updateEvent(event: String?) {
        when (event) {
            MainActivity.EVENT_NEXT -> eventNext()
            MainActivity.EVENT_PREVIOUS -> eventPrevious()
            MainActivity.EVENT_PLAY_PAUSE -> playPauseMedia()
        }
        listenerDuration!!.event(event, songDtoList[pos])
    }

    private fun playPauseMedia() {
        val songDto = songDtoList[pos]
        val isPlay = songDto.play
        if (isPlay) {
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) mediaPlayer!!.pause()
        } else {
            if (mediaPlayer != null) mediaPlayer!!.start()
        }
        songDtoList[pos].play = !isPlay
    }

    private fun eventNext() {
        try {
            if (pos >= posMax - 1) return
            //            mediaPlayer.reset();
            pos = pos + 1
            path = "http://api.mp3.zing.vn/api/streaming/audio/${songDtoList[pos].id}/320"
            //            mediaPlayer.setDataSource(path);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
            openMedia()
        } catch (ex: Exception) {
        }
    }

    private fun eventPrevious() {
        try {
            if (pos <= 0) return
            //            mediaPlayer.reset();
            pos = pos - 1
            path = "http://api.mp3.zing.vn/api/streaming/audio/${songDtoList[pos].id}/320"
            //            mediaPlayer.setDataSource(path);
//            mediaPlayer.prepare();
//            mediaPlayer.start();
            openMedia()
        } catch (ex: Exception) {
        }
    }

    private fun initMedia() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
    }

    fun releaseMedia() {
        if (mediaPlayer != null) {
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    fun setOnItemSelect(listenerDuration: ListenerDuration?) {
        this.listenerDuration = listenerDuration
    }


    interface ListenerDuration {
        fun duration(duration: Int ,timeEnd : Int)
        fun event(keyEvent: String?, songDto: Song?)
        fun endCoin()
    }
}