package com.fromjin.htss.Fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.htss.Model.NewsInfo
import com.fromjin.htss.Model.RankInfo
import com.fromjin.htss.Model.RelatedStockInfo
import com.fromjin.htss.R
import com.fromjin.htss.RecyclerView.NewsListAdapter
import com.fromjin.htss.RecyclerView.RankListAdapter
import com.fromjin.htss.RecyclerView.RelatedStockListAdapter
import com.fromjin.htss.Util.ResultModel.ResultNounMatchingList
import com.fromjin.htss.Util.ResultModel.ResultNewsList
import com.fromjin.htss.Util.ResultModel.ResultSectorThemeList
import com.fromjin.htss.Util.RetrofitClient
import com.fromjin.htss.databinding.FragmentKeywordResultBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class KeywordResultFragment : Fragment() {

    private lateinit var view: FragmentKeywordResultBinding
    private val retrofit = RetrofitClient.create()

    var keyword = ""
    var type = 0 // 0:키워드, 3:업종, 4:테마
    var nounNum = 3

    private val sectorLikeList = mutableListOf<RankInfo>()
    private val sectorListAdapter = RankListAdapter(sectorLikeList)
    var sectorNum = 3

    private val themeLikeList = mutableListOf<RankInfo>()
    private val themeListAdapter = RankListAdapter(themeLikeList)
    var themeNum = 3

    private val stockRankList = mutableListOf<RelatedStockInfo>()
    private val stockListAdapter = RelatedStockListAdapter(stockRankList)
    var stockNum = 3

    private val newsLikeList = mutableListOf<NewsInfo>()
    private val newsListAdapter = NewsListAdapter(newsLikeList)
    var newsNum = 3


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentKeywordResultBinding.inflate(inflater, container, false)

        keyword = arguments?.getString("keyword").toString()
        type = arguments?.getInt("type")!!

        when(type){
            0 -> { //키워드
                getNounMatchingStockListByKeyword(keyword, nounNum)
                getSectorLikeListByKeyword(keyword, sectorNum)
                getThemeLikeListByKeyword(keyword, themeNum)
                getNewsLikeListByKeyword(keyword, newsNum)
            }
            3 -> { // 업종
                getNounMatchingStockListByKeyword(keyword, nounNum)
                getSectorLikeListByKeyword(keyword, sectorNum)
//                getThemeLikeListByKeyword(keyword, themeNum)
                getNewsLikeListByKeyword(keyword, newsNum)
                view.themeLinear.visibility = View.GONE

            }
            4 -> { // 테마
                getNounMatchingStockListByKeyword(keyword, nounNum)
//                getSectorLikeListByKeyword(keyword, sectorNum)
                getThemeLikeListByKeyword(keyword, themeNum)
                getNewsLikeListByKeyword(keyword, newsNum)
                view.sectorLinear.visibility = View.GONE
            }
        }

        getNounMatchingStockListByKeyword(keyword, nounNum)
        getSectorLikeListByKeyword(keyword, sectorNum)
        getThemeLikeListByKeyword(keyword, themeNum)
        getNewsLikeListByKeyword(keyword, newsNum)


        view.keywordName.text = keyword
        view.keywordName2.text = keyword
        view.keywordName3.text = keyword

        view.sectorRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = sectorListAdapter

        }
        view.themeRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = themeListAdapter
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

        sectorListAdapter.setItemClickListener(object : RankListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    putString("name", sectorLikeList[position].name)
                    putString("rate", sectorLikeList[position].rate)
                }
                replaceFragment(SectorResultFragment(), bundle)
            }
        })

        themeListAdapter.setItemClickListener(object: RankListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    putString("name", themeLikeList[position].name)
                    putString("rate", themeLikeList[position].rate)
                }
                replaceFragment(ThemeResultFragment(), bundle)
            }

        })

        stockListAdapter.setItemClickListener(object : RelatedStockListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    putString("ticker", stockRankList[position].ticker)
//                    putString("name", stockRankList[position].name)
//                    putString("rate", stockRankList[position].rate)
//                    putInt("end_price", stockRankList[position].endPrice)
                }
                replaceFragment(StockFragment(), bundle)
            }

        })

        newsListAdapter.setItemClickListener(object : NewsListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.putString("ticker", newsLikeList[position].ticker)
                replaceFragment(StockFragment(), bundle)
            }
        })

        newsListAdapter.setLinkClickListener(object: NewsListAdapter.OnLinkClickListener{
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(newsLikeList[position].link)))
            }
        })

        //뒤로가기
        view.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }


        return view.root
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

    fun getNounMatchingStockListByKeyword(keyword: String, num: Int) {
        retrofit.getNounMatchingList(keyword, num)
            .enqueue(object : Callback<ResultNounMatchingList> {
                override fun onResponse(
                    call: Call<ResultNounMatchingList>,
                    response: Response<ResultNounMatchingList>
                ) {
                    if (response.code() == 200) {
                        addResultNounMatchingStockList(response.body())
                        Log.d("API호출", response.raw().toString())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "오류가 발생했습니다.\n다시 시도해주세요",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResultNounMatchingList>, t: Throwable) {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("API호출2", t.message.toString())
                }

            })
    }

    fun getSectorLikeListByKeyword(keyword: String, num: Int) {
        retrofit.getSectorLikeList(keyword, num).enqueue(object : Callback<ResultSectorThemeList> {
            override fun onResponse(
                call: Call<ResultSectorThemeList>,
                response: Response<ResultSectorThemeList>
            ) {
                if (response.code() == 200) {
                    addResultSectorThemeLikeByKeyword("sector", response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResultSectorThemeList>, t: Throwable) {
                Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                    .show()
                Log.d("API호출2", t.message.toString())
            }

        })
    }

    fun getThemeLikeListByKeyword(keyword: String, num: Int) {
        retrofit.getThemeLikeList(keyword, num).enqueue(object : Callback<ResultSectorThemeList> {
            override fun onResponse(
                call: Call<ResultSectorThemeList>,
                response: Response<ResultSectorThemeList>
            ) {
                if (response.code() == 200) {
                    addResultSectorThemeLikeByKeyword("theme", response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResultSectorThemeList>, t: Throwable) {
                Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                    .show()
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
                    Log.d("검색결과", response.raw().toString())

                } else {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResultNewsList>, t: Throwable) {
                Log.d("검색결과", t.message.toString())
            }

        })
    }

    private fun addResultNewsLikeList(body: ResultNewsList?) {
        Log.d("검색결과", "검색결과0")
        newsLikeList.clear()
        if (body.isNullOrEmpty()) {
            view.newsOpenBtn.text = "관련 뉴스가 없습니다."
            Log.d("검색결과", "검색결과1")
        } else {
            Log.d("검색결과", "검색결과2")
            for (item in body) {
                newsLikeList.add(
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
        Log.d("검색결과", "검색결과3")
    }

    fun addResultSectorThemeLikeByKeyword(name: String, body: ResultSectorThemeList?) {
        when (name) {
            "sector" -> {
                sectorLikeList.clear()
                if (body.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "검색결과가 없습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    var i = 1
                    for (item in body) {
                        Log.d("API결과", item.toString())
                        if (item.rate >= 0.0) {
                            sectorLikeList.add(
                                RankInfo(
                                    item.keyword,
                                    "+" + item.rate.toString() + "%"
                                )
                            )
                        } else {
                            sectorLikeList.add(RankInfo(item.keyword, item.rate.toString() + "%"))
                        }

                    }
                }
                Log.d("API결과리스트", sectorLikeList.toString())
                sectorListAdapter.notifyDataSetChanged()
            }
            "theme" -> {
                themeLikeList.clear()
                if (body.isNullOrEmpty()) {
                    // 결과 0건 처리 필요
                } else {
                    var i = 1
                    for (item in body) {
                        Log.d("API결과", item.toString())
                        if (item.rate >= 0.0) {
                            themeLikeList.add(
                                RankInfo(
                                    item.keyword,
                                    "+" + item.rate.toString() + "%"
                                )
                            )
                        } else {
                            themeLikeList.add(RankInfo(item.keyword, item.rate.toString() + "%"))
                        }
                    }
                }
                Log.d("API결과리스트", themeLikeList.toString())
                themeListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun addResultNounMatchingStockList(body: ResultNounMatchingList?) {
        stockRankList.clear()
        if (body != null) {
            for (item in body) {
                Log.d("API결과", item.toString())
                if (item.rate >= 0.0) {
                    stockRankList.add(
                        RelatedStockInfo(
                            item.ticker,
                            item.company_name,
                            "+" + item.rate.toString() + "%",
                            item.count,
                            item.end_price
                        )
                    )

                } else {
                    stockRankList.add(
                        RelatedStockInfo(
                            item.ticker,
                            item.company_name,
                            item.rate.toString() + "%",
                            item.count,
                            item.end_price
                        )
                    )

                }
            }
        }
        Log.d("API결과리스트", stockRankList.toString())
        stockListAdapter.notifyDataSetChanged()
    }
}