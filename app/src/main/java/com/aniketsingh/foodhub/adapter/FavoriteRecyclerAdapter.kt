package com.aniketsingh.foodhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.activity.FoodMenuActivity
import com.aniketsingh.foodhub.database.RestaurantEntity
import com.squareup.picasso.Picasso

class FavoriteRecyclerAdapter(val context: Context, val itemList: List<RestaurantEntity>)
    : RecyclerView.Adapter<FavoriteRecyclerAdapter.FavoriteViewHolder>() {

    class FavoriteViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val imgFoodIcon : ImageView = view.findViewById(R.id.recycler_img_food_icon_fav)
        val txtRestaurant : TextView = view.findViewById(R.id.recyclerview_restaurant_fav)
        val txtPrice : TextView = view.findViewById(R.id.recyclerview_price_fav)
        val txtRating : TextView = view.findViewById(R.id.recyclerview_rating_fav)
        val llcontent : LinearLayout = view.findViewById(R.id.recyclerview_llcontent_fav)
        val imgFavIcon : ImageView = view.findViewById(R.id.recyclerview_img_favorites_fav)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_home_single_row, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val item = itemList[position]

        val price = item.costForOne

        holder.txtRestaurant.text = item.name
        holder.txtPrice.text = "Rs. $price/person"
        Picasso.get().load(item.imageUrl).error(R.drawable.food_app_icon).into(holder.imgFoodIcon)
        holder.txtRating.text = item.rating

        holder.llcontent.setOnClickListener {
            val intent = Intent(context, FoodMenuActivity::class.java)
            intent.putExtra("id", item.id)
            intent.putExtra("name", item.name)
            context.startActivity(intent)
        }

        val restaurantEntity = RestaurantEntity(
            item.id,
            item.name,
            item.rating,
            item.costForOne,
            item.imageUrl
        )

        holder.imgFavIcon.setOnClickListener {

            if (!HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 1).execute().get()){
                val async = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()

                if (result){
                    Toast.makeText(context, "Restaurant added to favorites", Toast.LENGTH_SHORT).show()
                    holder.imgFavIcon.setImageResource(R.drawable.ic_fav_solid)
                }else{
                    Toast.makeText(context, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                }
            }else{
                val async = HomeRecyclerAdapter.DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result){
                    Toast.makeText(context, "Restaurant removed from favorites", Toast.LENGTH_SHORT).show()
                    holder.imgFavIcon.setImageResource(R.drawable.ic_fav_border)
                }else{
                    Toast.makeText(context, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

}