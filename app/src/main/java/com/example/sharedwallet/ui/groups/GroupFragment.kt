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

    // This property is only valid between onCreateView and
    // onDestroyView.
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

        // Beobachtet die Gruppenliste und aktualisiert die UI
        groupViewModel.groups.observe(viewLifecycleOwner) { newGroupList ->
            adapter = GroupAdapter(newGroupList)
            binding.recyclerViewGroups.adapter = adapter
        }

        groupViewModel.loadGroups()

        return root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewGroups.layoutManager = LinearLayoutManager(context)
        adapter = GroupAdapter(emptyList()) // Start mit leerer Liste
        binding.recyclerViewGroups.adapter = adapter
    }

    private fun showAddGroupDialog(groupViewModel: GroupViewModel) {
        // Layout für das Dialogfenster aufbauen
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_group, null)

        // EditText für Gruppennamen und Beschreibung
        val groupNameEditText = dialogView.findViewById<EditText>(R.id.editGroupName)
        val groupDescriptionEditText = dialogView.findViewById<EditText>(R.id.editGroupDescription)

        // Dialog erstellen
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add New Group")
            .setView(dialogView)
            .setPositiveButton("Save") { dialogInterface, _ ->
                // Wenn "Save" geklickt wird, die Eingaben validieren und speichern
                val groupName = groupNameEditText.text.toString()
                val groupDescription = groupDescriptionEditText.text.toString()

                if (groupName.isNotEmpty() && groupDescription.isNotEmpty()) {
                    // Hier kannst du die Gruppe speichern, z.B. in Firestore
                    val newGroup = GroupDO(name = groupName, description = groupDescription)
                    groupViewModel.addGroup(newGroup) // Methode im ViewModel, um die Gruppe hinzuzufügen
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

        // Den Dialog anzeigen
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}