package com.elementary.thefooddrivepro.dashboard.models

import android.os.Parcel
import android.os.Parcelable

data class ItemDonationModel(
    val `data`: ItemDonation,
    val message: String,
    val status: Boolean
)

data class ItemDonation(
    val current_page: Int,
    val `data`: List<ItemDonationData>,
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

data class ItemDonationData(
    val added_date_format: String,
    val address: String,
    val admin_id: Int,
    val admin_notes: Any,
    val confirmed_datetime: String,
    val country_code: String,
    val donation_files: List<ItemDonationFile>?,
    val donation_response_json: String,
    val donation_type: ItemDonationType?,
    val donation_type_customform: Int,
    val donation_type_id: Int,
    val donor: ItemDonationDonor?,
    val donor_id: Int,
    val donor_name: String,
    val id: Int,
    val ios_donation_id: Int,
    val latitude: String,
    val longitude: String,
    val note: String,
    val phone_number: String,
    val picked_by_admin: ItemPickedByAdmin?,
    val picked_by_admin_id: Int,
    val picked_datetime: String,
    val picking_date: String,
    val picking_date_format: String,
    val picking_end_time: String,
    val picking_end_time_format: String,
    val picking_start_time: String,
    val picking_start_time_format: String,
    val quantity: Int,
    val rating_add_datetime: Any,
    val rating_admin: Any,
    val rating_admin_id: Int,
    val rating_value: String,
    var status: String,
    val updated_date_format: String,
    var isLoadMore: Boolean = false

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        TODO("admin_notes"),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList(ItemDonationFile),
        parcel.readString()!!,
        parcel.readParcelable(ItemDonationType::class.java.classLoader),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readParcelable(ItemDonationDonor::class.java.classLoader),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readParcelable(ItemPickedByAdmin::class.java.classLoader),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        TODO("rating_add_datetime"),
        TODO("rating_admin"),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(added_date_format)
        parcel.writeString(address)
        parcel.writeInt(admin_id)
        parcel.writeString(confirmed_datetime)
        parcel.writeString(country_code)
        parcel.writeTypedList(donation_files)
        parcel.writeString(donation_response_json)
        parcel.writeInt(donation_type_customform)
        parcel.writeInt(donation_type_id)
        parcel.writeParcelable(donor, flags)
        parcel.writeInt(donor_id)
        parcel.writeString(donor_name)
        parcel.writeInt(id)
        parcel.writeInt(ios_donation_id)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(note)
        parcel.writeString(phone_number)
        parcel.writeInt(picked_by_admin_id)
        parcel.writeString(picking_date)
        parcel.writeString(picking_date_format)
        parcel.writeString(picking_end_time)
        parcel.writeString(picking_end_time_format)
        parcel.writeString(picking_start_time)
        parcel.writeString(picking_start_time_format)
        parcel.writeInt(quantity)
        parcel.writeInt(rating_admin_id)
        parcel.writeString(rating_value)
        parcel.writeString(status)
        parcel.writeString(updated_date_format)
        parcel.writeByte(if (isLoadMore) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemDonationData> {
        override fun createFromParcel(parcel: Parcel): ItemDonationData {
            return ItemDonationData(parcel)
        }

        override fun newArray(size: Int): Array<ItemDonationData?> {
            return arrayOfNulls(size)
        }
    }
}

data class ItemDonationFile(
    val donation_id: Int,
    val `file`: String,
    val file_type: String,
    val fullImage: String,
    val id: Int,
    val ios_donation_file_id: Int,
    val thumbImage: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(donation_id)
        parcel.writeString(file)
        parcel.writeString(fullImage)
        parcel.writeInt(id)
        parcel.writeInt(ios_donation_file_id)
        parcel.writeString(thumbImage)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemDonationFile> {
        override fun createFromParcel(parcel: Parcel): ItemDonationFile {
            return ItemDonationFile(parcel)
        }

        override fun newArray(size: Int): Array<ItemDonationFile?> {
            return arrayOfNulls(size)
        }
    }
}

data class ItemDonationDonor(
    val about_me: String,
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
    val longitude: String,
    val phone_number: String,
    val photo: String,
    val thumbImage: String,
    val updated_date_format: String,
    val user_type: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
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
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(about_me)
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
        parcel.writeString(longitude)
        parcel.writeString(phone_number)
        parcel.writeString(photo)
        parcel.writeString(thumbImage)
        parcel.writeString(updated_date_format)
        parcel.writeString(user_type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemDonationDonor> {
        override fun createFromParcel(parcel: Parcel): ItemDonationDonor {
            return ItemDonationDonor(parcel)
        }

        override fun newArray(size: Int): Array<ItemDonationDonor?> {
            return arrayOfNulls(size)
        }
    }
}

data class ItemDonationType(
    val id: Int,
    val ios_donation_type_id: Int,
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
        parcel.writeInt(ios_donation_type_id)
        parcel.writeString(key)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemDonationType> {
        override fun createFromParcel(parcel: Parcel): ItemDonationType {
            return ItemDonationType(parcel)
        }

        override fun newArray(size: Int): Array<ItemDonationType?> {
            return arrayOfNulls(size)
        }
    }
}

data class ItemPickedByAdmin(
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
