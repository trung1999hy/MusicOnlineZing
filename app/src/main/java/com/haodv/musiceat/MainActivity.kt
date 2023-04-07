package com.haodv.musiceat

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
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
    fun setVisibility(visibility: Int){
        binding.view.visibility = visibility
    }

    fun requestPermission() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data =
                    Uri.parse(String.format("package:%s", applicationContext.packageName))
                startActivityForResult(intent, 2296)
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission.ACCESS_MEDIA_LOCATION),
                    2296
                )
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, 2296)
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(permission.ACCESS_MEDIA_LOCATION),
                    2296
                )
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(
                    permission.WRITE_EXTERNAL_STORAGE,
                    permission.READ_EXTERNAL_STORAGE
                ),
                2296
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2296) {
            if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return if (VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager() && ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission.ACCESS_MEDIA_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            val result = ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission.READ_EXTERNAL_STORAGE
            )
            val result1 = ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission.WRITE_EXTERNAL_STORAGE
            )
            result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED

        }
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
        if (!checkPermission()) {
            requestPermission()
        }
        frameLayout = findViewById(R.id.frameLayout)
        txtCoin = findViewById(R.id.txtCoin)
        binding.container.adapter = MainViewPager(supportFragmentManager)
        binding.tablayou.setupWithViewPager(binding.container)
        binding.tablayou.getTabAt(0)?.icon = getDrawable(R.drawable.img)
        binding.tablayou.getTabAt(1)?.icon = getDrawable(R.drawable.music)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0){
          setVisibility(View.VISIBLE)
            supportFragmentManager.popBackStack()
        }


        else {
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
    }
}