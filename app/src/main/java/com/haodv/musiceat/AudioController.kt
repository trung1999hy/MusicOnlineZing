package com.haodv.musiceat

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import com.haodv.musiceat.model.Song
import kotlin.random.Random

class AudioController(songDtoList: List<Song>, pos: Int ) {
    private var pos = 1
    private var mediaPlayer: MediaPlayer? = null
    private var handler: Handler? = Handler()
    private var runnable: Runnable?= null
    private var duration = 0
    private var listenerDuration: ListenerDuration? = null
    private val songDtoList: List<Song>
    private var posMax = 1
    private var path: String
    private var nextRandom : Boolean = false



    init {
        this.songDtoList = songDtoList
        path = songDtoList[pos].path.toString()
        this.pos = pos
        posMax = songDtoList.size
        initMedia()
    }

    fun openMedia() {
        playAudio(path)
    }

    private fun playAudio(path: String) {
        try {
            if (mediaPlayer != null)releaseMedia()
            initMedia()
            mediaPlayer!!.reset()
            mediaPlayer!!.setDataSource(path)
            mediaPlayer!!.prepareAsync()
            mediaPlayer!!.setOnPreparedListener {
                it.start()
                runnable?.let { it1 -> handler?.removeCallbacks(it1) }
                runnable = object : Runnable {
                    override fun run() {
                        if (mediaPlayer != null) {
                            duration = mediaPlayer!!.currentPosition
                            if (duration < mediaPlayer!!.duration) {
                                handler!!.postDelayed(this, 500)
                                listenerDuration!!.duration(duration, mediaPlayer!!.duration)
                            }
                            else handler?.postDelayed( {  eventNext() },1000 )
                        }
                    }
                }
                handler!!.post(runnable as Runnable)
            }

        } catch (exception: Exception) {
            eventNext()

        }
//
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
            if (pos >= songDtoList.size - 1) return
            mediaPlayer?.reset();
            if (nextRandom){
                pos = Random.nextInt(0,posMax)
            }else  pos++
            path = songDtoList[pos].path.toString()
            openMedia()
            listenerDuration!!.event(MainActivity.EVENT_NEXT, songDtoList[pos])
        } catch (ex: Exception) {
        }
    }

    fun getNextRandom() : Boolean = nextRandom
    fun  setNextRandom(nextRandom: Boolean){
        this.nextRandom = nextRandom
    }

    private fun eventPrevious() {
        try {
            if (pos <= 0) return
            mediaPlayer?.reset();
            pos --
            path = songDtoList[pos].path.toString()
            openMedia()
            listenerDuration!!.event(MainActivity.EVENT_PREVIOUS, songDtoList[pos])
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

    fun setOnItemSelect(listenerDuration: ListenerDuration?) {
        this.listenerDuration = listenerDuration
    }


    interface ListenerDuration {
        fun duration(duration: Int, timeEnd: Int)
        fun event(keyEvent: String?, songDto: Song?)
        fun endCoin()
    }
}