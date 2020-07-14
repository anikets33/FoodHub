package com.aniketsingh.foodhub.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.util.ConnectionManager
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var etOtp: EditText
    lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        etPassword = findViewById(R.id.reset_password)
        etConfirmPassword = findViewById(R.id.reset_confirm_password)
        etOtp = findViewById(R.id.reset_otp)
        btnSubmit = findViewById(R.id.reset_btn_submit)

        btnSubmit.setOnClickListener {

            when {
                etOtp.text.toString() == "" -> {
                    etOtp.error = "Enter OTP"
                }
                etPassword.text.toString() == "" || etPassword.text.toString().length < 4 -> {
                    etPassword.error = "Enter valid password"
                }
                etConfirmPassword.text.toString() != etPassword.text.toString() -> {
                    etConfirmPassword.error = "password does not match"
                }
                else -> {
                    sendCode(intent.getStringExtra("mobile"), etPassword.text.toString(), etOtp.text.toString())
                }
            }

        }

    }

    fun sendCode(mobile: String, password: String, otp: String) {

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/reset_password/fetch_result"

        val jsonParams = JSONObject()
        jsonParams.put("mobile_number", mobile)
        jsonParams.put("password", password)
        jsonParams.put("otp", otp)

        if (ConnectionManager().checkConnectivity(this)) {
            val jsonRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                    try {

                        val resData = it.getJSONObject("data")
                        val success = resData.getBoolean("success")
                        if (success) {

                            val successMessage = resData.getString("successMessage")
                            Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()

                            val intent = Intent(this, LoginActivity::class.java)
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