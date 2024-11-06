package com.example.sharedwallet.firebase

import android.util.Log
import com.example.sharedwallet.firebase.objects.UserDO
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class DatabaseManager {

    private val db = FirebaseFirestore.getInstance()

    fun createUser(userId: String, username: String, email: String) {
        val user = UserDO(userId, email, username, null,
            FieldValue.serverTimestamp())

        db.collection("user")
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Benutzer $username erfolgreich erstellt")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Fehler beim Erstellen des Benutzers $username", e)
            }
    }

    fun getUser(userId: String, callback: (UserDO?) -> Unit) {
        val docRef = db.collection("user").document(userId)
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject<UserDO>()
                callback(user)
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Fehler beim Abfragen des Benutzers $userId", e)
                callback(null)
            }
    }


    fun removeUser(userId: String) {
        db.collection("user")
            .document(userId) // Die userId des Benutzers
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Benutzer erfolgreich gelöscht")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Fehler beim Löschen des Benutzers", e)
            }
    }
}
