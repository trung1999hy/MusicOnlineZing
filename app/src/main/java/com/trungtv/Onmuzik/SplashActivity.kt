package com.trungtv.Onmuzik

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.*

class SplashActivity : AppCompatActivity() {
    var billingClient: BillingClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkInAppPurchase()
        startMain()
    }

    private fun startMain() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, 3000)
    }

    fun checkInAppPurchase() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases()
            .setListener { billingResult: BillingResult?, list: List<Purchase?>? -> }
            .build()
        val finalBillingClient: BillingClient = billingClient as BillingClient
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {}
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    finalBillingClient.queryPurchasesAsync(
                        QueryPurchasesParams.newBuilder()
                            .setProductType(BillingClient.ProductType.INAPP).build()
                    ) { billingResult1: BillingResult, list: List<Purchase> ->
                        if (billingResult1.responseCode == BillingClient.BillingResponseCode.OK) {
                            for (purchase: Purchase in list) {
                                val consumeParams = ConsumeParams
                                    .newBuilder()
                                    .setPurchaseToken(purchase.purchaseToken)
                                    .build()
                                billingClient!!.consumeAsync(
                                    consumeParams,
                                    ConsumeResponseListener { billingResult, s ->
                                        Toast.makeText(
                                            this@SplashActivity,
                                            " Resume item ",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    })
                            }
                        }
                    }
                }
            }
        })
    }
}