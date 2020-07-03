package com.aniketsingh.foodhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class RegistrationActivity : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences

    lateinit var etName : EditText
    lateinit var etEmail : EditText
    lateinit var etMobile : EditText
    lateinit var etAddress : EditText
    lateinit var etPassword : EditText
    lateinit var etConfirmPassword : EditText
    lateinit var etBtn : Button
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        etName = findViewById(R.id.registration_name)
        etEmail = findViewById(R.id.registration_email)
        etMobile = findViewById(R.id.registration_mobile_number)
        etAddress = findViewById(R.id.registration_address)
        etPassword = findViewById(R.id.registration_password)
        etConfirmPassword = findViewById(R.id.registration_confirm_password)
        etBtn = findViewById(R.id.registration_btn)
        toolbar = findViewById(R.id.toolbar_registration)

        setUpToolbar("Register Yourself")
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        etBtn.setOnClickListener {

            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val address = etAddress.text.toString()
            val mobile = etMobile.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            when {
                name == "" -> {
                    etName.error = "Enter name"
                }
                email == "" -> {
                    etEmail.error = "Enter email"
                }
                mobile == "" -> {
                    etMobile.error = "Enter mobile number"
                }
                mobile.length != 10 ->{
                    etMobile.error = "Mobile Number must be 10 characters long"
                }
                address == "" -> {
                    etAddress.error = "Enter address"
                }
                password == "" -> {
                    etPassword.error = "Enter password"
                }
                password.length < 4 ->{
                    etPassword.error = "Password must be at least 4 characters long"
                }
                confirmPassword == "" -> {
                    etConfirmPassword.error = "Enter password again"
                }
                confirmPassword == password -> {
                    sendCredentials(name, mobile, password, address, email)
                }
                else -> {
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_LONG).show()
                }
            }

        }

    }

    fun sendCredentials(name : String, mobile : String, password : String, address : String, email : String){

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/register/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("name", name)
        jsonParams.put("mobile_number", mobile)
        jsonParams.put("password", password)
        jsonParams.put("address", address)
        jsonParams.put("email", email)

        //Log.d("json", jsonParams.toString())

        if(ConnectionManager().checkConnectivity(this)){
            val jsonRequest = object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                try {

                    //Log.d("responsejson", it.toString())

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

                        //Log.d("editor response", editor.toString())

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                    }else{
                        Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
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

}
