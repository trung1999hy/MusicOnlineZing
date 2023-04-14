package com.haodv.musiceat

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.haodv.musiceat.MainApp.Companion.newInstance
import com.haodv.musiceat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var frameLayout: FrameLayout? = null
    var txtCoin: TextView? = null

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater, null, false)
        setContentView(binding.root)
        initView()
        listener()
    }
    fun setVisibility(visibility: Int) {
        binding.view.visibility = visibility
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


    private fun initView() {
        frameLayout = findViewById(R.id.frameLayout)
        txtCoin = findViewById(R.id.txtCoin)
        binding.container.adapter = MainViewPager(supportFragmentManager)
        binding.tablayou.setupWithViewPager(binding.container)
        binding.tablayou.getTabAt(0)?.icon = getDrawable(R.drawable.img)
        binding.tablayou.getTabAt(1)?.icon = getDrawable(R.drawable.music)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            setVisibility(View.VISIBLE)
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        val coin = newInstance()?.preference?.getValueCoin()
        txtCoin?.text = String.format(resources.getString(R.string.value_coin), coin)
    }

    companion object {
        const val KEY_DATA = "KEY_DATA"
        const val KEY_POS = "KEY_POS"
        const val EVENT_NEXT = "EVENT_NEXT"
        const val EVENT_PREVIOUS = "EVENT_PREVIOUS"
        const val EVENT_PLAY_PAUSE = "EVENT_PLAY_PAUSE"
        const val EVENT_CLOSE = "EVENT_COLES"
    }
}