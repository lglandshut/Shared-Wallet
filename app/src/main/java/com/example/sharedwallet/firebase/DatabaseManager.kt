package com.example.sharedwallet.firebase

import android.util.Log
import com.example.sharedwallet.firebase.objects.GroupDO
import com.example.sharedwallet.firebase.objects.UserDO
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.UUID

object DatabaseManager {

    private val db = FirebaseFirestore.getInstance()
    private val authManager = AuthManager

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

    fun createGroup(name: String, description: String) {

        val userId = authManager.getCurrentUserId()
        val groupId = UUID.randomUUID().toString()
        val group = GroupDO(groupId, name, description, arrayListOf(userId))

        db.collection("groups")
            .document(groupId)
            .set(group)
            .addOnSuccessListener {
                Log.d("Firestore", "Gruppe $groupId erfolgreich erstellt")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Fehler beim Erstellen der Gruppe $groupId", e)
            }
    }

    fun getGroups(callback: (ArrayList<GroupDO>) -> Unit) {
        val userId = authManager.getCurrentUserId()
        db.collection("groups")
            .whereArrayContains("members", userId)
            .get()
            .addOnSuccessListener { documents ->
                val list = arrayListOf<GroupDO>()
                for (document in documents) {
                    val group = document.toObject<GroupDO>()
                    list.add(group)
                }
                callback(list)
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "Error getting documents: ", exception)
            }
    }
}
