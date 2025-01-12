package com.example.sharedwallet.ui.groupdetail

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedwallet.R
import com.example.sharedwallet.databinding.ActivityGroupDetailBinding
import com.example.sharedwallet.firebase.AuthManager
import com.example.sharedwallet.firebase.objects.ExpenseDO
import com.example.sharedwallet.firebase.objects.UserDO
import com.example.sharedwallet.firebase.objects.UserDebt
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import kotlin.math.absoluteValue


class GroupDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupDetailBinding
    private val viewModel: GroupDetailViewModel by viewModels()
    private lateinit var groupDetailUserAdapter: GroupDetailUserAdapter
    private lateinit var groupExpensesAdapter: GroupDetailExpenseAdapter
    private val authManager = AuthManager
    private val currentUser = authManager.getCurrentUserId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set Toolbar
        setSupportActionBar(binding.toolbar)

        //Get groupid from intent
        val groupId = intent.getStringExtra("GROUP_ID") ?: "Group Detail"
        supportActionBar?.title = groupId

        // configure RecyclerView
        groupExpensesAdapter = GroupDetailExpenseAdapter(emptyList(), currentUser)
        binding.recyclerViewExpenses.adapter = groupExpensesAdapter
        groupDetailUserAdapter = GroupDetailUserAdapter(emptyList())
        binding.recyclerViewExpensesPerUser.adapter = groupDetailUserAdapter

        // load data
        viewModel.loadGroup(groupId)
        viewModel.group.observe(this) { group ->
            supportActionBar?.title = group.name
        }
        viewModel.expenses.observe(this) { expenses ->
            groupExpensesAdapter.updateData(expenses, viewModel.userIdToUserNameMap)
        }

        viewModel.userDebts.observe(this) { debts ->
            groupDetailUserAdapter.updateData(debts, viewModel.userIdToUserNameMap)

            //update Total Debts
            val totalDebt = debts.sumOf { it.userDebt ?: 0.0 }
            binding.totalDebtAmount.text = "%.2f €".format(totalDebt)
            if (totalDebt < 0.0) binding.totalDebtAmount.setTextColor(Color.RED)
            else binding.totalDebtAmount.setTextColor(Color.GREEN)
        }

        viewModel.loadFriendsList()

        val leaveGroupButton = binding.imageViewLeaveGroup
        leaveGroupButton.setOnClickListener {
            showLeaveGroupDialog()
        }

        val refreshButton = binding.imageViewRefresh
        refreshButton.setOnClickListener {
            viewModel.loadGroup(groupId)
        }

        //speed Dial FAB
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
                    viewModel.group.value?.members?.let { member ->
                        openAddExpenseDialog(member.filterNot { it == currentUser }) }
                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }

                R.id.speeddial_settle_expense -> {
                    var debtList = viewModel.userDebts.value?.map { it.copy() }
                    debtList = debtList?.filterNot { debt -> debt.userDebt!! >= 0.0 }

                    //Check if there are debts to settle
                    if (!debtList.isNullOrEmpty()) { openSettleExpenseDialog(debtList) }
                    else {
                        speedDialView.close() // To close the Speed Dial with animation
                        Toast.makeText(this, "No debts to settle", Toast.LENGTH_SHORT).show()
                    }

                    speedDialView.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
            }
            false
        })
    }

    private fun openSettleExpenseDialog(debtList: List<UserDebt>) {
        //Array for names of users
        val debtItems = debtList.map { "${it.userName}: ${String.format("%.2f", it.userDebt?.absoluteValue)} €" }.toTypedArray()
        val checkedItems = BooleanArray(debtList.size) // Array, for selected items

        //create Dialog
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Did you settle the following debts?")

        //Multi-Choice-Checkbox-List
        dialogBuilder.setMultiChoiceItems(debtItems, checkedItems) { _, which, isChecked ->
            checkedItems[which] = isChecked // Refresh Checkbox
        }

        //Positive Button
        dialogBuilder.setPositiveButton("Settle") { dialog, _ ->
            //Check if all items are checked
            if (checkedItems.all { it }) {
                val settleDebtList = debtList.map { debt ->
                    ExpenseDO(null, currentUser,
                        viewModel.userIdToUserNameMap.entries.find { it.value == debt.userName }?.key,
                        debt.userDebt?.absoluteValue,
                        "Settle Debt")
                }
                viewModel.addExpense(settleDebtList)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please settle all debts before proceeding.", Toast.LENGTH_SHORT).show()
            }
        }

        //Negative Button
        dialogBuilder.setNegativeButton("Cancel", null)

        //Dialog show
        dialogBuilder.create().show()
    }

    private fun showAddFriendsToGroupDialog(friends: List<UserDO>) {
        val friendNames = friends.map { it.username }.toTypedArray()
        // Array for all selected friends
        val selectedFriends = BooleanArray(friendNames.size) { false }

        // Dialog create
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Add Friends to Group")
        dialogBuilder.setMultiChoiceItems(friendNames, selectedFriends) { _, which, isChecked ->
            selectedFriends[which] = isChecked // Markierung aktualisieren
        }
        dialogBuilder.setPositiveButton("Add") { _, _ ->
            // add selected friends to group
            val selectedUserIds = friends
                .filterIndexed { index, _ -> selectedFriends[index] }
                .map { it.userId }
            viewModel.addFriendsToGroup(selectedUserIds)
        }
        dialogBuilder.setNegativeButton("Cancel", null)

        //show dialogue
        dialogBuilder.create().show()
    }

    private fun showLeaveGroupDialog() {
        //create Dialogue
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Leave Group?")

        dialogBuilder.setPositiveButton("Yes") { _, _ ->
            if(viewModel.leaveGroup()) {
                finish()
            } else {
                Toast.makeText(this, "You owe money to other users, " +
                        "please settle them before leaving the group!", Toast.LENGTH_LONG).show()
            }
        }
        dialogBuilder.setNegativeButton("No", null)

        //show Dialogue
        dialogBuilder.create().show()
    }

    private fun openAddExpenseDialog(userList: List<String>) {
        val dialog = AddExpenseDialogFragment(userList)
        dialog.show(supportFragmentManager, "addExpenseDialog")
    }

}