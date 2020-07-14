package com.aniketsingh.foodhub.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OrderDao {
    @Insert
    fun insertOrder(orderEntity: OrderEntity)

    @Delete
    fun deleteOrder(orderEntity: OrderEntity)

    @Query("DELETE FROM order_food")
    fun deleteAllOrder()

    @Query("SELECT * FROM order_food")
    fun getAllOrder(): List<OrderEntity>

    @Query("SELECT * FROM order_food WHERE id = :id")
    fun getFoodById(id: String): OrderEntity
}