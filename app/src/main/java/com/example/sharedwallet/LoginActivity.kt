package com.example.sharedwallet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedwallet.firebase.AuthManager
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {

    private val authManager = AuthManager
    private var checkedBox: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailInputLayout = findViewById<TextInputLayout>(R.id.emailInputLayout)
        val passwordInputLayout = findViewById<TextInputLayout>(R.id.passwordInputLayout)
        val usernameInputLayout = findViewById<TextInputLayout>(R.id.usernameInputLayout)
        val authButton = findViewById<Button>(R.id.authButton)

        // Listener for RadioGroup
        val authOptionGroup = findViewById<RadioGroup>(R.id.authOptionGroup)
        authOptionGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioLogin -> {
                    emailInputLayout.visibility = View.VISIBLE
                    passwordInputLayout.visibility = View.VISIBLE
                    usernameInputLayout.visibility = View.GONE
                    authButton.text = getString(R.string.login)
                    checkedBox = 1
                }
                R.id.radioRegister -> {
                    emailInputLayout.visibility = View.VISIBLE
                    passwordInputLayout.visibility = View.VISIBLE
                    usernameInputLayout.visibility = View.VISIBLE
                    authButton.text = getString(R.string.create_account)
                    checkedBox = 2
                }
            }
            authButton.isEnabled = true // Activate Button if RadioGroup is checked
        }

        authButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
            val username = findViewById<EditText>(R.id.usernameEditText).text.toString()

            if (checkedBox == 1) {
                authManager.signInWithEmail(email, password) { success, exception ->
                    if (success) startMainActivity()
                    else {
                        Toast.makeText(this, "Fehler: ${exception?.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } else {
                authManager.createUserWithEmail(email, password, username, this) { success ->
                    if (success) startMainActivity()
                }
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity
    }

}
