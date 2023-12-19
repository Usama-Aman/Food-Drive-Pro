package com.elementary.thefooddrivepro.chat

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.elementary.thefooddrivepro.R
import kotlinx.android.synthetic.main.activity_chat_gallery.*

class ChatGallery : AppCompatActivity(), View.OnClickListener {

    var imageUrl = ""

    private lateinit var toolbarMenu: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_chat_gallery)
        initViews()
        getData()
        setData()
        setClickListeners()
    }

    private fun initViews() {
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }

        toolbarMenu = findViewById(R.id.toolbarMenu)
        toolbarMenu.setOnClickListener { finish() }
    }

    private fun getData() {
        imageUrl = intent.getStringExtra("imageUrl")!!
    }

    private fun setData() {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_place_holder)
            .error(R.drawable.ic_place_holder)
            .into(ivFullImage)
    }

    private fun setClickListeners() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toolbarMenu -> {
                onBackPressed()
            }
        }
    }
}