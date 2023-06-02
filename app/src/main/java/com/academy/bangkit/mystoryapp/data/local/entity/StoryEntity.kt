package com.academy.bangkit.mystoryapp.data.local.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "story")
@Parcelize
class StoryEntity(
    @PrimaryKey
    @field:ColumnInfo("id")
    val id: String,

    @field:ColumnInfo("photo_url")
    val photoUrl: String? = null,

    @field:ColumnInfo("name")
    val name: String? = null,

    @field:ColumnInfo("description")
    val description: String? = null,

    @field:ColumnInfo("created_at")
    val createdAt: String? = null,

    @field:ColumnInfo("lon")
    val lon: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
) : Parcelable