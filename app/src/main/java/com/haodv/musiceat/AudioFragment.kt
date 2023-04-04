package com.haodv.musiceat

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.haodv.musiceat.AudioController.ListenerDuration
import com.haodv.musiceat.model.Song
import com.haodv.musiceat.utils.Utils
import java.io.Serializable

class AudioFragment : Fragment(), View.OnClickListener, ListenerDuration {
    private var songDtoList: List<Song>? = arrayListOf()
    private var songDto: Song? = null
    private var pos = 0
    private var audioController: AudioController? = null
    private var imgBack: ImageView? = null
    private var imgNext: ImageView? = null
    private var imgPrevious: ImageView? = null
    private var imgPlayPause: ImageView? = null
    private var txtTime: TextView? = null
    private var txtName: TextView? = null
    private var sbTime: SeekBar? = null
    private var textTimeStart : TextView ? = null
    private var lottieAnimationView: LottieAnimationView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_aufio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        listener()
    }

    private fun initView(view: View) {
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
        data
    }

    private val data: Unit
        private get() {
            val bundle = arguments
            if (bundle != null) {
                songDtoList = (bundle.getSerializable(MainActivity.KEY_DATA) as List<Song>?)
                pos = bundle.getInt(MainActivity.KEY_POS)
            }
            songDto = songDtoList!![pos]
            audioController = AudioController(songDtoList!!, pos)
            audioController!!.setOnItemSelect(this)
            audioController!!.openMedia()
        }

    private fun listener() {
        txtName!!.text = songDto?.name
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.imgBack -> activity?.onBackPressed()
            R.id.imgNext -> audioController!!.updateEvent(MainActivity.EVENT_NEXT)
            R.id.imgPrevious -> audioController!!.updateEvent(MainActivity.EVENT_PREVIOUS)
            R.id.imgPlayPause -> controllerMedia()
        }
    }

    private fun updateView(songDto: Song) {
        txtName!!.text = songDto.name
        txtTime!!.text = songDto.artists_names
    }

    private fun controllerMedia() {
        val isPlay = !songDto!!.play
        if (isPlay) {
            imgPlayPause!!.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_pause))
            lottieAnimationView!!.playAnimation()
        } else {
            imgPlayPause!!.setImageDrawable(activity?.resources?.getDrawable(R.drawable.ic_play))
            lottieAnimationView!!.pauseAnimation()
        }
        audioController!!.updateEvent(MainActivity.EVENT_PLAY_PAUSE)
    }

    override fun duration(duration: Int,timeEnd : Int) {
        val time = duration * 100
        sbTime?.progress = time / timeEnd
        txtTime?.text = Utils.millisecondsToTime((timeEnd).toLong());
        textTimeStart?.text =  Utils.millisecondsToTime((duration).toLong());
    }




    override fun event(keyEvent: String?, songDto: Song?) {
        when (keyEvent) {
            MainActivity.EVENT_NEXT, MainActivity.EVENT_PREVIOUS -> songDto?.let { updateView(it) }
            MainActivity.EVENT_PLAY_PAUSE -> {}
        }
    }

    override fun endCoin() {
        Toast.makeText(activity, "Please purchase coin!", Toast.LENGTH_LONG).show()
        activity?.startActivity(Intent(activity, PurchaseInAppActivity::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        audioController!!.releaseMedia()
    }

    companion object {
        fun newInstance(songDtoList: List<Song?>?, pos: Int): AudioFragment {
            val audioFragment = AudioFragment()
            val bundle = Bundle()
            bundle.putSerializable(MainActivity.KEY_DATA, songDtoList as Serializable?)
            bundle.putSerializable(MainActivity.KEY_POS, pos)
            audioFragment.arguments = bundle
            return audioFragment
        }
    }
}