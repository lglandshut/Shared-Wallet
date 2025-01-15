package com.example.sharedwallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedwallet.firebase.AuthManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val authManager = AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (authManager.isUserLoggedIn()) {
            // User is logged in, show MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // User is not logged in, show LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish() // Close SplashActivity
    }
}
