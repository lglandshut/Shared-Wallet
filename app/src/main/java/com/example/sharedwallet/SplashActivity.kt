package com.example.sharedwallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedwallet.firebase.AuthManager

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var authManager: AuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authManager = AuthManager()
        if (authManager.isUserLoggedIn()) {
            // Benutzer ist eingeloggt, zeige MainActivity
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            // Benutzer ist nicht eingeloggt, zeige LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish() // Schließt SplashActivity
    }
}
