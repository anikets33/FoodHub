package com.aniketsingh.foodhub.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.util.ConnectionManager
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etMobile: EditText
    lateinit var etEmail: EditText
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etMobile = findViewById(R.id.fg_mobile)
        etEmail = findViewById(R.id.fg_email)
        btnNext = findViewById(R.id.fg_next_btn)

        btnNext.setOnClickListener {

            when {
                etEmail.text.toString() == "" -> {
                    etEmail.error = "Email required"
                }
                etMobile.text.toString() == "" || etMobile.text.toString().length != 10 -> {
                    etMobile.error = "Enter correct mobile number"
                }
                else -> {
                    receiveCode(etMobile.text.toString(), etEmail.text.toString())
                }
            }

        }
    }

    fun receiveCode(mobile: String, email: String) {

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobile)
        jsonParams.put("email", email)

        if (ConnectionManager().checkConnectivity(this)) {
            val jsonRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                    try {

                        val resData = it.getJSONObject("data")
                        val success = resData.getBoolean("success")
                        if (success) {

                            val intent = Intent(this, ResetPasswordActivity::class.java)
                            intent.putExtra("mobile", mobile)
                            startActivity(intent)

                        } else {
                            Toast.makeText(this, "Some error occurred", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "Some unexpected error occurred", Toast.LENGTH_SHORT)
                            .show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(this, "Volley Error $it", Toast.LENGTH_SHORT).show()
                }) {

                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "271706b81cebcc"
                        return headers
                    }

                }

            queue.add(jsonRequest)

        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Cancel") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }

            dialog.create()
            dialog.show()
        }

    }

}