package com.aniketsingh.foodhub.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.activity.FoodMenuActivity
import com.aniketsingh.foodhub.database.RestaurantDatabase
import com.aniketsingh.foodhub.database.RestaurantEntity
import com.aniketsingh.foodhub.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(val context: Context, val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgFoodIcon: ImageView = view.findViewById(R.id.recycler_img_food_icon)
        val txtRestaurant: TextView = view.findViewById(R.id.recyclerview_restaurant)
        val txtPrice: TextView = view.findViewById(R.id.recyclerview_price)
        val txtRating: TextView = view.findViewById(R.id.recyclerview_rating)
        val llcontent: LinearLayout = view.findViewById(R.id.recyclerview_llcontent)
        val imgFavIcon: ImageView = view.findViewById(R.id.recyclerview_img_favorites)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = itemList[position]

        val price = item.resPrice

        holder.txtRestaurant.text = item.resName
        holder.txtPrice.text = "Rs. $price/person"
        Picasso.get().load(item.resImage).error(R.drawable.food_app_icon).into(holder.imgFoodIcon)
        holder.txtRating.text = item.resRating

        holder.llcontent.setOnClickListener {
            val intent = Intent(context, FoodMenuActivity::class.java)
            intent.putExtra("id", item.resId)
            intent.putExtra("name", item.resName)
            context.startActivity(intent)
        }

        val restaurantEntity = RestaurantEntity(
            item.resId,
            item.resName,
            item.resRating,
            item.resPrice,
            item.resImage
        )

        val checkDatabase = DBAsyncTask(context, restaurantEntity, 1).execute().get()
        if (checkDatabase) {
            holder.imgFavIcon.setImageResource(R.drawable.ic_fav_solid)
        } else {
            holder.imgFavIcon.setImageResource(R.drawable.ic_fav_border)
        }

        holder.imgFavIcon.setOnClickListener {

            if (!checkDatabase) {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(context, "Restaurant added to favorites", Toast.LENGTH_SHORT)
                        .show()
                    holder.imgFavIcon.setImageResource(R.drawable.ic_fav_solid)
                } else {
                    Toast.makeText(context, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()

                if (result) {
                    Toast.makeText(context, "Restaurant removed from favorites", Toast.LENGTH_SHORT)
                        .show()
                    holder.imgFavIcon.setImageResource(R.drawable.ic_fav_border)
                } else {
                    Toast.makeText(context, "Some error occurred!!!", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
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
                    val restaurant: RestaurantEntity? = db.restaurantDao().getRestaurantById(
                        restaurantEntity.id
                    )
                    db.close()
                    return restaurant != null
                }

                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }

            }
            return false
        }

    }

}