package com.example.sharedwallet.firebase

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import com.example.sharedwallet.LoginActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

object AuthManager {

    private val auth: FirebaseAuth = Firebase.auth
    private val databaseManager = DatabaseManager

    fun createUserWithEmail(email: String, password: String, username: String, activity: LoginActivity, onComplete: (Boolean) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    onComplete(true)
                    auth.currentUser?.let { databaseManager.createUser(it.uid, username, email) }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        activity,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    onComplete(false)
                }
            }
    }

    fun signInWithEmail(email: String, password: String, onComplete: (Boolean, Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception)
                }
            }
    }

    fun signOut() {
        auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        Log.d(TAG, "currentUser:" + auth.currentUser)
        return auth.currentUser != null
    }

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid.toString()
    }
}
