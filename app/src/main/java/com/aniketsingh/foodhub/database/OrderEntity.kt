package com.aniketsingh.foodhub.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_food")
data class OrderEntity(
    @PrimaryKey val id : String,
    val name : String,
    val price : String,
    @ColumnInfo(name = "res_id") val resId : String
)