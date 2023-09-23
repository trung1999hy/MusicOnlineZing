package com.trungtv.Onmuzik.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.trungtv.Onmuzik.ui.MainApp.Companion.newInstance
import com.trungtv.Onmuzik.R
import com.trungtv.Onmuzik.databinding.ActivityMainBinding
import com.trungtv.Onmuzik.model.User

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
        binding.tablayou.getTabAt(0)?.icon = getDrawable(R.drawable.trophy)
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
        getData()
    }
    private fun setDataBaseGold(){
        val dataController = DataController(MainApp.newInstance()?.deviceId?:"")
        dataController.writeNewUser(MainApp.newInstance()?.deviceId?:"",  100)
    }

    fun getData(){
        val dataController = DataController(MainApp.newInstance()?.deviceId?:"")
        dataController.setOnListenerFirebase(object : DataController.OnListenerFirebase {
            override fun onCompleteGetUser(user: User?) {
                user?.let {
                    MainApp.newInstance()?.preference?.setValueCoin(user.coin)
                } ?: kotlin.run {
                    setDataBaseGold()
                }
                binding.txtCoin.text = String.format(resources.getString(R.string.amount_gold), MainApp.newInstance()?.preference?.getValueCoin())
            }

            override fun onSuccess() {

            }

            override fun onFailure() {
                Toast.makeText(this@MainActivity, "Có lỗi kết nối đến server!", Toast.LENGTH_LONG).show()
            }
        })
        dataController.user
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