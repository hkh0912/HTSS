package com.fromjin.htss.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.htss.Model.NewsInfo
import com.fromjin.htss.Model.StockInfo
import com.fromjin.htss.R
import com.fromjin.htss.RecyclerView.NewsListAdapter
import com.fromjin.htss.RecyclerView.StockListAdapter
import com.fromjin.htss.Util.ResultModel.ResultIncludeSectorThemeList
import com.fromjin.htss.Util.ResultModel.ResultNewsList
import com.fromjin.htss.Util.RetrofitClient
import com.fromjin.htss.databinding.FragmentSectorResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SectorResultFragment : Fragment(), View.OnClickListener {

    private lateinit var view: FragmentSectorResultBinding

    val retrofit = RetrofitClient.create()


    private val stockList = mutableListOf<StockInfo>()
    private val stockListAdapter = StockListAdapter(stockList)
    var stockNum = 3

    private val newsList = mutableListOf<NewsInfo>()
    private val newsListAdapter = NewsListAdapter(newsList)
    var newsNum = 3

    private var sectorName = ""
    private var sectorRate = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentSectorResultBinding.inflate(inflater, container, false)

        sectorName = arguments?.getString("name").toString()
        sectorRate = arguments?.getString("rate").toString()

        getSectorIncludeStockList(sectorName, stockNum)
        getNewsLikeListByKeyword(sectorName, newsNum)

        view.sectorName.text = sectorName
        view.sectorName2.text = sectorName

        if (sectorRate.substring(0, 1) == "+") {
            view.sectorRatePlus.text = sectorRate
            view.sectorRatePlus.visibility = View.VISIBLE
            view.sectorRateMinus.visibility = View.GONE
        } else {
            view.sectorRateMinus.text = sectorRate
            view.sectorRateMinus.visibility = View.VISIBLE
            view.sectorRatePlus.visibility = View.GONE
        }


        view.stockRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = stockListAdapter
        }
        view.newsRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = newsListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    view.newsRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
        }

        stockListAdapter.setItemClickListener(object : StockListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                Log.d("주식", stockList[position].name)
                val bundle = Bundle()
                bundle.apply {
                    putString("ticker", stockList[position].ticker)
                    putString("name", stockList[position].name)
                    putString("rate", stockList[position].rate)
                    putInt("end_price", stockList[position].endPrice)
                }
                replaceFragment(StockFragment(), bundle)
            }
        })

        newsListAdapter.setItemClickListener(object : NewsListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(newsList[position].link)))
            }

        })

        //뒤로가기
        view.backBtn.setOnClickListener(this)
        view.stockOpenBtn.setOnClickListener(this)
        view.stockCloseBtn.setOnClickListener(this)
        view.newsOpenBtn.setOnClickListener(this)
        view.newsCloseBtn.setOnClickListener(this)



        return view.root
    }

    fun getSectorIncludeStockList(sector: String, num: Int){
        retrofit.getSectorIncludeList(sector, num).enqueue(object:
            Callback<ResultIncludeSectorThemeList> {
            override fun onResponse(
                call: Call<ResultIncludeSectorThemeList>,
                response: Response<ResultIncludeSectorThemeList>
            ) {
                if(response.code()==200) addSectorIncludeStockList(response.body())
                else  Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<ResultIncludeSectorThemeList>, t: Throwable) {
                Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                Log.d("API호출2", t.message.toString())
            }

        })
    }

    fun getNewsLikeListByKeyword(keyword: String, num: Int) {
        retrofit.getNewsLikeList(keyword, num).enqueue(object : Callback<ResultNewsList> {
            override fun onResponse(
                call: Call<ResultNewsList>,
                response: Response<ResultNewsList>
            ) {
                if (response.code() == 200) addResultNewsLikeList(response.body())
                else Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()

            }

            override fun onFailure(call: Call<ResultNewsList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }

        })
    }

    private fun addResultNewsLikeList(body: ResultNewsList?) {
        newsList.clear()
        if (!body.isNullOrEmpty()) {
            for (item in body) {
                newsList.add(NewsInfo(item.ticker, item.provider, item.date, item.title, item.rink))
            }
            newsListAdapter.notifyDataSetChanged()
        } else {

        }
    }

    private fun addSectorIncludeStockList(body: ResultIncludeSectorThemeList?) {
        stockList.clear()
        if(!body.isNullOrEmpty()) {
            for(item in body){
                if(item.rate >= 0.0) stockList.add(StockInfo(item.ticker, item.company_name, "+${item.rate}%", item.end_price))
                else stockList.add(StockInfo(item.ticker, item.company_name, "${item.rate}%", item.end_price))
            }

        } else{

        }
        stockListAdapter.notifyDataSetChanged()
    }

    private fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        Log.d("argument", bundle.toString())
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            R.id.back_btn -> parentFragmentManager.popBackStack()
            R.id.stock_open_btn -> {
                stockNum = 10
                getSectorIncludeStockList(sectorName, stockNum)
            }
            R.id.stock_close_btn -> {
                stockNum = 3
                getSectorIncludeStockList(sectorName, stockNum)
            }
            R.id.news_open_btn -> {
                newsNum = 10
                getNewsLikeListByKeyword(sectorName, newsNum)
            }
            R.id.news_close_btn -> {
                newsNum = 3
                getNewsLikeListByKeyword(sectorName, newsNum)
            }
        }
    }
}