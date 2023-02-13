package com.fromjin.htss.Fragment

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
import com.fromjin.htss.Model.RankInfo
import com.fromjin.htss.R
import com.fromjin.htss.RecyclerView.NewsListAdapter
import com.fromjin.htss.RecyclerView.RankListAdapter
import com.fromjin.htss.Util.ResultModel.ResultSectorThemeList
import com.fromjin.htss.Util.RetrofitClient
import com.fromjin.htss.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var view: FragmentHomeBinding
    private val retrofit = RetrofitClient.create()

    // 업종
    private val sectorRankList = mutableListOf<RankInfo>()
    private val sectorListAdapter = RankListAdapter(sectorRankList)

    private val themeRankList = mutableListOf<RankInfo>()
    private val themeListAdapter = RankListAdapter(themeRankList)

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

        view = FragmentHomeBinding.inflate(inflater, container, false)

        getSectorHighList(3)
        getThemeHighList(3)

        view.sectorRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = sectorListAdapter
        }

        view.themeRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = themeListAdapter
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
                Log.d("업종상위", position.toString())
                val bundle = Bundle()
                bundle.apply {
                    putString("sector_name", sectorRankList[position].name)
                    putString("sector_value", sectorRankList[position].value)
                }
                replaceFragment(SectorResultFragment(), bundle)

            }

        })

        view.sectorBtn.setOnClickListener(this)
        view.themeBtn.setOnClickListener(this)
        view.searchBtn.setOnClickListener(this)

        return view.root
    }

    fun getSectorHighList(num: Int){
        retrofit.getSectorHighList(num).enqueue(object: Callback<ResultSectorThemeList>{
            override fun onResponse(
                call: Call<ResultSectorThemeList>,
                response: Response<ResultSectorThemeList>
            ) {
                if(response.code()==200) {
                    addResultSectorThemeHighList("sector",response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultSectorThemeList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }

        })
    }

    fun getThemeHighList(num: Int){
        retrofit.getThemeHighList(num).enqueue(object: Callback<ResultSectorThemeList>{
            override fun onResponse(
                call: Call<ResultSectorThemeList>,
                response: Response<ResultSectorThemeList>
            ) {
                if(response.code()==200) {
                    addResultSectorThemeHighList("theme",response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResultSectorThemeList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }

        })
    }

    private fun addResultSectorThemeHighList(name:String, body: ResultSectorThemeList?) {
        when(name){
            "sector" -> {
                sectorRankList.clear()
                if(body != null) {
                    for(item in body) {
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
                if(body != null) {
                    for(item in body) {
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
            R.id.sector_btn -> {
                val bundle = Bundle()
                bundle.putString("focus","sector")
                replaceFragment(AllListFragment(), bundle)
            }
            R.id.theme_btn -> {
                val bundle = Bundle()
                bundle.putString("focus","theme")
                replaceFragment(AllListFragment(), bundle)
            }
            R.id.search_btn -> {
                val bundle = Bundle()
                bundle.putString("keyword",view.searchEdit.text.toString())
                view.searchEdit.text = null
                replaceFragment(KeywordResultFragment(), bundle)
            }
        }
    }

}