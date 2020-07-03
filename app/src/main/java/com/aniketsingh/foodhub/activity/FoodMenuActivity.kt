package com.aniketsingh.foodhub.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.adapter.FoodMenuRecyclerAdapter
import com.aniketsingh.foodhub.fragment.HomeFragment
import com.aniketsingh.foodhub.model.FoodItem
import com.aniketsingh.foodhub.util.ConnectionManager
import org.json.JSONException

class FoodMenuActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FoodMenuRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar
    lateinit var btnCart: Button

    val foodList = arrayListOf<FoodItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_menu)

        recyclerView = findViewById(R.id.food_recyclerview)
        layoutManager = LinearLayoutManager(this)

        toolbar = findViewById(R.id.toolbar_food)

        val id = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        setUpToolbar(name)
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        btnCart = findViewById(R.id.btn_cart)
        progressLayout = findViewById(R.id.progress_layout_food)
        progressBar = findViewById(R.id.progress_bar_food)
        progressLayout.visibility = View.VISIBLE
        btnCart.visibility = View.GONE

        val queue = newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$id"

        if (ConnectionManager().checkConnectivity(this)) {

            val jsonObjectRequest = object : JsonObjectRequest(Method.GET, url, null,
                Response.Listener {

                    try {

                        progressLayout.visibility = View.GONE

                        val foodData = it.getJSONObject("data")
                        val success = foodData.getBoolean("success")
                        if (success) {

                            val data = foodData.getJSONArray("data")
                            for (i in 0 until data.length()) {

                                val foodJsonObject = data.getJSONObject(i)
                                val foodObject = FoodItem(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("cost_for_one"),
                                    foodJsonObject.getString("restaurant_id")
                                )
                                foodList.add(foodObject)

                                recyclerAdapter = FoodMenuRecyclerAdapter(this, foodList, btnCart)

                                recyclerView.layoutManager = layoutManager
                                recyclerView.adapter = recyclerAdapter
                            }

                        } else {
                            Toast.makeText(this, "Error occurred", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: JSONException) {
                        Toast.makeText(
                            this,
                            "Some unexpected error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                , Response.ErrorListener {
                    Toast.makeText(this, "Volley error occurred", Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "271706b81cebcc"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)

        } else {

            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this.finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }

            dialog.create()
            dialog.show()

        }

    }

    fun setUpToolbar(name: String){
        setSupportActionBar(toolbar)
        supportActionBar?.title = name
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
