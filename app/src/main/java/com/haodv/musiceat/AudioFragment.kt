package com.haodv.musiceat

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.DownloadManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.haodv.musiceat.databinding.FragmentAufioBinding
import com.haodv.musiceat.model.Song
import com.haodv.musiceat.service.MusicService
import com.haodv.musiceat.utils.Utils


class AudioFragment : Fragment(), View.OnClickListener {
    private var songDtoList: ArrayList<Song> = arrayListOf()
    private var songDto: Song? = null
    private var pos = 0
    private var imgBack: ImageView? = null
    private var imgNext: ImageView? = null
    private var imgPrevious: ImageView? = null
    private var imgPlayPause: ImageView? = null
    private var txtTime: TextView? = null
    private var txtName: TextView? = null
    private var sbTime: SeekBar? = null
    private var textTimeStart: TextView? = null
    private var lottieAnimationView: LottieAnimationView? = null
    private var isPlayLocal: Boolean = false
    private lateinit var musicService: MusicService
    private lateinit var binding: FragmentAufioBinding

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MyBinder
            musicService = binder.getService()
            musicService.setListenDuration(listenerDuration)
            listener()

        }

        override fun onServiceDisconnected(name: ComponentName?) {

        }
    }

    private val listenerDuration = object : com.haodv.musiceat.service.ListenerDuration {
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

        override fun unbind() {
            this@AudioFragment.unbind()
            view?.let {
                controllerMedia()
            }
        }

    }

    fun unbind() {
        if (isMyServiceRunning(MusicService::class.java))
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


    private fun initView(view: View) {
        (activity as MainActivity)?.setVisibility(View.GONE)
        imgBack = view.findViewById(R.id.imgBack)
        imgNext = view.findViewById(R.id.imgNext)
        imgPrevious = view.findViewById(R.id.imgPrevious)
        imgPlayPause = view.findViewById(R.id.imgPlayPause)
        txtTime = view.findViewById(R.id.txtTimeEnd)
        textTimeStart = view.findViewById(R.id.txtTimeStart)
        txtName = view.findViewById(R.id.txtName)
        sbTime = view.findViewById(R.id.sbTime)
        lottieAnimationView = view.findViewById(R.id.lottieAnimationView)
        imgBack?.setOnClickListener(this)
        imgNext?.setOnClickListener(this)
        imgPrevious?.setOnClickListener(this)
        imgPlayPause?.setOnClickListener(this)
        binding.imgLoop.setOnClickListener(this)
        val intent = Intent(context, MusicService::class.java)
        intent.putExtra("listSong", songDtoList)
        intent.putExtra("position", pos)
        context?.startService(intent)
        progessbarListenr()
        context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        if (MainApp.newInstance()?.preference?.getNextRandom() == true)
            binding.imgLoop.setImageDrawable(context?.getDrawable(R.drawable.ic_lap_enable))
        else
            binding.imgLoop.setImageDrawable(
                context?.getDrawable(R.drawable.ic_lap)
            )
        downlod()
        binding.imgDownload.visibility = if (isPlayLocal) View.GONE else View.VISIBLE
    }


    private fun listener() {
        txtName!!.text =
            "Bạn đang nghe bài hát hát " + musicService.getSong()?.name + " do ca sĩ " + musicService.getSong()?.performer + " thể hiện"
        txtName?.isSelected = true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgBack -> if (activity?.supportFragmentManager?.backStackEntryCount!! > 0) activity?.supportFragmentManager?.popBackStack() else activity?.onBackPressed()
            R.id.imgNext -> enventNext()
            R.id.imgPrevious -> eventPrevious()
            R.id.imgPlayPause -> isPlayPause()
            R.id.img_loop -> imgNextRandom()
        }
    }

    private fun isPlayPause() {
        if (!isMyServiceRunning(MusicService::class.java)) {
            val intent = Intent(context, MusicService::class.java)
            intent.putExtra("listSong", songDtoList)
            intent.putExtra("position", pos)
            context?.startService(intent)
            context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        } else {
            musicService.playPauseMedia()
            controllerMedia()
        }
    }

    private fun eventPrevious() {
        if (!isMyServiceRunning(MusicService::class.java)) {
            val intent = Intent(context, MusicService::class.java)
            intent.putExtra("listSong", songDtoList)
            intent.putExtra("position", if (pos > 0) pos-- else songDtoList.size - 1)
            context?.startService(intent)
            context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        } else {
            musicService.eventPrevious()
            listener()
        }

    }

    private fun enventNext() {
        if (!isMyServiceRunning(MusicService::class.java)) {
            val intent = Intent(context, MusicService::class.java)
            intent.putExtra("listSong", songDtoList)
            intent.putExtra("position", if (pos < songDtoList.size) pos++ else 0)
            context?.startService(intent)
            context?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        } else {
            musicService.eventNext()
            listener()
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager =
            requireActivity()?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun imgNextRandom() {
        musicService?.setNextRandom(MainApp.newInstance()?.preference?.getNextRandom() == true)
        MainApp.newInstance()?.preference?.setNextRandom(MainApp.newInstance()?.preference?.getNextRandom() != true)
        if (MainApp.newInstance()?.preference?.getNextRandom() == true)
            context?.let { Glide.with(it).load(R.drawable.ic_lap_enable).into(binding.imgLoop) }
        else
            context?.let { Glide.with(it).load(R.drawable.ic_lap).into(binding.imgLoop) }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun downlod() {
        binding.mainWv.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                var url = url
                super.shouldOverrideUrlLoading(view, url)
                if (url.contains("http://")) {
                    url = url.replace("http://", "https://")
                }
                val uri = Uri.parse(url)
                songDto?.let { downloadData(uri, it) }
                return true
            }
        }
        binding.mainWv.settings.javaScriptEnabled = true
        binding.imgDownload.setOnClickListener(View.OnClickListener { view: View? ->
            MainApp.newInstance()?.preference?.apply {
                if (this.getValueCoin() > 1) {
                    this.setValueCoin(getValueCoin() - 1)
                    val coin = MainApp.newInstance()?.preference?.getValueCoin()
                    (activity as MainActivity).txtCoin?.text =
                        String.format(resources.getString(R.string.value_coin), coin)
                    songDto?.path?.let { binding.mainWv.loadUrl(it) }
                    Toast.makeText(
                        context,
                        " Bạn đã mở mua lượt mở khóa thành công - 1 coin ",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Bạn đã hết coin vui lòng mua thêm coin",
                        Toast.LENGTH_LONG
                    ).show()
                    startActivity(Intent(requireActivity(), PurchaseInAppActivity::class.java))
                }
            }

        })
    }

    var downloadManager: DownloadManager? = null
    private fun downloadData(uri: Uri, s: Song) {
        downloadManager =
            requireActivity()!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(uri)
        request.setTitle("MP3Online download " + s.name)
        request.setDescription("MP3Online downloading " + s.name)
        request.setAllowedOverRoaming(false)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            s.name + ".mp3"
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadManager!!.enqueue(request)
    }


    private fun updateView(songDto: Song) {
        txtName!!.text = songDto.name
    }

    private fun controllerMedia() {
        val isPlay = musicService.getSong()?.play ?: false
        if (isPlay) {
            imgPlayPause!!.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_pause))
            lottieAnimationView!!.playAnimation()
        } else {
            imgPlayPause!!.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_play))
            lottieAnimationView!!.pauseAnimation()
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
        }
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