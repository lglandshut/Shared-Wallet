package com.example.sharedwallet.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sharedwallet.databinding.FragmentActivityBinding
import com.example.sharedwallet.firebase.AuthManager

class ActivityFragment : Fragment() {

    private var _binding: FragmentActivityBinding? = null
    private val binding get() = _binding!!
    private lateinit var activityFragmentAdapter: ActivityFragmentAdapter
    private val authManager = AuthManager
    private val currentUser = authManager.getCurrentUserId()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel =
            ViewModelProvider(this).get(ActivityViewModel::class.java)

        _binding = FragmentActivityBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // RecyclerView konfigurieren
        activityFragmentAdapter = ActivityFragmentAdapter(emptyList(), currentUser)
        binding.recyclerViewActivities.adapter = activityFragmentAdapter

        viewModel.loadData()
        viewModel.expenses.observe(viewLifecycleOwner) { expenses ->
            activityFragmentAdapter.updateData(expenses, viewModel.userIdToUserNameMap)
        }


        return root
    }

    override fun onResume() {
        super.onResume()
        val viewModel =
            ViewModelProvider(this).get(ActivityViewModel::class.java)
        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}