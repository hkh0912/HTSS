package com.fromjin.htss.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.htss.Model.RankInfo
import com.fromjin.htss.R

class SectorThemeListAdapter(val itemList: MutableList<RankInfo>) :
    RecyclerView.Adapter<SectorThemeListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SectorThemeListAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.sector_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: SectorThemeListAdapter.ViewHolder, position: Int) {
        holder.name.text = itemList[position].name

        if (itemList[position].rate.substring(0, 1) == "+") {
            holder.valuePlus.text = itemList[position].rate
            holder.valuePlus.visibility = View.VISIBLE
            holder.valueMinus.visibility = View.GONE
        } else if (itemList[position].rate.substring(0, 1) == "-") {
            holder.valueMinus.text = itemList[position].rate
            holder.valueMinus.visibility = View.VISIBLE
            holder.valuePlus.visibility = View.GONE
        }

        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.sector_name)
        val valueMinus: TextView = itemView.findViewById(R.id.sector_rate_minus)
        val valuePlus: TextView = itemView.findViewById(R.id.sector_rate_plus)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener: OnItemClickListener
}