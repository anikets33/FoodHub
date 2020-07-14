package com.aniketsingh.foodhub.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.activity.CartActivity
import com.aniketsingh.foodhub.database.OrderEntity
import com.aniketsingh.foodhub.database.RestaurantDatabase
import com.aniketsingh.foodhub.model.FoodItem

class FoodMenuRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<FoodItem>,
    var btnCart: Button
) : RecyclerView.Adapter<FoodMenuRecyclerAdapter.FoodViewHolder>() {

    class FoodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFoodItem: TextView = view.findViewById(R.id.recyclerview_food_item)
        val txtPrice: TextView = view.findViewById(R.id.recyclerview_food_price)
        val btnAdd: Button = view.findViewById(R.id.recycler_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_foodmenu_single_row, parent, false)
        return FoodViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = itemList[position]

        val price = item.cost

        holder.txtFoodItem.text = item.name
        holder.txtPrice.text = "Rs. $price"

        val orderEntity = OrderEntity(
            item.id,
            item.name,
            item.cost,
            item.restaurantId
        )

        val check = DBAsyncTask(context, orderEntity, 1).execute().get()
        if (check) {
            makeRemoveButton(holder)
        } else {
            makeAddButon(holder)
        }

        holder.btnAdd.setOnClickListener {
            if (check) {

                val result = DBAsyncTask(context, orderEntity, 3).execute().get()
                if (result) {
                    makeAddButon(holder)
                    val orderList = CartActivity.RetrieveOrder(context).execute().get()
                    Log.d("orderList", orderList.size.toString())
                    if (orderList.isEmpty()) {
                        btnCart.visibility = View.GONE
                    }
                    Toast.makeText(context, "Food removed from Cart", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                }

            } else {

                val result = DBAsyncTask(context, orderEntity, 2).execute().get()
                if (result) {
                    makeRemoveButton(holder)
                    if (btnCart.visibility == View.GONE) {
                        btnCart.visibility = View.VISIBLE
                    }
                    Toast.makeText(context, "Food added to Cart", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                }

            }
        }

        btnCart.setOnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("resId", item.restaurantId)
            context.startActivity(intent)
        }

    }

    fun makeAddButon(holder: FoodViewHolder) {
        holder.btnAdd.text = "add"
        val colorValue = ContextCompat.getColor(context, R.color.colorExtras)
        holder.btnAdd.setBackgroundColor(colorValue)
        holder.btnAdd.setTextColor(ContextCompat.getColor(context, R.color.white))
    }

    fun makeRemoveButton(holder: FoodViewHolder) {
        holder.btnAdd.text = "remove"
        val colorValue = ContextCompat.getColor(context, R.color.colorRemove)
        holder.btnAdd.setBackgroundColor(colorValue)
        holder.btnAdd.setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    class DBAsyncTask(val context: Context, val orderEntity: OrderEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        mode 1 -> check db if restaurant is added to favorite or not
        mode 2 -> add restaurant to favorite
        mode 3 -> delete restaurant from favorite
        */

        val db: RestaurantDatabase =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {

                1 -> {
                    val order: OrderEntity? = db.orderDao().getFoodById(
                        orderEntity.id
                    )
                    db.close()
                    return order != null
                }

                2 -> {
                    db.orderDao().insertOrder(orderEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.orderDao().deleteOrder(orderEntity)
                    db.close()
                    return true
                }

            }
            return false
        }

    }

}