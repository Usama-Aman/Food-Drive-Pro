package com.elementary.thefooddrivepro.dashboard

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.dashboard.adapter.ImageSliderAdapter
import com.elementary.thefooddrivepro.dashboard.models.ItemDonationData
import com.elementary.thefooddrivepro.utils.AppUtils
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView


class DonationDetailFragment : Fragment() {

    private lateinit var v: View
    private lateinit var toolbarTitle: TextView
    private lateinit var toolbarBtn: TextView
    private lateinit var toolbarLogo: ImageView
    private lateinit var placeHolder: ImageView
    private lateinit var phoneConstraint: ConstraintLayout
    private lateinit var locationConstraint: ConstraintLayout
    private lateinit var sliderView: SliderView
    private lateinit var tvName: TextView
    private lateinit var tvType: TextView
    private lateinit var tvPhoneNo: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvNotes: TextView
    private lateinit var tvPickUpTime: TextView


    private lateinit var itemDonationData: ItemDonationData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_donation_detail, container, false)

        initViews()

        return v
    }

    private fun initViews() {
        itemDonationData = arguments?.getParcelable("data")!!

        if (itemDonationData == null) {
            activity!!.supportFragmentManager.popBackStack()
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.error_something_went_wrong),
                false
            )
        }

        locationConstraint = v.findViewById(R.id.locationConstraint)
        phoneConstraint = v.findViewById(R.id.phoneConstraint)
        toolbarTitle = activity!!.findViewById(R.id.toolbarTitle)
        toolbarLogo = activity!!.findViewById(R.id.toolBarLogo)
        toolbarBtn = activity!!.findViewById(R.id.toolbarBtn)
        toolbarBtn.setOnClickListener { activity!!.supportFragmentManager.popBackStack() }
        sliderView = v.findViewById(R.id.imageSlider)
        placeHolder = v.findViewById(R.id.placeHolder)

        tvName = v.findViewById(R.id.donorName)
        tvType = v.findViewById(R.id.donationType)
        tvPhoneNo = v.findViewById(R.id.donorPhoneNo)
        tvAddress = v.findViewById(R.id.donationPickUpAddress)
        tvNotes = v.findViewById(R.id.donationNotes)
        tvPickUpTime = v.findViewById(R.id.donationPickUpTime)

        toolbarBtn.text = context!!.resources.getString(R.string.donationDetailBtnBack)
        toolbarTitle.visibility = View.GONE
        toolbarBtn.visibility = View.VISIBLE
        toolbarLogo.visibility = View.VISIBLE


        phoneConstraint.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + "${itemDonationData.donor?.country_code}${itemDonationData.donor?.phone_number}")
            )
            context!!.startActivity(intent)
        }

        locationConstraint.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=${itemDonationData.donor?.latitude},${itemDonationData.donor?.longitude}")
            )
            startActivity(intent)
        }

        if (itemDonationData.donation_files.isNullOrEmpty()) {
            sliderView.visibility = View.GONE
            placeHolder.visibility = View.VISIBLE
        } else {
            sliderView.visibility = View.VISIBLE
            placeHolder.visibility = View.GONE
            setUpImageSlider()
        }

        setUpData()
    }

    private fun setUpImageSlider() {
        val adapter = ImageSliderAdapter(itemDonationData.donation_files)
        sliderView.setSliderAdapter(adapter)
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM) //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        sliderView.indicatorSelectedColor = ContextCompat.getColor(context!!, R.color.appGreenColor)
        sliderView.indicatorUnselectedColor =
            ContextCompat.getColor(context!!, R.color.appGreyColor)
        sliderView.scrollTimeInSec = 3 //set scroll delay in seconds :

        sliderView.startAutoCycle()
    }

    private fun setUpData() {

        tvName.text = itemDonationData.donor_name
        tvPhoneNo.text =
            "${itemDonationData.donor?.country_code}${itemDonationData.donor?.phone_number}"
        tvAddress.text = itemDonationData.donor?.address
        tvType.text = itemDonationData.donation_type?.name
        tvPickUpTime.text =
            "${itemDonationData.picking_date_format} , ${itemDonationData.picking_start_time_format} - ${itemDonationData.picking_end_time_format}"

        tvNotes.text = itemDonationData.note


        locationConstraint.isEnabled = !itemDonationData.donor?.address.isNullOrEmpty()
        phoneConstraint.isEnabled = !itemDonationData.donor?.phone_number.isNullOrEmpty()

    }

}