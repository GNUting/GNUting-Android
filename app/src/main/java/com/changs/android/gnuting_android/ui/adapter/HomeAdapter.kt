package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.HomePostItem
import com.changs.android.gnuting_android.databinding.HomeListItemBinding
import com.changs.android.gnuting_android.util.PostItemNavigator


class HomeAdapter(private val listener: PostItemNavigator) :
    ListAdapter<HomePostItem, HomeAdapter.ViewHolder>(object : DiffUtil.ItemCallback<HomePostItem>() {
        override fun areItemsTheSame(oldItem: HomePostItem, newItem: HomePostItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: HomePostItem, newItem: HomePostItem): Boolean {
            return oldItem.id == newItem.id
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
        return HomeAdapter.ViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(parent: ViewGroup, private val listener: PostItemNavigator) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.home_list_item, parent, false)
    ) {
        private val binding = HomeListItemBinding.bind(itemView)

        fun bind(item: HomePostItem) {
            binding.homeListItemTxtDepartment.text = item.department
            binding.homeListItemTxtTitle.text = item.title
            binding.root.setOnClickListener {
                listener.navigateToDetail(adapterPosition)
            }
        }
    }
}