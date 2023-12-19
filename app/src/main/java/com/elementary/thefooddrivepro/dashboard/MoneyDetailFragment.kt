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
import androidx.fragment.app.Fragment
import com.elementary.thefooddrivepro.R
import com.elementary.thefooddrivepro.dashboard.models.MoneyDonationData
import com.elementary.thefooddrivepro.utils.AppUtils


class MoneyDetailFragment : Fragment() {

    private lateinit var v: View
    private lateinit var toolbarTitle: TextView
    private lateinit var toolbarBtn: TextView
    private lateinit var toolbarLogo: ImageView
    private lateinit var phoneConstraint: ConstraintLayout
    private lateinit var locationConstraint: ConstraintLayout

    private lateinit var tvMoney: TextView
    private lateinit var tvDonationType: TextView
    private lateinit var tvDonationPickUpTime: TextView
    private lateinit var tvDonorPhoneNo: TextView
    private lateinit var tvDonationPickUpAddress: TextView
    private lateinit var tvDonationNote: TextView

    private lateinit var moneyDonationData: MoneyDonationData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.fragment_money_detail, container, false)

        initViews()

        return v
    }

    private fun initViews() {
        moneyDonationData = arguments?.getParcelable("data")!!

        if (moneyDonationData == null) {
            activity!!.supportFragmentManager.popBackStack()
            AppUtils.showToast(
                activity,
                context!!.resources.getString(R.string.error_something_went_wrong),
                false
            )
        }

        locationConstraint = v.findViewById(R.id.locationConstraint)
        phoneConstraint = v.findViewById(R.id.phoneConstraint)

        tvMoney = v.findViewById(R.id.tvMoney)
        tvDonationType = v.findViewById(R.id.tvDonationType)
        tvDonationPickUpTime = v.findViewById(R.id.tvDonationPickUpTime)
        tvDonorPhoneNo = v.findViewById(R.id.tvDonorPhoneNo)
        tvDonationPickUpAddress = v.findViewById(R.id.tvDonationPickUpAddress)
        tvDonationNote = v.findViewById(R.id.tvDonationNote)

        toolbarTitle = activity!!.findViewById(R.id.toolbarTitle)
        toolbarLogo = activity!!.findViewById(R.id.toolBarLogo)
        toolbarBtn = activity!!.findViewById(R.id.toolbarBtn)
        toolbarBtn.setOnClickListener { activity!!.supportFragmentManager.popBackStack() }

        toolbarBtn.text = context!!.resources.getString(R.string.moneyDetailBtnBack)
        toolbarTitle.visibility = View.GONE
        toolbarBtn.visibility = View.VISIBLE
        toolbarLogo.visibility = View.VISIBLE

        phoneConstraint.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + "${moneyDonationData.donor?.country_code}${moneyDonationData.donor?.phone_number}")
            )
            context!!.startActivity(intent)
        }

        locationConstraint.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=${moneyDonationData.donor?.latitude},${moneyDonationData.donor?.longitude}")
            )
            startActivity(intent)
        }

        setUpData()

    }

    private fun setUpData() {

        tvMoney.text = moneyDonationData.amount
        tvDonorPhoneNo.text =
            "${moneyDonationData.donor?.country_code}${moneyDonationData.donor?.phone_number}"
        tvDonationPickUpAddress.text = moneyDonationData.donor?.address
        tvDonationType.text = moneyDonationData.money_donation_type?.name
        tvDonationPickUpTime.text =
            "${moneyDonationData.picking_date} , " +
                    "${moneyDonationData.picking_start_time.substringBeforeLast(
                        ":", moneyDonationData.picking_start_time
                    )} - ${moneyDonationData.picking_end_time.substringBeforeLast(
                        ":",
                        moneyDonationData.picking_end_time
                    )}"

        tvDonationNote.text = moneyDonationData.notes


        locationConstraint.isEnabled = !moneyDonationData.donor?.address.isNullOrEmpty()
        phoneConstraint.isEnabled = !moneyDonationData.donor?.phone_number.isNullOrEmpty()

    }


}