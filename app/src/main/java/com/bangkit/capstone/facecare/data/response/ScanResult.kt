package com.bangkit.capstone.facecare.data.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScanResult(
    val imageUrl: String,
    val skinCondition: String,
    val treatmentTips: String,
    val dateTime: String
):Parcelable{
    constructor() : this("", "", "", "")
}