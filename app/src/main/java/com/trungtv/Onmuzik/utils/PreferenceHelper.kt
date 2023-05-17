package com.trungtv.Onmuzik.utils

import java.util.*

interface PreferenceHelper {
    var isLoggedIn: Boolean
    var isKeepLogged: Boolean
    var isReloadHome: Boolean
    fun setAccessToken(s: String?)
    fun generateAccessToken(): String?
    var password: String?
    var isLogginSocial: Boolean
    fun setTimeResend(timeResend: Calendar?)
    fun getTimeResend(): Long
    var social: HashMap<String?, Any?>?
    fun loggout()
}