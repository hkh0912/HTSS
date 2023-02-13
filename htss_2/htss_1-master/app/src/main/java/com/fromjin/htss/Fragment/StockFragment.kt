package com.fromjin.htss.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.htss.Model.NewsInfo
import com.fromjin.htss.R
import com.fromjin.htss.RecyclerView.NewsListAdapter
import com.fromjin.htss.Util.ResultModel.ResultNewsList
import com.fromjin.htss.Util.ResultModel.ResultStockInfo
import com.fromjin.htss.Util.RetrofitClient
import com.fromjin.htss.databinding.FragmentStockBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockFragment : Fragment() {

    private lateinit var view: FragmentStockBinding
    private val retrofit = RetrofitClient.create()

//    val unitList = listOf("1일", "1주", "3달", "1년", "5년")

    var name = ""
    var rate =""
    var endPrice = 0
    var ticker = ""

    private val newsRankList = arrayListOf<NewsInfo>()
    private val newsListAdapter = NewsListAdapter(newsRankList)
    var newsNum = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentStockBinding.inflate(inflater, container, false)

        ticker = arguments?.getString("ticker").toString()
        name = arguments?.getString("name").toString()
        rate = arguments?.getString("rate").toString()
        endPrice = arguments?.getInt("end_price")!!.toInt()


        view.stockName.text = name
        view.stockRate.text = rate
        view.stockEndPrice.text = endPrice.toString()+"원"

        if(rate.substring(0,1) == "+"){
            view.stockRate.setTextColor(ContextCompat.getColor(requireContext(),R.color.navy))
        } else {
            view.stockRate.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))
        }
        getStockInfoByTicker(ticker)

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
        newsListAdapter.setItemClickListener(object : NewsListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(newsRankList[position].link)))
            }

        })

        getNewsLikeListByKeyword(name, newsNum)



//        view.unitSpinner.apply {
//            adapter = ArrayAdapter(requireContext(), R.layout.news_item_view, unitList)
//            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected( parent: AdapterView<*>?, view: View?, position: Int, id: Long
//                ) {
//                    // 조회 단위 변경
//                }
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//                    TODO("Not yet implemented")
//                }
//            } }

        //뒤로가기
        view.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        return view.root
    }

    fun getStockInfoByTicker(ticker: String){
        retrofit.getStockInfoByTicker(ticker).enqueue(object: Callback<ResultStockInfo>{
            override fun onResponse(
                call: Call<ResultStockInfo>,
                response: Response<ResultStockInfo>
            ) {
                if(response.code()==200){
                    if(response.body()?.isNullOrEmpty() == false) {
                        view.stockInfo.text = response.body()!![0].company_info
                    } else {
                        view.stockInfo.text = "개요정보없음"
                    }
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultStockInfo>, t: Throwable) {
                Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
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
                if (response.code() == 200) {
                    addResultNewsLikeList(response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResultNewsList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }

        })
    }

    private fun addResultNewsLikeList(body: ResultNewsList?) {
        newsRankList.clear()
        if (body.isNullOrEmpty()) {

        } else {
            for (item in body) {
                newsRankList.add(
                    NewsInfo(
                        item.ticker,
                        item.provider,
                        item.date,
                        item.title,
                        item.rink
                    )
                )
            }
            newsListAdapter.notifyDataSetChanged()
        }
    }


}