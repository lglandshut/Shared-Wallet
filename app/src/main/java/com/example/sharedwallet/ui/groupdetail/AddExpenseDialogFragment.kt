package com.example.sharedwallet.ui.groupdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.sharedwallet.R
import com.example.sharedwallet.firebase.objects.ExpenseDO
import com.example.sharedwallet.firebase.objects.UserDO

class AddExpenseDialogFragment(private var userList: List<UserDO>) : DialogFragment() {

    private lateinit var recyclerViewAdapter: UserDeptSplitAdapter
    private val viewModel: GroupDetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_add_expense, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.visibility = View.VISIBLE
        val expenseAmount = view.findViewById<EditText>(R.id.expense_amount)
        val expenseReason = view.findViewById<EditText>(R.id.expense_reason)

        // RecyclerView konfigurieren
        recyclerViewAdapter = UserDeptSplitAdapter(userList.map { UserDebt(it.userId.toString(),
            0.0) }.toList())

        val cancelButton = view.findViewById<Button>(R.id.button_cancel)
        cancelButton.setOnClickListener {
            dismiss() // Close the dialog
        }

        val addButton = view.findViewById<Button>(R.id.button_add)
        addButton.setOnClickListener {
            val expense = ExpenseDO(null, null, null,
                expenseAmount.text.toString().toDouble(), expenseReason.text.toString())
            viewModel.addExpense(listOf(expense))
            dismiss() // Close the dialog
        }

        val distributionTypeRadioGroup = view.findViewById<RadioGroup>(R.id.distribution_type)
        distributionTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.distribution_equal -> {
                    expenseAmount.visibility = View.VISIBLE
                    expenseReason.visibility = View.VISIBLE
                }

                R.id.distribution_percentage -> {
                    expenseAmount.visibility = View.GONE
                    expenseReason.visibility = View.GONE
                }
            }
        }
    }
}
