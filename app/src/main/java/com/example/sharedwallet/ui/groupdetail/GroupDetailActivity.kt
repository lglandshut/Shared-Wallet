package com.example.sharedwallet.ui.groupdetail

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.sharedwallet.R
import com.example.sharedwallet.databinding.ActivityGroupDetailBinding
import com.example.sharedwallet.firebase.objects.GroupDO
import com.google.android.material.snackbar.Snackbar

class GroupDetailActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityGroupDetailBinding
    private val viewModel: GroupDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setze die Toolbar als AppBar
        setSupportActionBar(binding.toolbar)

        // Gruppennamen aus Intent erhalten und als Titel setzen
        val groupId = intent.getStringExtra("GROUP_ID") ?: "Group Detail"
        supportActionBar?.title = groupId

        // Load group details and observe changes
        viewModel.loadGroup(groupId)
        viewModel.group.observe(this) { group ->
            updateUI(group)
        }

        binding.fabGroupDetail.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fabGroupDetail).show()
        }
    }

    private fun updateUI(group: GroupDO) {
        supportActionBar?.title = group.name
        // Weitere UI-Elemente können hier ebenfalls aktualisiert werden
        // binding.totalDebtAmount.text = group.totalDebt.toString()  // Beispielwert, je nach Attributen im GroupDO
    }
}