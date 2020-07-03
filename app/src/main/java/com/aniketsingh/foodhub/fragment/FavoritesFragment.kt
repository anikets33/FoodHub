package com.aniketsingh.foodhub.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.adapter.FavoriteRecyclerAdapter
import com.aniketsingh.foodhub.adapter.HomeRecyclerAdapter
import com.aniketsingh.foodhub.database.RestaurantDatabase
import com.aniketsingh.foodhub.database.RestaurantEntity


class FavoritesFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FavoriteRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var dbRestaurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)

        recyclerView = view.findViewById(R.id.fav_recyclerview)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progress_layout_fav)
        progressBar = view.findViewById(R.id.progress_bar_fav)
        progressLayout.visibility = View.VISIBLE

        dbRestaurantList = RetrieveFavorites(activity as Context).execute().get()

        if (activity != null){
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavoriteRecyclerAdapter(activity as Context, dbRestaurantList)
        }

        return view
    }

    class RetrieveFavorites(val context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>(){
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

            return db.restaurantDao().getAllRestaurants()
        }

    }

}
