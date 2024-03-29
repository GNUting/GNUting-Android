package com.changs.android.gnuting_android.ui.adapter

import android.provider.Settings.Global.getString
import android.text.Html
import android.text.Html.FROM_HTML_MODE_LEGACY
import android.text.Spanned
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.AlarmResult
import com.changs.android.gnuting_android.data.model.ApplicationItem
import com.changs.android.gnuting_android.data.model.ApplicationResult
import com.changs.android.gnuting_android.data.model.HomePostItem
import com.changs.android.gnuting_android.data.model.Member
import com.changs.android.gnuting_android.data.model.PostListItem
import com.changs.android.gnuting_android.databinding.AddMemberItemBinding
import com.changs.android.gnuting_android.databinding.AlarmListItemBinding
import com.changs.android.gnuting_android.databinding.ApplicationListItemBinding
import com.changs.android.gnuting_android.databinding.HomeListItemBinding
import com.changs.android.gnuting_android.databinding.PostListItemBinding
import com.changs.android.gnuting_android.databinding.PostMemberItemBinding


class AlarmAdapter(private val listener: (Int) -> Unit) :
    ListAdapter<AlarmResult, AlarmAdapter.ViewHolder>(object : DiffUtil.ItemCallback<AlarmResult>() {
        override fun areItemsTheSame(oldItem: AlarmResult, newItem: AlarmResult): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AlarmResult, newItem: AlarmResult): Boolean {
            return oldItem == newItem
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(parent: ViewGroup, val listener: (Int) -> Unit) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.alarm_list_item, parent, false)
    ) {
        private val binding = AlarmListItemBinding.bind(itemView)

        fun bind(item: AlarmResult) {
            binding.root.setOnClickListener {
                listener(item.id)
            }

            binding.alarmListItemTxtBody.text = item.body
            binding.alarmListItemTxtTime.text = item.time
        }
    }
}