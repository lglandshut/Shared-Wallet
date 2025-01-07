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
    private lateinit var groupDetailUserAdapter: GroupDetailUserAdapter
    private lateinit var groupExpensesAdapter: GroupDetailExpenseAdapter

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
        groupExpensesAdapter = GroupDetailExpenseAdapter(emptyList())
        binding.recyclerViewExpenses.adapter = groupExpensesAdapter
        groupDetailUserAdapter = GroupDetailUserAdapter(emptyList())
        binding.recyclerViewExpensesPerUser.adapter = groupDetailUserAdapter

        // Lade Daten
        viewModel.loadGroup(groupId)
        viewModel.group.observe(this) { group ->
            updateUI(group)
        }
        viewModel.expenses.observe(this) { expenses ->
            groupExpensesAdapter.updateData(expenses, viewModel.userIdToUserNameMap)
        }

        viewModel.loadUserDebts(groupId)
        viewModel.userDebts.observe(this) { debts ->
            groupDetailUserAdapter.updateData(debts)
        }

        viewModel.loadFriendsList()

        val leaveGroupButton = binding.imageViewLeaveGroup
        leaveGroupButton.setOnClickListener {
            showLeaveGroupDialog()
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
            SpeedDialActionItem.Builder(
                R.id.speeddial_settle_expense,
                R.drawable.ic_balance_wallet_24
            )
                .setLabel("Settle Expense")
                .create()
        )
        speedDialView.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.speeddial_add_friend -> {
                    viewModel.loadFriendsList()
                    showAddFriendsToGroupDialog(viewModel.friendsToAdd.value!!)
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }

                R.id.speeddial_add_expense -> {
                    viewModel.friendsToAdd.value?.let { openAddExpenseDialog(it) }
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

    private fun showAddFriendsToGroupDialog(friends: List<UserDO>) {
        val friendNames = friends.map { it.username }.toTypedArray()
        // Array für die ausgewählten Elemente
        val selectedFriends = BooleanArray(friendNames.size) { false }

        // Dialog erstellen
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Add Friends to Group")
        dialogBuilder.setMultiChoiceItems(friendNames, selectedFriends) { _, which, isChecked ->
            selectedFriends[which] = isChecked // Markierung aktualisieren
        }
        dialogBuilder.setPositiveButton("Add") { _, _ ->
            // Ausgewählte Freunde zur Gruppe hinzufügen
            val selectedUserIds = friends
                .filterIndexed { index, _ -> selectedFriends[index] }
                .map { it.userId }
            viewModel.addFriendsToGroup(selectedUserIds)
        }
        dialogBuilder.setNegativeButton("Cancel", null)

        // Dialog anzeigen
        dialogBuilder.create().show()
    }

    private fun showLeaveGroupDialog() {
        // Dialog erstellen
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Leave Group?")

        dialogBuilder.setPositiveButton("Yes") { _, _ ->
            viewModel.leaveGroup()
            finish()
        }
        dialogBuilder.setNegativeButton("No", null)

        // Dialog anzeigen
        dialogBuilder.create().show()
    }

    private fun openAddExpenseDialog(userList: List<UserDO>) {
        val dialog = AddExpenseDialogFragment(userList)
        dialog.show(supportFragmentManager, "addExpenseDialog")
    }

    private fun updateUI(group: GroupDO) {
        supportActionBar?.title = group.name
        // Weitere UI-Elemente können hier ebenfalls aktualisiert werden
        // binding.totalDebtAmount.text = group.totalDebt.toString()  // Beispielwert, je nach Attributen im GroupDO
    }
}