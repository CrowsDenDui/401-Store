package com.examples.a401store.Model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class orderDetail(): Parcelable {
    var userUid: String? = null
    var userName: String? = null
    var foodName: MutableList<String>? = null
//    var foodImg: MutableList<String>? = null
    var foodPrice: MutableList<String>? = null
    var foodQuantity: MutableList<Int>? = null
    var address: String? = null
    var totalPrice: String? = null
    var phoneNumber: String? = null
    var orderAccepted: Boolean = false
    var paymentReceived: Boolean = false
    var itemPushKey: String? = null
    var currentTime: Long = 0

    constructor(parcel: Parcel) : this() {
        userUid = parcel.readString()
        userName = parcel.readString()
        address = parcel.readString()
        totalPrice = parcel.readString()
        phoneNumber = parcel.readString()
        orderAccepted = parcel.readByte() != 0.toByte()
        paymentReceived = parcel.readByte() != 0.toByte()
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    constructor(
        userId: String,
        name: String,
        foodItemName: ArrayList<String>,
        foodItemPrice: ArrayList<String>,
        foodItemQuantity: ArrayList<Int>,
        totalAmount: String,
        address: String,
        phone: String,
        time: Long,
        itemPushKey: String?,
        b: Boolean,
        b1: Boolean
    ) : this(){
        this.userUid = userId
        this.userName = name
        this.foodName = foodItemName
        this.foodPrice = foodItemPrice
        this.foodQuantity = foodItemQuantity
        this.totalPrice = totalAmount
        this.address = address
        this.phoneNumber = phone
        this.currentTime = time
        this.itemPushKey = itemPushKey
        this.orderAccepted = orderAccepted
        this.paymentReceived = paymentReceived
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userUid)
        parcel.writeString(userName)
        parcel.writeString(address)
        parcel.writeString(totalPrice)
        parcel.writeString(phoneNumber)
        parcel.writeByte(if (orderAccepted) 1 else 0)
        parcel.writeByte(if (paymentReceived) 1 else 0)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<orderDetail> {
        override fun createFromParcel(parcel: Parcel): orderDetail {
            return orderDetail(parcel)
        }

        override fun newArray(size: Int): Array<orderDetail?> {
            return arrayOfNulls(size)
        }
    }
}