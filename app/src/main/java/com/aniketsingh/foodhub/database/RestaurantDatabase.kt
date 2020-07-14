package com.aniketsingh.foodhub.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [RestaurantEntity::class, OrderEntity::class], version = 1)
abstract class RestaurantDatabase : RoomDatabase() {

    abstract fun restaurantDao() : RestaurantDao
    abstract fun orderDao() : OrderDao

}