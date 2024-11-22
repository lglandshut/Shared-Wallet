package com.example.sharedwallet.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.example.sharedwallet.firebase.objects.ExpenseDO
import com.example.sharedwallet.firebase.objects.GroupDO
import com.example.sharedwallet.firebase.objects.UserDO
import com.example.sharedwallet.ui.groupdetail.UserDebt
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import java.util.UUID

object DatabaseManager {

    @SuppressLint("StaticFieldLeak")
    private val db = FirebaseFirestore.getInstance()
    private val authManager = AuthManager


    fun createUser(userId: String, username: String, email: String) {
        val user = UserDO(userId, email, username, null, FieldValue.serverTimestamp())

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

    fun addFriend(userId: String) {
        val currentUser = authManager.getCurrentUserId()
        db.collection("user")
            .document(currentUser)
            .update("friends", FieldValue.arrayUnion(userId))
    }

    fun getFriends(callback: (ArrayList<UserDO>) -> Unit) {
        getUser(authManager.getCurrentUserId()) { currentUser ->
            if (currentUser != null && !currentUser.friends.isNullOrEmpty()) {
                val list = arrayListOf<UserDO>()
                val friends = currentUser.friends
                var loadedCount = 0

                // Iteriere über die Freundesliste und hole die Daten der einzelnen Freunde
                friends.forEach { friendId ->
                    getUser(friendId) { friend ->
                        friend?.let { list.add(it) }
                        loadedCount++

                        // Prüfe, ob alle Freunde geladen sind, bevor der Callback aufgerufen wird
                        if (loadedCount == friends.size) {
                            callback(list)
                        }
                    }
                }
            } else {
                // Falls keine Freunde vorhanden sind, rufe den Callback mit einer leeren Liste auf
                callback(arrayListOf())
            }
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

    fun getGroupById(groupId: String, callback: (GroupDO) -> Unit) {
        val docRef = db.collection("groups").document(groupId)
        docRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val group = documentSnapshot.toObject<GroupDO>()
                if (group != null) {
                    callback(group)
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Fehler beim Abfragen der Gruppe $groupId", e)
            }
    }

    fun getUserByEmail(username: String, callback: (UserDO?) -> Unit) {
        db.collection("user")
            .whereEqualTo("email", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(null)
                } else {
                    callback(documents.first().toObject<UserDO>())
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun getUserByUsername(username: String, callback: (UserDO?) -> Unit) {
        db.collection("user")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(null)
                } else {
                    callback(documents.first().toObject<UserDO>())
                }
            }
            .addOnFailureListener{
                callback(null)
            }
    }

    fun addExpenseToGroup(groupId: String, expense: ExpenseDO) {
        db.collection("groups")
            .document(groupId)
            .collection("expenses")
            .document(expense.expenseId ?: UUID.randomUUID().toString())
            .set(expense)
    }

    fun getUserDebtsByGroupId(groupId: String, callback: (List<UserDebt>) -> Unit) {
        db.collection("groups")
            .document(groupId)
            .collection("expenses")
            .get()
    }

    fun addUsersToGroup(groupId: String, userIds: List<String?>) {
        db.collection("groups")
            .document(groupId)
            .update("members", FieldValue.arrayUnion(*userIds.toTypedArray()))
    }

}
