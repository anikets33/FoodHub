package com.aniketsingh.foodhub.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity (
    @PrimaryKey val id : String,
    val name : String,
    val rating : String,
    @ColumnInfo(name = "cost_for_one") val costForOne : String,
    @ColumnInfo(name = "image_url") val imageUrl : String
)