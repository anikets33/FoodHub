package com.aniketsingh.foodhub.fragment

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.aniketsingh.foodhub.R

class ProfileFragment : Fragment() {

    lateinit var txtName : TextView
    lateinit var txtMobile : TextView
    lateinit var txtEmail : TextView
    lateinit var txtAddress : TextView

    var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        txtName = view.findViewById(R.id.profile_name)
        txtMobile = view.findViewById(R.id.profile_mobile)
        txtEmail = view.findViewById(R.id.profile_email)
        txtAddress = view.findViewById(R.id.profile_address)

        sharedPreferences = activity?.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        txtName.text = sharedPreferences?.getString("name", "Profile Name")
        txtMobile.text = sharedPreferences?.getString("mobile_number", "9999999999")
        txtEmail.text = sharedPreferences?.getString("email", "xyz@info.com")
        txtAddress.text = sharedPreferences?.getString("address", "Delhi")

        return view
    }

}
