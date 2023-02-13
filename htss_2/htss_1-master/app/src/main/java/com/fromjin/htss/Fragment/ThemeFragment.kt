package com.fromjin.htss.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.htss.Model.RankInfo
import com.fromjin.htss.RecyclerView.SectorThemeListAdapter
import com.fromjin.htss.Util.ResultModel.ResultSectorThemeList
import com.fromjin.htss.Util.RetrofitClient
import com.fromjin.htss.databinding.FragmentThemeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ThemeFragment : Fragment() {

    private lateinit var view: FragmentThemeBinding
    private val retrofit = RetrofitClient.create()
    private var num = 20

    private val themeList = mutableListOf<RankInfo>()
    private val themeListAdapter = SectorThemeListAdapter(themeList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = FragmentThemeBinding.inflate(inflater, container, false)

        getThemeList(num)

        view.themeRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = themeListAdapter
        }




        return view.root
    }


    fun getThemeList(num: Int) {
        retrofit.getThemeHighList(num).enqueue(object : Callback<ResultSectorThemeList> {
            override fun onResponse(
                call: Call<ResultSectorThemeList>,
                response: Response<ResultSectorThemeList>
            ) {
                if (response.code() == 200) {
                    addResultThemeList(response.body())
                    Log.d("API호출", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "오류가 발생했습니다.\n다시 시도해주세요", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<ResultSectorThemeList>, t: Throwable) {
                Log.d("API호출2", t.message.toString())
            }

        })
    }

    private fun addResultThemeList(body: ResultSectorThemeList?) {
        themeList.clear()
        if (body != null) {
            for (item in body) {
                Log.d("API결과", item.toString())
                if (item.rate >= 0.0) {
                    themeList.add(RankInfo(item.keyword, "+" + item.rate.toString() + "%"))
                } else {
                    themeList.add(RankInfo(item.keyword, item.rate.toString() + "%"))
                }
            }
        }
        Log.d("API결과리스트", themeList.toString())
        themeListAdapter.notifyDataSetChanged()
    }

}