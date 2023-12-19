package com.elementary.thefooddrivepro.network

import org.json.JSONObject

interface ResponseCallBack {

    fun onSuccess(jsonObject: JSONObject, tag: String)
    fun onError(message: String, tag: String)

}