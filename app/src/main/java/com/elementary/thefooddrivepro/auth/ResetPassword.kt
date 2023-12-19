package com.elementary.thefooddrivepro.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.network.ResponseCallBack
import com.elementary.thefooddrivepro.network.RetrofitClient
import com.elementary.thefooddrivepro.utils.AppUtils
import com.elementary.thefooddrivepro.utils.BaseActivity
import com.elementary.thefooddrivepro.utils.Loader
import kotlinx.android.synthetic.main.activity_reset_password.*
import org.json.JSONObject

class ResetPassword : BaseActivity(), ResponseCallBack {

    private lateinit var btnNext: TextView

    private var isPassword: Boolean = false
    private var isRetypePassword: Boolean = false

    private var userEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        setStatusBarColor(this)

        initViews()

    }

    private fun initViews() {
        userEmail = intent.getStringExtra("email")!!
        if (userEmail == null || userEmail == "")
            finish()

        btnNext = findViewById(R.id.tvFPNext)
        btnNext.setOnClickListener {
            validate()

            if (isPassword && isRetypePassword) {
                checkForSamePasswords()
            }
        }

    }

    private fun checkForSamePasswords() {
        if (etNewPassword.text.toString() != etRetypePassword.text.toString()) {

            etNewPassword.setBackgroundResource(R.drawable.error_text_field_drawable)
            etNewPasswordError.visibility = View.VISIBLE
            etRetypePassword.setBackgroundResource(R.drawable.error_text_field_drawable)
            etRetypePasswordError.visibility = View.VISIBLE
            etNewPasswordError.text = resources.getString(R.string.resetErrorPasswordMatch)
            etRetypePasswordError.text = resources.getString(R.string.resetErrorPasswordMatch)
            return
        } else {
            etNewPassword.setBackgroundResource(R.drawable.white_text_field_drawable)
            etNewPasswordError.visibility = View.INVISIBLE
            etRetypePassword.setBackgroundResource(R.drawable.white_text_field_drawable)
            etRetypePasswordError.visibility = View.INVISIBLE

            callResetPassword()
        }


    }

    private fun validate() {
        isPassword = if (!etNewPassword.text.toString().isBlank()) {
            etNewPassword.setBackgroundResource(R.drawable.white_text_field_drawable)
            etNewPasswordError.text = resources.getString(R.string.resetErrorPasswordRequired)
            etNewPasswordError.visibility = View.INVISIBLE
            true
        } else {
            etNewPassword.setBackgroundResource(R.drawable.error_text_field_drawable)
            etNewPasswordError.visibility = View.VISIBLE
            false
        }

        isRetypePassword = if (!etRetypePassword.text.toString().isBlank()) {
            etRetypePassword.setBackgroundResource(R.drawable.white_text_field_drawable)
            etNewPasswordError.text =
                resources.getString(R.string.resetErrorConfirmPasswordRequired)
            etRetypePasswordError.visibility = View.INVISIBLE
            true
        } else {
            etRetypePassword.setBackgroundResource(R.drawable.error_text_field_drawable)
            etRetypePasswordError.visibility = View.VISIBLE
            false
        }
    }

    private fun callResetPassword() {
        Loader.showLoader(this)
        val call = RetrofitClient.getClientNoToken()
            .resetPassword(
                userEmail,
                etNewPassword.text.toString(),
                etRetypePassword.text.toString()
            )
        RetrofitClient.apiCall(call, this, "ResetPassword", this)
    }

    override fun onSuccess(jsonObject: JSONObject, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(this, jsonObject.getString("message"), true)

        Handler().postDelayed({
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }, 800)

    }

    override fun onError(message: String, tag: String) {
        Loader.hideLoader()
        AppUtils.showToast(this, message, false)
    }

}