package com.example.sharedwallet.ui.groupdetail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedwallet.R
import com.example.sharedwallet.databinding.ActivityGroupDetailBinding
import com.example.sharedwallet.firebase.objects.GroupDO
import com.example.sharedwallet.firebase.objects.UserDO
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailBinding
    private val viewModel: GroupDetailViewModel by viewModels()
    private lateinit var recyclerViewAdapter: GroupDetailUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar setzen
        setSupportActionBar(binding.toolbar)

        // Gruppennamen aus Intent erhalten
        val groupId = intent.getStringExtra("GROUP_ID") ?: "Group Detail"
        supportActionBar?.title = groupId

        // RecyclerView konfigurieren
        recyclerViewAdapter = GroupDetailUserAdapter(emptyList())
        binding.recyclerViewExpensesPerUser.adapter = recyclerViewAdapter

        // Lade Daten
        viewModel.loadGroup(groupId)
        viewModel.group.observe(this) { group ->
            updateUI(group)
        }

        viewModel.userDebts.observe(this) { debts ->
            recyclerViewAdapter.updateData(debts)
        }

        viewModel.friendsToAdd.observe(this) { friendsToAdd ->
            // Aktualisiere die UI mit den gefilterten Freunden
            println(friendsToAdd)
        }

        // Speed Dial FAB
        val speedDialView = binding.fabGroupDetail
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.speeddial_add_friend, R.drawable.ic_menu_friends)
                .setLabel("Add Friend")
                .create()
        )
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.speeddial_add_expense, R.drawable.ic_dollar)
                .setLabel("Add Expense")
                .create()
        )
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(R.id.speeddial_settle_expense, R.drawable.ic_balance_wallet_24)
                .setLabel("Settle Expense")
                .create()
        )
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.speeddial_add_friend -> {
                    viewModel.loadFriendsList { filteredFriends ->
                        showAddFriendsToGroupDialog(filteredFriends)
                    }
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.speeddial_add_expense -> {
                    Toast.makeText(this, "Add expense", Toast.LENGTH_SHORT).show()
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                R.id.speeddial_settle_expense -> {
                    Toast.makeText(this, "Settle expense", Toast.LENGTH_SHORT).show()
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
            }
            false
        })
    }

    fun showAddFriendsToGroupDialog(friends: List<UserDO>) {
        // Erstelle eine Liste mit den Namen der Freunde
        val friendsInGroup = viewModel.group.value?.members ?: emptyList()
        val filteredFriends = friends.filterNot { friendsInGroup.contains(it.userId) }
        val friendNames = filteredFriends.map { it.username }.toTypedArray()

        // Array für die ausgewählten Elemente
        val selectedFriends = BooleanArray(filteredFriends.size) { false }

        // Dialog erstellen
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Add Friends to Group")
        dialogBuilder.setMultiChoiceItems(friendNames, selectedFriends) { _, which, isChecked ->
            selectedFriends[which] = isChecked // Markierung aktualisieren
        }
        dialogBuilder.setPositiveButton("Add") { _, _ ->
            // Ausgewählte Freunde zur Gruppe hinzufügen
            val selectedUserIds = filteredFriends
                .filterIndexed { index, _ -> selectedFriends[index] }
                .map { it.userId }
            viewModel.addFriendsToGroup(selectedUserIds)
        }
        dialogBuilder.setNegativeButton("Cancel", null)

        // Dialog anzeigen
        dialogBuilder.create().show()
    }


    private fun updateUI(group: GroupDO) {
        supportActionBar?.title = group.name
        // Weitere UI-Elemente können hier ebenfalls aktualisiert werden
        // binding.totalDebtAmount.text = group.totalDebt.toString()  // Beispielwert, je nach Attributen im GroupDO
    }
}