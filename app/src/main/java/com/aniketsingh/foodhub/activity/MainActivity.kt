package com.aniketsingh.foodhub.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.aniketsingh.foodhub.R
import com.aniketsingh.foodhub.database.RestaurantDatabase
import com.aniketsingh.foodhub.fragment.FavoritesFragment
import com.aniketsingh.foodhub.fragment.HomeFragment
import com.aniketsingh.foodhub.fragment.ProfileFragment
import com.aniketsingh.foodhub.fragment.QuestionsFragment
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView

    lateinit var sharedPreferences: SharedPreferences

    var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame_layout)
        navigationView = findViewById(R.id.nav_view)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        setUpToolbar()

        openHome()

        val actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
            )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){

                R.id.nav_home -> {

                    openHome()

                    drawerLayout.closeDrawers()

                }
                R.id.nav_profile -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame_layout,
                            ProfileFragment()
                        ).commit()

                    supportActionBar?.title = "Profile"

                    drawerLayout.closeDrawers()

                }
                R.id.nav_favorites -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame_layout,
                            FavoritesFragment()
                        ).commit()

                    supportActionBar?.title = "Favorites"

                    drawerLayout.closeDrawers()

                }
                R.id.nav_order_history -> {
                    Toast.makeText(this, "Order History Clicked", Toast.LENGTH_SHORT).show()
                }
                R.id.nav_faqs -> {

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame_layout,
                            QuestionsFragment()
                        ).commit()

                    supportActionBar?.title = "Frequently Asked Questions"

                    drawerLayout.closeDrawers()

                }
                R.id.nav_log_out -> {
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder.setTitle("Confirmation")
                    dialogBuilder.setMessage("Are you sure you want to log out?")
                    dialogBuilder.setPositiveButton("Yes"){text, listener ->
                        sharedPreferences.edit().clear().apply()
                        getSharedPreferences(getString(R.string.preference_login), Context.MODE_PRIVATE).edit().putBoolean("isLoggedIn", false).apply()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    }
                    dialogBuilder.setNegativeButton("No"){text, listener ->
                        text.dismiss()
                    }

                    dialogBuilder.create()
                    dialogBuilder.show()
                }

            }

            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "All Restaurants"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        val id = item?.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun openHome() {

        CartActivity.DeleteOrder(this).execute().get()

        supportFragmentManager.beginTransaction()
            .replace(
                R.id.frame_layout,
                HomeFragment()
            ).commit()
        supportActionBar?.title = "All Restaurants"

        navigationView.setCheckedItem(R.id.nav_home)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame_layout)

        when (frag) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }

}
