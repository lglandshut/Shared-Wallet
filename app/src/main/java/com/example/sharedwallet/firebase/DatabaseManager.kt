package com.example.sharedwallet.firebase

import android.annotation.SuppressLint
import android.util.Log
import com.example.sharedwallet.firebase.objects.ExpenseDO
import com.example.sharedwallet.firebase.objects.GroupDO
import com.example.sharedwallet.firebase.objects.UserDO
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

    fun getAllUsers(callback: (Map<String, String>) -> Unit) {
        db.collection("user")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val userMap = mutableMapOf<String, String>()
                for (document in querySnapshot.documents) {
                    val userId = document.getString("userId")
                    val username = document.getString("username")
                    if (userId != null && username != null) {
                        userMap[userId] = username
                    }
                }
                callback(userMap)
            }
    }

    fun getUsersByUserId(userIds: List<String>, callback: (Map<String, String>) -> Unit) {
        val docRef = db.collection("user").whereIn("userId", userIds)
        docRef.get()
            .addOnSuccessListener { querySnapshot ->
                val userMap = mutableMapOf<String, String>()
                for (document in querySnapshot.documents) {
                    val userId = document.getString("userId")
                    val username = document.getString("username")
                    if (userId != null && username != null) {
                        userMap[userId] = username
                    }
                }
                callback(userMap)
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error getting users by userIds", e)
                callback(emptyMap()) // Return empty map on failure
            }
    }

    fun addFriend(userId: String) {
        db.collection("user")
            .document(authManager.getCurrentUserId())
            .update("friends", FieldValue.arrayUnion(userId))
    }

    fun getFriends(callback: (ArrayList<UserDO>) -> Unit) {
        val currentUser = authManager.getCurrentUserId()
        getUser(currentUser) { user ->
            if (user != null && !user.friends.isNullOrEmpty()) {
                val list = arrayListOf<UserDO>()
                val friends = user.friends
                var loadedCount = 0

                // Iterate over friends and load each friend
                friends.forEach { friendId ->
                    getUser(friendId) { friend ->
                        friend?.let { list.add(it) }
                        loadedCount++

                        // Check if all friends have been loaded
                        if (loadedCount == friends.size) {
                            callback(list)
                        }
                    }
                }
            } else {
                // If no friends are found, return an empty list
                callback(arrayListOf())
            }
        }
    }

    fun createGroup(name: String, description: String, groupId: String) {
        val group = GroupDO(groupId, name, description, arrayListOf(authManager.getCurrentUserId()))

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
        db.collection("groups")
            .whereArrayContains("members", authManager.getCurrentUserId())
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
            .addOnFailureListener {
                callback(null)
            }
    }

    fun addUsersToGroup(groupId: String, userIds: List<String?>, onComplete: () -> Unit) {
        db.collection("groups")
            .document(groupId)
            .update("members", FieldValue.arrayUnion(*userIds.toTypedArray()))
            .addOnSuccessListener {
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.e("DatabaseManager", "Fehler beim Hinzufügen der Nutzer: ", e)
            }
    }

    fun removeUserFromGroup(groupId: String) {
        db.collection("groups")
            .document(groupId)
            .update("members", FieldValue.arrayRemove(authManager.getCurrentUserId()))
    }

    fun addExpense(groupId: String, expenses: List<ExpenseDO>, onComplete: () -> Unit) {
        val groupDocRef = db.collection("groups").document(groupId)

        val updatedExpenses = expenses.map { expense ->
            expense.apply {
                this.expenseId = UUID.randomUUID().toString()
                this.paidBy = authManager.getCurrentUserId()
                this.date = com.google.firebase.Timestamp.now()
                this.isConfirmed = false
            }
        }

        groupDocRef.update("expenses", FieldValue.arrayUnion(*updatedExpenses.toTypedArray()))
            .addOnSuccessListener {
                Log.d("Firestore", "Expenses added to existing list using arrayUnion")
                onComplete()
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error adding expenses to existing list using arrayUnion", e)
            }
    }

    fun confirmExpense(expense: ExpenseDO, groupId: String, callback: () -> Unit) {
        val groupDocRef = db.collection("groups").document(groupId)

        groupDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val group = document.toObject(GroupDO::class.java)

                    // Suche nach der zu aktualisierenden Expense
                    val updatedExpenses = group?.expenses?.map { existingExpense ->
                        if (existingExpense.expenseId == expense.expenseId) {
                            existingExpense.copy(isConfirmed = true) // Setze isConfirmed auf true
                        } else {
                            existingExpense
                        }
                    }

                    // Aktualisierte Liste zurück in Firestore speichern
                    groupDocRef.update("expenses", updatedExpenses).addOnSuccessListener {
                        callback()
                    }
                }
            }
    }

    fun removeExpense(expense: ExpenseDO, groupId: String, onComplete: () -> Unit) {
        val groupDocRef = db.collection("groups").document(groupId)

        groupDocRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val group = document.toObject(GroupDO::class.java)

                    // Suche nach der zu aktualisierenden Expense
                    val updatedExpenses = group?.expenses?.filterNot { existingExpense ->
                        existingExpense.expenseId == expense.expenseId
                    }

                    // Aktualisierte Liste zurück in Firestore speichern
                    groupDocRef.update("expenses", updatedExpenses).addOnSuccessListener {
                        onComplete()
                    }
                }
            }
    }

}
