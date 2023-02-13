package com.fromjin.htss.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.htss.Model.SettingInfo
import com.fromjin.htss.R

class SettingListAdapter(private val settingList: MutableList<SettingInfo>): RecyclerView.Adapter<SettingListAdapter.SettingViewHolder>() {
    interface OnItemClickListener {
        fun onItemClick(view: View, data: SettingInfo, position: Int)
    }

    private var listener: OnItemClickListener? = null
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class SettingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var settingName: TextView
        var img: ImageView

        init {
            settingName = itemView.findViewById<TextView>(R.id.setting_item_name)
            img = itemView.findViewById(R.id.setting_item_img)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.setting_item_view, parent, false)
        return SettingViewHolder(view)
    }

    override fun onBindViewHolder(holder: SettingViewHolder, position: Int) {
        holder.settingName.text = settingList[position].name
        holder.img.setImageResource(settingList[position].img)

        if (position != RecyclerView.NO_POSITION) {
            holder.itemView.setOnClickListener {
                listener?.onItemClick(holder.itemView, settingList[position], position)
            }
        }


    }

    override fun getItemCount(): Int {
        return settingList.size
    }
}