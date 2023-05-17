package com.trungtv.Onmuzik.audio

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.trungtv.Onmuzik.MainActivity
import com.trungtv.Onmuzik.MainApp
import com.trungtv.Onmuzik.MainViewModel
import com.trungtv.Onmuzik.PurchaseInAppActivity
import com.trungtv.Onmuzik.R
import com.trungtv.Onmuzik.databinding.FragmentAufioBinding
import com.trungtv.Onmuzik.lyric.LyricFragment
import com.trungtv.Onmuzik.lyric.LyricsAdapter
import com.trungtv.Onmuzik.model.LyricLine
import com.trungtv.Onmuzik.model.Song
import com.trungtv.Onmuzik.nolyric.NoLyricFragment
import com.trungtv.Onmuzik.service.ListenerDuration
import com.trungtv.Onmuzik.service.MusicService
import com.trungtv.Onmuzik.utils.Utils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class AudioFragment : Fragment(), View.OnClickListener {
    private var songDtoList: ArrayList<Song> = arrayListOf()
    private var pos = 0
    private var imgBack: ImageView? = null
    private var imgNext: ImageView? = null
    private var imgPrevious: ImageView? = null
    private var imgPlayPause: ImageView? = null
    private var txtTime: TextView? = null
    private var txtName: TextView? = null
    private var sbTime: SeekBar? = null
    private var textTimeStart: TextView? = null
    private var isPlayLocal: Boolean = false
    private lateinit var musicService: MusicService
    private lateinit var binding: FragmentAufioBinding
    private val noLyricFragment = NoLyricFragment.newInstance()
    private lateinit var lyricFragment : LyricFragment
      var lyricsAdapter: LyricsAdapter =  LyricsAdapter()
    private lateinit var audioViewPager : AudioViewPager

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MyBinder
            musicService = binder.getService()
            musicService.isBinder = true
            musicService.setListenDuration(listenerDuration)
            listener()
        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }
    private val viewModel: MainViewModel by activityViewModels()

    private val listenerDuration = object : ListenerDuration {
        override fun duration(duration: Int, timeEnd: Int) {
            view?.let {
                this@AudioFragment.duration(duration, timeEnd)
            }
        }

        override fun event(keyEvent: String?, songDto: Song?) {
            view?.let {
                this@AudioFragment.event(keyEvent, songDto)
            }
        }

        override fun endCoin() {

        }

        override fun unbind(isBinder: Boolean) {
            if (MainApp.newInstance()?.isMyServiceRunning(MusicService::class.java) == true)
                if (isBinder) {
                    unBind()
                }
        }
    }

    fun unBind() {
        context?.unbindService(connection)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAufioBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
    }


    override fun onDestroyView() {
        musicService.isBinder = false
        super.onDestroyView()
        context?.unbindService(connection)


    }

    override fun onStart() {
        super.onStart()
        if (MainApp.newInstance()?.isMyServiceRunning(MusicService::class.java) == true) {
            val intent = Intent(context, MusicService::class.java)
            context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
    private fun  setViewPager(){
     audioViewPager = AudioViewPager(childFragmentManager)
     binding.viewPager.adapter= audioViewPager
        audioViewPager.setData(arrayListOf(noLyricFragment, lyricFragment))
        binding.indicator.setViewPager(binding.viewPager)
        audioViewPager.registerDataSetObserver(binding.indicator.dataSetObserver);
    }

    private fun initView(view: View) {
        lyricFragment = LyricFragment.newInstance()
        setViewPager()
        (activity as MainActivity)?.setVisibility(View.GONE)
        imgBack = view.findViewById(R.id.imgBack)
        imgNext = view.findViewById(R.id.imgNext)
        imgPrevious = view.findViewById(R.id.imgPrevious)
        imgPlayPause = view.findViewById(R.id.imgPlayPause)
        txtTime = view.findViewById(R.id.txtTimeEnd)
        textTimeStart = view.findViewById(R.id.txtTimeStart)
        txtName = view.findViewById(R.id.txtName)
        sbTime = view.findViewById(R.id.sbTime)
        imgBack?.setOnClickListener(this)
        imgNext?.setOnClickListener(this)
        imgPrevious?.setOnClickListener(this)
        imgPlayPause?.setOnClickListener(this)
        binding.imgLoop.setOnClickListener(this)
        startServiceMusic(songDtoList, pos)
        if (MainApp.newInstance()?.preference?.getNextRandom() == true)
            binding.imgLoop.setImageDrawable(context?.getDrawable(R.drawable.ic_lap_enable))
        else
            binding.imgLoop.setImageDrawable(
                context?.getDrawable(R.drawable.ic_lap)
            )
        Glide.with(requireContext())
            .load(if (songDtoList[pos].like) R.drawable.heart else R.drawable.love)
            .into(binding.imgStar)
        setClickLike()

    }


    private fun listener() {
        txtName!!.text =
            "Bạn đang nghe bài hát hát " + musicService.getSong()?.name + " do ca sĩ " + musicService.getSong()?.performer + " thể hiện"
        txtName?.isSelected = true
        musicService.getSong()?.lyric?.let { parse(it) }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgBack -> (activity as MainActivity).onBackPressed()
            R.id.imgNext -> enventNext()
            R.id.imgPrevious -> eventPrevious()
            R.id.imgPlayPause -> isPlayPause()
            R.id.img_loop -> imgNextRandom()
        }
    }

    fun startServiceMusic(songDtoList: ArrayList<Song>, pos: Int) {
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra("listSong", songDtoList)
        intent.putExtra("position", pos)
        context?.startService(intent)
        context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun isPlayPause() {
        if (MainApp.newInstance()?.isMyServiceRunning(MusicService::class.java) == false) {
            startServiceMusic(songDtoList, pos)
        } else {
            musicService.playPauseMedia()
        }
    }

    private fun setClickLike() {
        binding.imgStar.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
            if (songDtoList.get(pos).like) {
                dialog.setMessage("Bạn có muốn xóa khỏi danh sách yêu thích không?")
                    .setTitle("Xóa ?")
            } else {
                dialog.setMessage("Bạn có muốn thêm vào danh sách yêu thích không và trừ 1 vàng ?")
                dialog.setTitle("Thêm ? ")
            }

            dialog.setPositiveButton("Oke") { dialog, which ->
                if (!songDtoList.get(pos).like) {
                    MainApp.newInstance()?.preference?.apply {
                        if (getValueCoin() > 1) {
                            setValueCoin(getValueCoin() - 1)
                            val song = songDtoList.get(pos).apply {
                                this.like = !like
                            }
                            viewModel.upDateSong(pos, song)
                            (activity as? MainActivity)?.txtCoin?.text = String.format(
                                resources.getString(R.string.value_coin),
                                getValueCoin()
                            )
                            Glide.with(requireContext())
                                .load(if (songDtoList.get(pos).like) R.drawable.heart else R.drawable.love)
                                .into(binding.imgStar)

                            Toast.makeText(
                                requireContext(),
                                "Đã thêm  thành công và trù 1 vàng",
                                Toast.LENGTH_SHORT

                            ).show()
                        } else startActivity(
                            Intent(
                                requireContext(),
                                PurchaseInAppActivity::class.java
                            )
                        )
                    }
                } else {
                    val song = songDtoList[pos].apply {
                        this.like = !like
                    }
                    viewModel.upDateSong(pos, song)
                    Glide.with(requireContext())
                        .load(if (songDtoList[pos].like) R.drawable.heart else R.drawable.love)
                        .into(binding.imgStar)

                    Toast.makeText(requireContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()

            }
            dialog.setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }.create()
            dialog.show()


        }
    }

    fun parse(fileUrl: String) {
        val lyrics = arrayListOf<LyricLine>()
        Thread {
            val url = URL(fileUrl)
            val connection = url.openConnection()
            val reader =
                BufferedReader(InputStreamReader(connection.getInputStream()))

            var line: String? = ""

            while (reader.readLine().also { line = it } != null) {
                val lyric = parseLyric(line)
                if (lyric != null) {
                    lyrics.add(lyric)
                }
            }
            reader.close()
           requireActivity().runOnUiThread {
                lyricFragment.setDataLyric(lyrics)
            }
        }.start()

    }

    private fun parseLyric(line: String?): LyricLine? {
        // Định dạng mẫu của một dòng lời trong file LRC là "[mm:ss.SS]lyric_text"
        val regex = Regex("\\[(\\d+):(\\d+).(\\d+)](.+)")
        val matchResult = regex.find(line ?: "") ?: return null

        val minutes = matchResult.groupValues[1].toInt()
        val seconds = matchResult.groupValues[2].toInt()
        val milliseconds = matchResult.groupValues[3].toInt()
        val text = matchResult.groupValues[4]

        val timeInMillis = (minutes * 60 * 1000) + (seconds * 1000) + milliseconds

        return LyricLine(text, timeInMillis)
    }

    private fun eventPrevious() {
        if (MainApp.newInstance()?.isMyServiceRunning(MusicService::class.java) == false) {
            startServiceMusic(songDtoList, if (pos > 0) pos-- else songDtoList.size - 1)
        } else {
            musicService.eventPrevious()
            listener()
        }

    }

    private fun enventNext() {
        if (MainApp.newInstance()?.isMyServiceRunning(MusicService::class.java) == false) {
            startServiceMusic(songDtoList, if (pos < songDtoList.size) pos++ else 0)
        } else {
            musicService.eventNext()
            listener()
        }
    }


    private fun imgNextRandom() {
        musicService?.setNextRandom(MainApp.newInstance()?.preference?.getNextRandom() == true)
        MainApp.newInstance()?.preference?.setNextRandom(MainApp.newInstance()?.preference?.getNextRandom() != true)
        if (MainApp.newInstance()?.preference?.getNextRandom() == true)
            context?.let { Glide.with(it).load(R.drawable.ic_lap_enable).into(binding.imgLoop) }
        else
            context?.let { Glide.with(it).load(R.drawable.ic_lap).into(binding.imgLoop) }
    }


    private fun controllerMedia() {
        val isPlay = musicService.getSong()?.play ?: false
        if (isPlay) {
            imgPlayPause!!.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_pause))

        } else {
            imgPlayPause!!.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_play))

        }
    }

    fun progessbarListenr() {
        sbTime?.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
    }

    fun duration(duration: Int, timeEnd: Int) {
        val time = duration * 100
        if (timeEnd != 0)
            sbTime?.progress = time / timeEnd
        else sbTime?.progress = 0
        txtTime?.text = Utils.millisecondsToTime((timeEnd).toLong());
        textTimeStart?.text = Utils.millisecondsToTime((duration).toLong())
    }


    fun event(keyEvent: String?, songDto: Song?) {
        when (keyEvent) {
            MainActivity.EVENT_NEXT, MainActivity.EVENT_PREVIOUS -> listener()
            MainActivity.EVENT_PLAY_PAUSE -> controllerMedia()
            MainActivity.EVENT_CLOSE -> cloes()
        }
    }

    private fun cloes() {
        imgPlayPause!!.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_play))
    }

    fun endCoin() {
        Toast.makeText(activity, "Please purchase coin!", Toast.LENGTH_LONG).show()
        activity?.startActivity(Intent(activity, PurchaseInAppActivity::class.java))
    }


    companion object {
        fun newInstance(
            songDtoList: ArrayList<Song>,
            pos: Int,
            isPlayLocal: Boolean = false
        ): AudioFragment {
            val audioFragment = AudioFragment()
            audioFragment.songDtoList = songDtoList
            audioFragment.pos = pos
            audioFragment.isPlayLocal = isPlayLocal
            return audioFragment
        }
    }


}