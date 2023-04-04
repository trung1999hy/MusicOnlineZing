package com.haodv.musiceat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.haodv.musiceat.AudioAdapter.OnItemSelect
import com.haodv.musiceat.MainApp.Companion.newInstance
import com.haodv.musiceat.di.Api
import com.haodv.musiceat.di.Network
import com.haodv.musiceat.model.Music
import com.haodv.musiceat.model.Song
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), OnItemSelect {
    private var listData: RecyclerView? = null
    private var audioAdapter: AudioAdapter? = null
    private var frameLayout: FrameLayout? = null
    private var txtCoin: TextView? = null
    private var listValue = ArrayList<Song>()
    private var pos = 1
    private var network: Network? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        listener()
    }

    private fun listener() {
        txtCoin!!.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    PurchaseInAppActivity::class.java
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        val coin =newInstance()?.preference?.getValueCoin()
        txtCoin!!.text = String.format(resources.getString(R.string.value_coin), coin)
    }

    private fun initView() {
        listData = findViewById(R.id.listData)
        frameLayout = findViewById(R.id.frameLayout)
        txtCoin = findViewById(R.id.txtCoin)
        audioAdapter = AudioAdapter(this)
        audioAdapter?.setOnItemSelect(this)
        audio
    }

    private val audio: Unit
        private get() {
            network = Network()
            network!!.retrofit().create(Api::class.java).bxhZing().enqueue(object : Callback<Music> {
                override fun onResponse(call: Call<Music>, response: Response<Music>) {
                    listValue = response.body()!!.data!!.song
                    val layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                    listData!!.setHasFixedSize(true)
                    listData!!.layoutManager = layoutManager
                    listData!!.adapter = audioAdapter
                    audioAdapter!!.setData(listValue)
                }

                override fun onFailure(call: Call<Music>, t: Throwable) {}
            })

        }

    private fun openPlayController(songDto: Song) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_bottom,
            R.anim.slide_out_bottom,
            R.anim.slide_in_top,
            R.anim.slide_out_top
        )
        transaction.addToBackStack(null)
        transaction.replace(R.id.frameLayout, AudioFragment.newInstance(listValue, pos)).commit()
    }

    override fun onItemSelect(pos: Int) {
        if (newInstance()?.preference?.getValueCoin() == 0) {
            startActivity(Intent(this@MainActivity, PurchaseInAppActivity::class.java))
        } else {
            val songDto = listValue[pos]
            this.pos = pos
            songDto.play = true
            openPlayController(songDto)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val coin = newInstance()!!.preference!!.getValueCoin()
        txtCoin!!.text = String.format(resources.getString(R.string.value_coin), coin)
    }

    companion object {
        const val KEY_DATA = "KEY_DATA"
        const val KEY_POS = "KEY_POS"
        const val EVENT_NEXT = "EVENT_NEXT"
        const val EVENT_PREVIOUS = "EVENT_PREVIOUS"
        const val EVENT_PLAY_PAUSE = "EVENT_PLAY_PAUSE"
    }
}