package com.elementary.thefooddrivepro.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.kaopiz.kprogresshud.KProgressHUD
import java.lang.Exception

class Loader {

    companion object {
        var mContext: Context? = null
        var progressKHUD: KProgressHUD? = null
        fun showLoader(context: Context) {
            try {

                if (progressKHUD != null && progressKHUD!!.isShowing) {
                    return
                }
                mContext = context
                AppUtils.touchScreenDisable(mContext!!)

                if ((context as AppCompatActivity).isFinishing)
                    return
                try {
                    if (!(context).isFinishing) {
                        progressKHUD = KProgressHUD.create(context)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setCancellable(false)
                            .setAnimationSpeed(2)
                            .setDimAmount(10f).show()
                    }
                } catch (e: Exception) {
                }
            } catch (e: Exception) {
            }

        }

        fun hideLoader() {
            try {
                AppUtils.touchScreenEnable(mContext!!)
                if (progressKHUD != null && progressKHUD!!.isShowing) {
                    progressKHUD!!.dismiss()
                }
            } catch (e: Exception) {
            }
        }
    }

}