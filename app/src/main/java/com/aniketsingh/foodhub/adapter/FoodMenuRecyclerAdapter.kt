package com.aniketsingh.foodhub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.model.FoodItem

class FoodMenuRecyclerAdapter(val context: Context, val itemList : ArrayList<FoodItem>, val btn : Button) : RecyclerView.Adapter<FoodMenuRecyclerAdapter.FoodViewHolder>() {

    var int = 1

    class FoodViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val txtSerial : TextView = view.findViewById(R.id.recycler_serial)
        val txtFoodItem : TextView = view.findViewById(R.id.recyclerview_food_item)
        val txtPrice : TextView = view.findViewById(R.id.recyclerview_food_price)
        val btnAdd : Button = view.findViewById(R.id.recycler_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_foodmenu_single_row, parent, false)
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
        holder.txtSerial.text = int.toString()
        int++

        holder.btnAdd.setOnClickListener {
            if(holder.btnAdd.text == "remove"){
                holder.btnAdd.text = "add"
                if (btn.visibility == View.VISIBLE){
                    btn.visibility = View.GONE
                }
            }else{
                holder.btnAdd.text = "remove"
                if (btn.visibility == View.GONE){
                    btn.visibility = View.VISIBLE
                }
            }
        }


    }

}