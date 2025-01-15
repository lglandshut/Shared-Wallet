package com.example.sharedwallet.ui.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sharedwallet.R
import com.example.sharedwallet.databinding.FragmentGroupBinding
import com.example.sharedwallet.firebase.objects.GroupDO

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private lateinit var adapter: GroupAdapter
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val groupViewModel =
            ViewModelProvider(this)[GroupViewModel::class.java]

        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        binding.fab.setOnClickListener {
            showAddGroupDialog(groupViewModel)
        }

        // Observe the LiveData from the ViewModel and update recyclerView
        groupViewModel.groups.observe(viewLifecycleOwner) { newGroupList ->
            adapter = GroupAdapter(newGroupList)
            binding.recyclerViewGroups.adapter = adapter
        }

        groupViewModel.loadGroups()

        return root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewGroups.layoutManager = LinearLayoutManager(context)
        adapter = GroupAdapter(emptyList()) // Start with an empty list
        binding.recyclerViewGroups.adapter = adapter
    }

    private fun showAddGroupDialog(groupViewModel: GroupViewModel) {
        // Build Layout for dialogue
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_group, null)

        // EditText for the group name and description
        val groupNameEditText = dialogView.findViewById<EditText>(R.id.editGroupName)
        val groupDescriptionEditText = dialogView.findViewById<EditText>(R.id.editGroupDescription)

        // Create dialogue
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add New Group")
            .setView(dialogView)
            .setPositiveButton("Save") { dialogInterface, _ ->
                // When the user clicks the save button, validate and save the group
                val groupName = groupNameEditText.text.toString()
                val groupDescription = groupDescriptionEditText.text.toString()

                if (groupName.isNotEmpty() && groupDescription.isNotEmpty()) {
                    val newGroup = GroupDO(name = groupName, description = groupDescription)
                    groupViewModel.addGroup(newGroup)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Please fill in all fields",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancel") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }

        // Show dialogue
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}