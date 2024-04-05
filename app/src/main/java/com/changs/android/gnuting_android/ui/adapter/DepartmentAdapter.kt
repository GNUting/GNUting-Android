package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.SearchDepartmentResult
import com.changs.android.gnuting_android.databinding.DepartmentItemBinding


class DepartmentAdapter(private val listener: (String) -> Unit) :
    ListAdapter<SearchDepartmentResult, DepartmentAdapter.ViewHolder>(object : DiffUtil.ItemCallback<SearchDepartmentResult>() {
        override fun areItemsTheSame(oldItem: SearchDepartmentResult, newItem: SearchDepartmentResult): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SearchDepartmentResult, newItem: SearchDepartmentResult): Boolean {
            return oldItem.id == newItem.id
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(parent: ViewGroup, private val listener: (String) -> Unit) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.department_item, parent, false)
    ) {
        private val binding = DepartmentItemBinding.bind(itemView)

        fun bind(item: SearchDepartmentResult) {
            binding.departmentItemTxt.text = item.name
            binding.root.setOnClickListener {
                listener(item.name)
            }
        }
    }
}