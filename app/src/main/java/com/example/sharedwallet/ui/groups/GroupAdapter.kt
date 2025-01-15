package com.example.sharedwallet.ui.groups

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedwallet.ui.groupdetail.GroupDetailActivity
import com.example.sharedwallet.R
import com.example.sharedwallet.firebase.objects.GroupDO

class GroupAdapter(private val groups: List<GroupDO>) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val groupName: TextView = itemView.findViewById(R.id.group_name)
        val groupDescription: TextView = itemView.findViewById(R.id.group_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.group_row_item, parent, false)
        return GroupViewHolder(view)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.groupName.text = group.name
        holder.groupDescription.text = group.description
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, GroupDetailActivity::class.java).apply {
                // Pass the group ID to the GroupDetailActivity
                putExtra("GROUP_ID", group.groupId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = groups.size
}
