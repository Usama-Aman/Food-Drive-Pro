package com.elementary.thefooddrivepro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.elementary.thefooddrivepro.auth.Login
import com.elementary.thefooddrivepro.utils.BaseActivity
import com.elementary.thefooddrivepro.utils.Constants
import com.elementary.thefooddrivepro.utils.SharedPreference

class Splash : BaseActivity() {

    private var isComingFromNotification = false
    private var messageData: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setStatusBarColor(this)

        Handler().postDelayed({

            if (SharedPreference.getBoolean(this, Constants.IsLoggedIn)) {
                val intent = Intent(this, MainActivity()::class.java)

                if (getIntent().hasExtra("isComingFromNotification")) {
                    isComingFromNotification =
                        getIntent().getBooleanExtra("isComingFromNotification", false)
                    messageData = getIntent().getStringExtra("messageData")
                }

                if (isComingFromNotification) {
                    intent.putExtra("isComingFromNotification", isComingFromNotification)
                    intent.putExtra("messageData", messageData)
                } else {
                    intent.putExtra("isComingFromNotification", false)
                }

                startActivity(intent)
            } else
                startActivity(Intent(this, Login()::class.java))
            finish()
        }, 1000)

    }


}