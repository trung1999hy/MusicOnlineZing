package com.haodv.musiceat.utils

import android.content.Context
import android.content.SharedPreferences

class Preference(context: Context) {
    private val BEARER_HEADER = "Bearer "
    private val sharedPreferences: SharedPreferences
    private val PREFS_ACCOUNT = "PREFS_ACCOUNT"
    private val KEY_TYPE_ONE = "KEY_TYPE_ONE"
    private val KEY_TOTAL_COIN = "KEY_TOTAL_COIN" // coin
    private val KEY_FIRST_INSTALL = "KEY_FIRST_INSTALL" // coin
    private val INT_ZERO = 0 // coin
    private val Key_Next_Random = "Key_Next_Random"

    init {
        sharedPreferences = context.getSharedPreferences(PREFS_ACCOUNT, Context.MODE_PRIVATE)
    }

    fun setValueTypeOne(value: String?) {
        sharedPreferences.edit().putString(KEY_TYPE_ONE, value).apply()
    }

    //    public int getPremium() {
    //        return sharedPreferences.getInt(KEY_PREMIUM, Constants.RESULT);
    //    }
    //
    //    public void setVip(String value) {
    //        sharedPreferences.edit().putString(KEY_VIP, value).apply();
    //    }
    //
    //    public String getVip() {
    //        return sharedPreferences.getString(KEY_VIP, Constants.STRING_DEFAULT);
    //    }
    //
    var firstInstall: Boolean
        get() = sharedPreferences.getBoolean(KEY_FIRST_INSTALL, false)
        set(value) {
            sharedPreferences.edit().putBoolean(KEY_FIRST_INSTALL, value).apply()
        }

    fun setValueCoin(value: Int) {
        sharedPreferences.edit().putInt(KEY_TOTAL_COIN, value).apply()
    }

    fun getValueCoin(): Int {
        return sharedPreferences.getInt(KEY_TOTAL_COIN, INT_ZERO)
    }

    fun setNextRandom(value: Boolean) {
        sharedPreferences.edit().putBoolean(Key_Next_Random, value).apply()
    }

    fun getNextRandom(): Boolean = sharedPreferences.getBoolean(Key_Next_Random, false)

    companion object {
        var instance: Preference? = null
        fun buildInstance(context: Context): Preference? {
            if (instance == null) {
                instance = Preference(context)
            }
            return instance
        }
    }
}