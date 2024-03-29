package com.changs.android.gnuting_android.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.changs.android.gnuting_android.R

class SpinnerAdapter(context: Context, private val list: List<String>?) : BaseAdapter() {
    private val inflater: LayoutInflater

    var item: String? = null
        private set

    init {
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return if (list != null) list.size - 1 else 0
    }

    override fun getItem(position: Int): Any {
        return list!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) view = inflater.inflate(R.layout.spinner_outer_view, parent, false)
        return view!!
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) view = inflater.inflate(R.layout.spinner_inner_view, parent, false)
        if (list != null) {
            item = list[position]
            (view!!.findViewById<View>(R.id.spinner_text) as TextView).text = item
        }
        return view!!
    }
}

