package com.monzo.androidtest.data.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.monzo.androidtest.data.domain.SectionType
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "section_table")
@Parcelize
data class DBSectionType(
        @PrimaryKey val id: String,
        val type: String
) : Parcelable

fun List<DBSectionType>.asDomainModel(): List<SectionType> {
    return map {
        SectionType(
                id = it.id,
                type = it.type
        )
    }
}