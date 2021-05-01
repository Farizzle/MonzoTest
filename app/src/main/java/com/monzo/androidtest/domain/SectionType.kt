package com.monzo.androidtest.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SectionType(val id: String, val type: String) : Parcelable