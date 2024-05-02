package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.databinding.AddMemberItemBinding


class AddMemberAdapter(
    private val myUserId: Int?,
    private val navigateListener: (InUser) -> Unit,
    private val listener: (InUser, Boolean) -> Unit
) : ListAdapter<InUser, AddMemberAdapter.ViewHolder>(object : DiffUtil.ItemCallback<InUser>() {
    override fun areItemsTheSame(oldItem: InUser, newItem: InUser): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: InUser, newItem: InUser): Boolean {
        return oldItem.id == newItem.id
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, myUserId, navigateListener, listener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class ViewHolder(
        parent: ViewGroup,
        private val myUserId: Int?,
        private val navigateListener: (InUser) -> Unit,
        private val listener: (InUser, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.add_member_item, parent, false)
    ) {
        private val binding = AddMemberItemBinding.bind(itemView)

        fun bind(item: InUser) {
            if (item.isChecked) {
                binding.addMemberItemBtn.text = "삭제"
                binding.addMemberItemBtn.setTextColor(
                    binding.root.context.resources.getColor(
                        R.color.white, null
                    )
                )
                binding.addMemberItemBtn.setBackgroundResource(R.drawable.background_radius_100dp_solid_secondary)
            } else {
                binding.addMemberItemBtn.text = "추가"
                binding.addMemberItemBtn.setBackgroundResource(R.drawable.background_radius_100dp_stroke_secondary)
                binding.addMemberItemBtn.setTextColor(
                    binding.root.context.resources.getColor(
                        R.color.secondary, null
                    )
                )
            }

            if (myUserId == item.id) {
                binding.addMemberItemBtn.visibility = View.INVISIBLE
            } else {
                binding.addMemberItemBtn.visibility = View.VISIBLE
            }

            binding.addMemberItemBtn.setOnClickListener {
                if (myUserId != item.id) {
                    item.isChecked = !item.isChecked

                    if (item.isChecked) {
                        binding.addMemberItemBtn.text = "삭제"
                        binding.addMemberItemBtn.setBackgroundResource(R.drawable.background_radius_100dp_solid_secondary)
                        binding.addMemberItemBtn.setTextColor(
                            binding.root.context.resources.getColor(
                                R.color.white, null
                            )
                        )
                    } else {
                        binding.addMemberItemBtn.text = "추가"
                        binding.addMemberItemBtn.setBackgroundResource(R.drawable.background_radius_100dp_stroke_secondary)
                        binding.addMemberItemBtn.setTextColor(
                            binding.root.context.resources.getColor(
                                R.color.secondary, null
                            )
                        )
                    }

                    listener(item, item.isChecked)
                }
            }

            binding.addMemberItemTxtName.text = item.nickname

            Glide.with(binding.root.context)
                .load(item.profileImage)
                .error(R.drawable.ic_profile)
                .into(binding.addMemberItemImg)

            binding.addMemberItemImg.setOnClickListener {
                navigateListener(item)
            }

            val info = "${item.studentId} | ${item.department}"

            binding.addMemberItemTxtMemberInfo.text = info
            binding.addMemberItemTxtMemberIntro.text = item.userSelfIntroduction
        }
    }
}