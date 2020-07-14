package com.aniketsingh.foodhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.adapter.CartRecyclerAdapter
import com.aniketsingh.foodhub.adapter.FoodMenuRecyclerAdapter
import com.aniketsingh.foodhub.database.OrderEntity
import com.aniketsingh.foodhub.database.RestaurantDatabase
import com.aniketsingh.foodhub.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class CartActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var toolbar: Toolbar
    lateinit var btnOrder: Button
    var dbOrderList = listOf<OrderEntity>()

    var totalPrice = 0
    lateinit var sharedPreferences : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        recyclerView = findViewById(R.id.cart_recyclerview)
        progressLayout = findViewById(R.id.progress_layout_cart)
        progressBar = findViewById(R.id.progress_bar_cart)
        btnOrder = findViewById(R.id.btn_order)
        toolbar = findViewById(R.id.cart_toolbar_food)
        layoutManager = LinearLayoutManager(this)
        progressLayout.visibility = View.VISIBLE

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", "1")

        setUpToolbar("My Cart")
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        dbOrderList = RetrieveOrder(this).execute().get()

        for (i in dbOrderList.indices){
            totalPrice += dbOrderList[i].price.toInt()
        }

        btnOrder.text = "Place Order (Total: Rs. $totalPrice)"

        progressLayout.visibility = View.GONE
        recyclerAdapter = CartRecyclerAdapter(this, dbOrderList)

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = recyclerAdapter

        btnOrder.setOnClickListener {
            sendOrder(userId, dbOrderList, totalPrice)
        }

    }

    fun sendOrder(userId: String?, dbOrderList: List<OrderEntity>, totalPrice: Int){

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        val jsonParams = JSONObject()
        val jsonArray = JSONArray()

        for (i in dbOrderList.indices){
            jsonArray.put(i, JSONObject().put("food_item_id", dbOrderList[i].id))
        }

        jsonParams.put("user_id", userId)
        jsonParams.put("restaurant_id", intent.getStringExtra("resId"))
        jsonParams.put("total_cost", totalPrice.toString())
        jsonParams.put("food", jsonArray)

        if(ConnectionManager().checkConnectivity(this)){
            val jsonRequest = object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                try {

                    val resData = it.getJSONObject("data")
                    val success = resData.getBoolean("success")
                    if(success){

                        val intent = Intent(this, OrderPlacedActivity::class.java)
                        startActivity(intent)

                    }else{
                        Toast.makeText(this, "Order Placing Failed!!!", Toast.LENGTH_SHORT).show()
                    }

                }catch (e: Exception){
                    Toast.makeText(this, "Some unexpected error occurred", Toast.LENGTH_SHORT).show()
                }

            }, Response.ErrorListener {
                Toast.makeText(this, "Volley Error $it", Toast.LENGTH_SHORT).show()
            }){

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-Type"] = "application/json"
                    headers["token"] = "271706b81cebcc"
                    return headers
                }

            }

            queue.add(jsonRequest)

        }else{
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings"){text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Cancel"){text, listener ->
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

    class RetrieveOrder(val context: Context) : AsyncTask<Void, Void, List<OrderEntity>>(){
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()

            return db.orderDao().getAllOrder()
        }
    }

    class DeleteOrder(val context: Context) : AsyncTask<Void, Void, Boolean>(){
        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
            db.orderDao().deleteAllOrder()
            return true
        }
    }
}