package com.fromjin.htss.Fragment

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

    // 임시데이터
    private val sectorRankList = mutableListOf<RankInfo>()
    private val sectorListAdapter = RankListAdapter(sectorRankList)
    var sectorNum = 3

    // 임시데이터
    private val themeRankList = mutableListOf<RankInfo>()
    private val themeListAdapter = RankListAdapter(themeRankList)
    var themeNum = 3

    private val stockRankList = mutableListOf<RelatedStockInfo>()
    private val stockListAdapter = RelatedStockListAdapter(stockRankList)
    var stockNum = 3

    private val newsRankList = arrayListOf<NewsInfo>(
        NewsInfo("미 인플레 둔화 속 비트코인 1만9천달러선 회복(종합)", "+10%"),
        NewsInfo("전 종가보다 6%↑…이더리움도 3% 올라 1천400달러선", "-10%"),
        NewsInfo("전 종가보다 6%↑…이더리움도 3% 올라 1천400달러선", "+5.8%")
    )
    private val newsListAdapter = NewsListAdapter(newsRankList)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentKeywordResultBinding.inflate(inflater, container, false)

        keyword = arguments?.getString("keyword").toString()

        getNounMatchingStockListByKeyword(keyword)

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
                    putString("sector_name", sectorRankList[position].name)
                    putString("sector_value", sectorRankList[position].value)
                }
                replaceFragment(SectorResultFragment(), bundle)
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

    fun getNounMatchingStockListByKeyword(keyword: String) {
        retrofit.getNounMatchingList(keyword,stockNum)
            .enqueue(object: Callback<ResultNounMatchingList>{
                override fun onResponse(
                    call: Call<ResultNounMatchingList>,
                    response: Response<ResultNounMatchingList>
                ) {
                    if(response.code()==200) {
                        addResultNounMatchingStockList(response.body())
                        Log.d("API호출", response.raw().toString())
                    } else {
                        Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                    }
//                    addResultNounMatchingStockList(response.body())
//                    Log.d("API호출", response.raw().toString())
                }

                override fun onFailure(call: Call<ResultNounMatchingList>, t: Throwable) {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                    Log.d("API호출2", t.message.toString())
                }

            })
    }

    fun getSectorLikeListByKeyword(keyword: String){
        retrofit.getSectorLikeList(keyword).enqueue(object : Callback<ResultSectorThemeList> {
            override fun onResponse(
                call: Call<ResultSectorThemeList>,
                response: Response<ResultSectorThemeList>
            ) {
                if(response.code()==200) {
                    addResultSectorThemeLikeByKeyword("sector", response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultSectorThemeList>, t: Throwable) {
                Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                Log.d("API호출2", t.message.toString())
            }

        })
    }

    fun getThemeLikeListByKeyword(keyword: String){
        retrofit.getThemaLikeList(keyword).enqueue(object : Callback<ResultSectorThemeList> {
            override fun onResponse(
                call: Call<ResultSectorThemeList>,
                response: Response<ResultSectorThemeList>
            ) {
                if(response.code()==200) {
                    addResultSectorThemeLikeByKeyword("theme", response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultSectorThemeList>, t: Throwable) {
                Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                Log.d("API호출2", t.message.toString())
            }

        })
    }

    fun addResultSectorThemeLikeByKeyword(name: String, body: ResultSectorThemeList?) {
        when(name) {
            "sector" -> {
                sectorRankList.clear()
                if(body.isNullOrEmpty()) {
                    // 결과 0건 처리 필요
                } else {
                    var i = 1
                    for(item in body) {
                        if(i == 4) break

                        Log.d("API결과",item.toString())
                        if(item.rate >= 0.0){
                            sectorRankList.add(RankInfo(item.keyword, "+"+item.rate.toString()+"%"))
                        } else {
                            sectorRankList.add(RankInfo(item.keyword, item.rate.toString()+"%"))
                        }

                    }
                }
                Log.d("API결과리스트", sectorRankList.toString())
                sectorListAdapter.notifyDataSetChanged()
            }
            "theme" -> {
                themeRankList.clear()
                if(body.isNullOrEmpty()){
                    // 결과 0건 처리 필요
                } else {
                    var i = 1
                    for(item in body) {

                        if(i == 4) break

                        Log.d("API결과",item.toString())
                        if(item.rate >= 0.0){
                            themeRankList.add(RankInfo(item.keyword, "+"+item.rate.toString()+"%"))
                        } else {
                            themeRankList.add(RankInfo(item.keyword, item.rate.toString()+"%"))
                        }
                    }
                }
                Log.d("API결과리스트", themeRankList.toString())
                themeListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun addResultNounMatchingStockList(body: ResultNounMatchingList?) {
        stockRankList.clear()
        if(body != null) {
            for(item in body) {
                Log.d("API결과",item.toString())
                stockRankList.add(RelatedStockInfo(item.ticker, item.company_name,"+5%", item.count))
            }
        }
        Log.d("API결과리스트", stockRankList.toString())
        stockListAdapter.notifyDataSetChanged()
    }
}