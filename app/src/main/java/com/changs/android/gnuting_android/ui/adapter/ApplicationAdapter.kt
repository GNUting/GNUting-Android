package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.data.model.ApplicationResult
import com.changs.android.gnuting_android.databinding.ApplicationListItemBinding


class ApplicationAdapter(
    private val type: ApplicationType,
    private val listener: (ApplicationResult) -> Unit,
    private val deleteListener: (Int) -> Unit
) : ListAdapter<ApplicationResult, ApplicationAdapter.ViewHolder>(object :
    DiffUtil.ItemCallback<ApplicationResult>() {
    override fun areItemsTheSame(
        oldItem: ApplicationResult, newItem: ApplicationResult
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ApplicationResult, newItem: ApplicationResult
    ): Boolean {
        return oldItem == newItem
    }
}) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent, listener, deleteListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(type, currentList[position])
    }

    class ViewHolder(
        parent: ViewGroup,
        val listener: (ApplicationResult) -> Unit,
        private val deleteListener: (Int) -> Unit
    ) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.application_list_item, parent, false)
    ) {
        private val binding = ApplicationListItemBinding.bind(itemView)

        fun bind(type: ApplicationType, item: ApplicationResult) {
            binding.root.setOnClickListener {
                listener(item)
            }

            binding.root.setOnLongClickListener {
                if (item.applyStatus != "대기중") deleteListener(item.id)
                true
            }


            when (type) {
                ApplicationType.APPLY -> {
                    if (item.participantUserCount == 1) {
                        binding.applicationListTxtMember.text =
                            "1:1 매칭"
                    } else {
                        binding.applicationListTxtMember.text =
                            "과팅 (${item.participantUserCount}:${item.participantUserCount}매칭)"
                    }
                }

                ApplicationType.PARTICIPANT -> {
                    if (item.applyUserCount == 1) {
                        binding.applicationListTxtMember.text =
                            "1:1 매칭"
                    } else {
                        binding.applicationListTxtMember.text =
                            "과팅 (${item.applyUserCount}:${item.applyUserCount}매칭)"
                    }
                }
            }


            when (item.applyStatus) {
                "대기중" -> {
                    binding.applicationListTxtStatus.text = "대기중"
                    binding.applicationListLlStatusContainer.setBackgroundResource(R.drawable.background_radius_10dp_solid_gray7)
                }

                "거절" -> {
                    binding.applicationListTxtStatus.text = "거절됨"
                    binding.applicationListLlStatusContainer.setBackgroundResource(R.drawable.background_radius_10dp_solid_main)
                }

                else -> {
                    binding.applicationListTxtStatus.text = "수락"
                    binding.applicationListLlStatusContainer.setBackgroundResource(R.drawable.background_radius_10dp_solid_secondary)
                }
            }
        }
    }

    enum class ApplicationType {
        APPLY, PARTICIPANT
    }
}
