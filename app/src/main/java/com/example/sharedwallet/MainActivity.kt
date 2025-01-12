package com.example.sharedwallet

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sharedwallet.databinding.ActivityMainBinding
import com.example.sharedwallet.firebase.AuthManager
import com.example.sharedwallet.firebase.DatabaseManager
import com.example.sharedwallet.ui.groups.GroupFragment
import com.example.sharedwallet.ui.groups.GroupViewModel
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var navController: NavController
    private lateinit var headerView: View
    private val databaseManager = DatabaseManager
    private val authManager = AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViews()
        initListeners()
        loadData()
    }

    public override fun onStart() {
        super.onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_refresh) {
            refreshGroups() // Method to refresh groups
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refreshGroups() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
                as? androidx.navigation.fragment.NavHostFragment
        val currentFragment = navHostFragment?.childFragmentManager?.primaryNavigationFragment

        if (currentFragment is GroupFragment) {
            val groupViewModel = ViewModelProvider(currentFragment)[GroupViewModel::class.java]
            groupViewModel.loadGroups()
        } else {
            Toast.makeText(this, "No GroupFragment active to refresh", Toast.LENGTH_SHORT).show()
        }
    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun initViews() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        headerView = navView.getHeaderView(0)
        navController = findNavController(R.id.nav_host_fragment_content_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_groups, R.id.nav_activity, R.id.nav_friends
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    private fun initListeners() {

        headerView.findViewById<ImageView>(R.id.imageView_logout).setOnClickListener { _ ->
            authManager.signOut()
            //Back to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Schließt MainActivity
        }
    }

    private fun loadData() {
        val textEmail = headerView.findViewById<TextView>(R.id.textEmail)
        val textUsername = headerView.findViewById<TextView>(R.id.text_username)
        if (authManager.isUserLoggedIn()) {
            databaseManager.getUser(authManager.getCurrentUserId()) { user ->
                if (user != null) {
                    textEmail.text = user.email
                    textUsername.text = user.username
                } else {
                    textEmail.text = getString(R.string.user_not_found)
                    textUsername.text = getString(R.string.user_not_found)
                }
            }

        }
    }

}
