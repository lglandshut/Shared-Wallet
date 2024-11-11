package com.example.sharedwallet.ui.friends

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
import com.example.sharedwallet.databinding.FragmentFriendsBinding

class FriendsFragment : Fragment() {

    private var _binding: FragmentFriendsBinding? = null
    private lateinit var adapter: FriendsAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val friendsViewModel =
            ViewModelProvider(this)[FriendsViewModel::class.java]

        _binding = FragmentFriendsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()
        binding.fab.setOnClickListener {
            showAddFriendDialog(friendsViewModel)
        }

        // Beobachtet die Gruppenliste und aktualisiert die UI
        friendsViewModel.friends.observe(viewLifecycleOwner) { friendsList ->
            adapter = FriendsAdapter(friendsList)
            binding.recyclerViewFriends.adapter = adapter
        }

        friendsViewModel.loadFriends()

        return root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewFriends.layoutManager = LinearLayoutManager(context)
        adapter = FriendsAdapter(emptyList()) // Start mit leerer Liste
        binding.recyclerViewFriends.adapter = adapter
    }

    private fun showAddFriendDialog(friendsViewModel: FriendsViewModel) {
        // Layout für das Dialogfenster aufbauen
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_friend, null)

        // EditText für Gruppennamen und Beschreibung
        val userNameEditText = dialogView.findViewById<EditText>(R.id.editUsernameOrEmail)

        // Dialog erstellen
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add New Friend")
            .setView(dialogView)
            .setPositiveButton("Add") { dialogInterface, _ ->
                // Wenn "Add" geklickt wird, nach User suchen und hinzufügen
                val friendUsernameOrEmail = userNameEditText.text.toString()

                if (friendUsernameOrEmail.isNotEmpty()) {
                    // Hier kannst du die Gruppe speichern, z.B. in Firestore
                    friendsViewModel.searchUser(friendUsernameOrEmail) { result ->
                        if (!result) {
                            Toast.makeText(
                                requireContext(),
                                "User not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
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