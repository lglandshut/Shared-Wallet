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

    fun createGroup(name: String, description: String, groupId: String) {
        val userId = authManager.getCurrentUserId()
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

    fun getUserDebtsByGroupId(groupId: String, callback: (List<ExpenseDO>) -> Unit) {
        db.collection("groups")
            .document(groupId)
            .collection("expenses")
            .get()
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
                this.paidFor = this.paidBy
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

    fun getExpensesByPaidByUserId(groupId: String, userId: String, callback: (List<ExpenseDO>) -> Unit) {
        val groupDocRef = db.collection("groups").document(groupId)

        groupDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val group = documentSnapshot.toObject(GroupDO::class.java)
                if (group != null) {
                    val expenses = group.expenses ?: emptyList()
                    val filteredExpenses = expenses.filter { it.paidBy == userId }
                    callback(filteredExpenses)
                } else {
                    Log.w("Firestore", "Group document not found: $groupId")
                    callback(emptyList()) // Return empty list if group not found
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error fetching group document: $groupId", e)
                callback(emptyList()) // Return empty list on failure
            }
    }

}
