package com.fromjin.htss.RecyclerView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.htss.Model.NewsInfo
import com.fromjin.htss.R
import java.text.SimpleDateFormat

class NewsNoStockListAdapter(val itemList: MutableList<NewsInfo>) :
    RecyclerView.Adapter<NewsNoStockListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): NewsNoStockListAdapter.ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.news_no_stock_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: NewsNoStockListAdapter.ViewHolder, position: Int) {
        holder.content.text = itemList[position].title
        holder.provider.text = itemList[position].provider
        val dateFormat = "MM.dd"
        holder.date.text = SimpleDateFormat(dateFormat).format(itemList[position].date)
        holder.link.setOnClickListener {
            linkClickListener.onClick(it, position)
        }

//        holder.ticker.setOnClickListener {
//            itemClickListener.onClick(it, position)
//        }


        // 아이템 클릭 이벤트
//        holder.itemView.setOnClickListener {
//            itemClickListener.onClick(it, position)
//        }


    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content: TextView = itemView.findViewById(R.id.news_contents)
        val provider: TextView = itemView.findViewById(R.id.news_provider)
        val date: TextView = itemView.findViewById(R.id.news_date)
//        val ticker: ImageView = itemView.findViewById(R.id.news_ticker_btn)
        val link: ImageView = itemView.findViewById(R.id.news_link_btn)
    }

//    interface OnItemClickListener {
//        fun onClick(v: View, position: Int)
//    }

    interface OnLinkClickListener {
        fun onClick(v: View, position: Int)
    }

//    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
//        this.itemClickListener = onItemClickListener
//    }

    fun setLinkClickListener(onLinkClickListener: OnLinkClickListener){
        this.linkClickListener = onLinkClickListener
    }

//    private lateinit var itemClickListener: OnItemClickListener
    private lateinit var linkClickListener: OnLinkClickListener
}
