package com.changs.android.gnuting_android.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.changs.android.gnuting_android.R
import com.changs.android.gnuting_android.databinding.HomePagerItemBinding


class ViewPagerAdapter(private val images: List<String>) :
    RecyclerView.Adapter<ViewPagerAdapter.ViewHolder>() {

    class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.home_pager_item, parent, false)
    ) {
        private val binding = HomePagerItemBinding.bind(itemView)

        fun bind(item: String) {
            Glide.with(binding.root.context).load(item).centerCrop()
                .error(R.drawable.background_radius_10dp_solid_gray7).into(binding.homePagerItemImg)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(viewGroup)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(images[position])
    }

    override fun getItemCount() = images.size

}


