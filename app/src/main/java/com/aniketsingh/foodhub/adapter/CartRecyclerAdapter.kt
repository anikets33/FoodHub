package com.aniketsingh.foodhub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.database.OrderEntity
import kotlin.properties.Delegates

class CartRecyclerAdapter(val context: Context, val itemList: List<OrderEntity>) : RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    var totalPrice = 0

    class CartViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val txtFoodName : TextView = view.findViewById(R.id.recyclerview_food_name_cart)
        val txtFoodPrice : TextView = view.findViewById(R.id.recyclerview_food_price_cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = itemList[position]

        val price = item.price

        totalPrice += price.toInt()

        holder.txtFoodName.text = item.name
        holder.txtFoodPrice.text = "Rs. $price"
    }

}