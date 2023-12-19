package com.elementary.thefooddrivepro.utils

import android.app.Application
import android.content.Context
import com.elementary.thefooddrivepro.auth.models.LoginData
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.google.GoogleEmojiProvider

class AppController : Application() {

    override fun onCreate() {
        super.onCreate()
        mInstance = this
        application = this
        appContext = applicationContext

        EmojiManager.install(GoogleEmojiProvider())

    }


    companion object {

        @get:Synchronized
        var mInstance: AppController? = null
        var appContext: Context? = null
        var application: Application? = null
        var userInfo: LoginData? = null
        var isAppRunning: Boolean = false
    }


}