package com.elementary.thefooddrivepro.dashboard.models

import android.os.Parcel
import android.os.Parcelable

data class MoneyDonationModel(
    val `data`: MoneyDonation,
    val message: String,
    val status: Boolean
)

data class MoneyDonation(
    val current_page: Int,
    val `data`: List<MoneyDonationData>,
    val first_page_url: String,
    val from: Int,
    val last_page: Int,
    val last_page_url: String,
    val next_page_url: String,
    val path: String,
    val per_page: Int,
    val prev_page_url: Any,
    val to: Int,
    val total: Int
)

data class MoneyDonationData(
    val added_date_format: String,
    val address: String,
    val admin_notes: String,
    val amount: String,
    val bank_detail_info: String,
    val bank_id: Int,
    val bank_name: String,
    val donor: MoneyDonationDonor?,
    val donor_id: Int,
    val id: Int,
    val ios_money_donation_id: Int,
    val latitude: String,
    val longitude: String,
    val money_donation_type: MoneyDonationType?,
    val money_donation_type_id: Int,
    val notes: String,
    val picked_by_admin: MoneyReceivedByAdmin?,
    val picked_by_admin_id: Int,
    val picked_datetime: String,
    val picking_date: String,
    val picking_date_format: String,
    val picking_end_time: String,
    val picking_start_time: String,
    var status: String,
    val updated_date_format: String,
    var isLoadMore: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readParcelable(MoneyDonationDonor::class.java.classLoader),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(MoneyDonationType::class.java.classLoader),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readParcelable(MoneyReceivedByAdmin::class.java.classLoader),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(added_date_format)
        parcel.writeString(address)
        parcel.writeString(admin_notes)
        parcel.writeString(amount)
        parcel.writeString(bank_detail_info)
        parcel.writeInt(bank_id)
        parcel.writeString(bank_name)
        parcel.writeParcelable(donor, flags)
        parcel.writeInt(donor_id)
        parcel.writeInt(id)
        parcel.writeInt(ios_money_donation_id)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeParcelable(money_donation_type, flags)
        parcel.writeInt(money_donation_type_id)
        parcel.writeString(notes)
        parcel.writeParcelable(picked_by_admin, flags)
        parcel.writeInt(picked_by_admin_id)
        parcel.writeString(picked_datetime)
        parcel.writeString(picking_date)
        parcel.writeString(picking_date_format)
        parcel.writeString(picking_end_time)
        parcel.writeString(picking_start_time)
        parcel.writeString(status)
        parcel.writeString(updated_date_format)
        parcel.writeByte(if (isLoadMore) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MoneyDonationData> {
        override fun createFromParcel(parcel: Parcel): MoneyDonationData {
            return MoneyDonationData(parcel)
        }

        override fun newArray(size: Int): Array<MoneyDonationData?> {
            return arrayOfNulls(size)
        }
    }

}

data class MoneyDonationDonor(
    val about_me: Any,
    val added_date_format: String,
    val address: String,
    val app_account_type: String,
    val birth_date_format: String,
    val country_code: String,
    val date_of_birth: String,
    val donor_form_json: String,
    val email: String,
    val first_name: String,
    val fullImage: String,
    val full_name: String,
    val id: Int,
    val ios_donor_id: Int,
    val last_name: String,
    val latitude: String,
    val longitude: Any,
    val phone_number: String,
    val photo: String,
    val thumbImage: String,
    val updated_date_format: String,
    val user_type: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        TODO("about_me"),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        TODO("longitude"),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(added_date_format)
        parcel.writeString(address)
        parcel.writeString(app_account_type)
        parcel.writeString(birth_date_format)
        parcel.writeString(country_code)
        parcel.writeString(date_of_birth)
        parcel.writeString(donor_form_json)
        parcel.writeString(email)
        parcel.writeString(first_name)
        parcel.writeString(fullImage)
        parcel.writeString(full_name)
        parcel.writeInt(id)
        parcel.writeInt(ios_donor_id)
        parcel.writeString(last_name)
        parcel.writeString(latitude)
        parcel.writeString(phone_number)
        parcel.writeString(photo)
        parcel.writeString(thumbImage)
        parcel.writeString(updated_date_format)
        parcel.writeString(user_type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MoneyDonationDonor> {
        override fun createFromParcel(parcel: Parcel): MoneyDonationDonor {
            return MoneyDonationDonor(parcel)
        }

        override fun newArray(size: Int): Array<MoneyDonationDonor?> {
            return arrayOfNulls(size)
        }
    }
}

data class MoneyDonationType(
    val id: Int,
    val ios_money_donation_type_id: Int,
    val key: String,
    val name: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(ios_money_donation_type_id)
        parcel.writeString(key)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MoneyDonationType> {
        override fun createFromParcel(parcel: Parcel): MoneyDonationType {
            return MoneyDonationType(parcel)
        }

        override fun newArray(size: Int): Array<MoneyDonationType?> {
            return arrayOfNulls(size)
        }
    }
}

data class MoneyReceivedByAdmin(
    val admin_type: String,
    val email: String,
    val id: Int,
    val ios_admin_id: Int,
    val last_login_ip: Any,
    val name: String,
    val otp: String,
    val password: String,
    val photo: String,
    val remember_token: Any,
    val status: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        TODO("last_login_ip"),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        TODO("remember_token"),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(admin_type)
        parcel.writeString(email)
        parcel.writeInt(id)
        parcel.writeInt(ios_admin_id)
        parcel.writeString(name)
        parcel.writeString(otp)
        parcel.writeString(password)
        parcel.writeString(photo)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemPickedByAdmin> {
        override fun createFromParcel(parcel: Parcel): ItemPickedByAdmin {
            return ItemPickedByAdmin(parcel)
        }

        override fun newArray(size: Int): Array<ItemPickedByAdmin?> {
            return arrayOfNulls(size)
        }
    }
}