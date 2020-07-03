package com.aniketsingh.foodhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue

import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.adapter.HomeRecyclerAdapter
import com.aniketsingh.foodhub.model.Restaurant
import com.aniketsingh.foodhub.util.ConnectionManager
import org.json.JSONException
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter

    val resList = arrayListOf<Restaurant>()

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

//    var ratingComparator = Comparator<Restaurant>{res1, res2 ->
//        res1.resRating.compareTo(res2.resRating, true)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerView = view.findViewById(R.id.home_recyclerview)
        layoutManager = LinearLayoutManager(activity)

        progressLayout = view.findViewById(R.id.progress_layout)
        progressBar = view.findViewById(R.id.progress_bar)
        progressLayout.visibility = View.VISIBLE

        val queue = newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if(ConnectionManager().checkConnectivity(activity as Context)){

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
                Response.Listener {

                    try {

                        progressLayout.visibility = View.GONE

                        val resData = it.getJSONObject("data")
                        val success = resData.getBoolean("success")
                        if(success){

                            val data = resData.getJSONArray("data")
                            for(i in 0 until data.length()){

                                val resJsonObject = data.getJSONObject(i)
                                val resObject = Restaurant(
                                    resJsonObject.getString("id"),
                                    resJsonObject.getString("name"),
                                    resJsonObject.getString("rating"),
                                    resJsonObject.getString("cost_for_one"),
                                    resJsonObject.getString("image_url")
                                )
                                resList.add(resObject)

                                recyclerAdapter = HomeRecyclerAdapter(activity as Context, resList)

                                recyclerView.layoutManager = layoutManager
                                recyclerView.adapter = recyclerAdapter
                            }

                        }else{
                            Toast.makeText(activity, "Error occurred", Toast.LENGTH_SHORT).show()
                        }

                    }catch (e: JSONException){
                        Toast.makeText(activity, "Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()
                    }

                }
                , Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(activity, "Volley error occurred", Toast.LENGTH_SHORT).show()
                    }
                }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "271706b81cebcc"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)

        }else{

            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Cancel"){text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }

            dialog.create()
            dialog.show()

        }

        return view
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_drawer, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        if(id == R.id.action_sort) {
//            Collections.sort(resList, ratingComparator)
//            resList.reverse()
//        }
//
//        recyclerAdapter.notifyDataSetChanged()
//
//        return super.onOptionsItemSelected(item)
//    }

}
