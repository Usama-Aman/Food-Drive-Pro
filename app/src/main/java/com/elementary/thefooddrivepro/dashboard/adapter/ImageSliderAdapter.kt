package com.elementary.thefooddrivepro.dashboard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.dashboard.models.ItemDonationFile
import com.smarteist.autoimageslider.SliderViewAdapter


class ImageSliderAdapter(_donationFiles: List<ItemDonationFile>?) :
    SliderViewAdapter<SliderViewAdapter.ViewHolder>() {

    private lateinit var context: Context
    private var donationFiles = _donationFiles

    override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        context = parent?.context!!
        val inflate: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image_view_slider, null)
        return Item(inflate)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder?, position: Int) {
        if (viewHolder is Item)
            viewHolder.bind(position)
    }

    override fun getCount(): Int = donationFiles?.size!!

    inner class Item(itemView: View) : ViewHolder(itemView) {

        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(position: Int) {
            Glide.with(context).load(donationFiles?.get(position)?.fullImage).into(imageView)
        }

    }

}