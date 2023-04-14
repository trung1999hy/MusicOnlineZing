package com.haodv.musiceat

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
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.haodv.musiceat.databinding.FragmentAufioBinding
import com.haodv.musiceat.model.Song
import com.haodv.musiceat.service.ListenerDuration
import com.haodv.musiceat.service.MusicService
import com.haodv.musiceat.utils.Utils


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
    private var lottieAnimationView: LottieAnimationView? = null
    private var isPlayLocal: Boolean = false
    private lateinit var musicService: MusicService
    private lateinit var binding: FragmentAufioBinding

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
        startServiceMusic(songDtoList, pos)
        if (MainApp.newInstance()?.preference?.getNextRandom() == true)
            binding.imgLoop.setImageDrawable(context?.getDrawable(R.drawable.ic_lap_enable))
        else
            binding.imgLoop.setImageDrawable(
                context?.getDrawable(R.drawable.ic_lap)
            )
        Glide.with(requireContext())
            .load(if (songDtoList.get(pos).like) R.drawable.star_slect else R.drawable.star)
            .into(binding.imgStar)
        setClickLike()
    }


    private fun listener() {
        txtName!!.text =
            "Bạn đang nghe bài hát hát " + musicService.getSong()?.name + " do ca sĩ " + musicService.getSong()?.performer + " thể hiện"
        txtName?.isSelected = true
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
                            (activity as? MainActivity)?.txtCoin?.text = String.format(resources.getString(R.string.value_coin), getValueCoin())
                            Glide.with(requireContext())
                                .load(if (songDtoList.get(pos).like) R.drawable.star_slect else R.drawable.star)
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
                        .load(if (songDtoList[pos].like) R.drawable.star_slect else R.drawable.star)
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
        MainActivity.EVENT_CLOSE -> cloes()
    }
}

private fun cloes() {
    imgPlayPause!!.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_play))
    lottieAnimationView!!.pauseAnimation()
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