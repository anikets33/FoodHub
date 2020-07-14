package com.aniketsingh.foodhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.database.RestaurantDatabase
import com.aniketsingh.foodhub.database.RestaurantEntity
import com.aniketsingh.foodhub.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences
    lateinit var loginSharedPreferences: SharedPreferences

    lateinit var etMobileNumber : EditText
    lateinit var etPassword : EditText
    lateinit var btnLogin : Button
    lateinit var tvForgotPassword : TextView
    lateinit var tvRegister : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)

        loginSharedPreferences = getSharedPreferences(getString(R.string.preference_login), Context.MODE_PRIVATE)
        val isLoggedIn = loginSharedPreferences.getBoolean("isLoggedIn", false)

        setContentView(R.layout.activity_login)

        if (isLoggedIn){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        etMobileNumber = findViewById(R.id.food_login_username)
        etPassword = findViewById(R.id.food_login_password)
        btnLogin = findViewById(R.id.food_login_btn)
        tvForgotPassword = findViewById(R.id.food_login_forgot_password)
        tvRegister = findViewById(R.id.food_login_register)

        btnLogin.setOnClickListener {

            val mobileNumber = etMobileNumber.text.toString()
            val password = etPassword.text.toString()

            if(mobileNumber.length != 10 || password.length < 4){
                Toast.makeText(this, "Invalid Login Details", Toast.LENGTH_LONG).show()
            }else{
                savePreferences()
                DeleteFavorites(this).execute().get()
                sendCredentials(mobileNumber, password)
            }

        }

        tvRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

    }

    fun sendCredentials(mobile: String, password: String){

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/login/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobile)
        jsonParams.put("password", password)

        if(ConnectionManager().checkConnectivity(this)){
            val jsonRequest = object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                try {

                    val resData = it.getJSONObject("data")
                    val success = resData.getBoolean("success")
                    if(success){

                        val data = resData.getJSONObject("data")

                        val editor : SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("user_id", data.getString("user_id")).apply()
                        editor.putString("name", data.getString("name")).apply()
                        editor.putString("email", data.getString("email")).apply()
                        editor.putString("mobile_number", data.getString("mobile_number")).apply()
                        editor.putString("address", data.getString("address")).apply()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    }else{
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()
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

    fun savePreferences(){
        loginSharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    class DeleteFavorites(val context: Context) : AsyncTask<Void, Void, Void>(){
        override fun doInBackground(vararg params: Void?): Void? {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "res-db").build()
            db.restaurantDao().deleteAll()
            return null
        }
    }
}
