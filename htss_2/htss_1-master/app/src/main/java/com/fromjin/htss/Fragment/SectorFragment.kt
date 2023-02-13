package com.fromjin.htss.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.htss.Model.RankInfo
import com.fromjin.htss.R
import com.fromjin.htss.RecyclerView.SectorThemeListAdapter
import com.fromjin.htss.Util.ResultModel.ResultSectorThemeList
import com.fromjin.htss.Util.RetrofitClient
import com.fromjin.htss.databinding.FragmentSectorBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SectorFragment : Fragment() {

    private lateinit var view: FragmentSectorBinding
    private val retrofit = RetrofitClient.create()
    private var sectorNum = 20

    private val sectorList = mutableListOf<RankInfo>()
    private val sectorListAdapter = SectorThemeListAdapter(sectorList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentSectorBinding.inflate(inflater, container, false)

        view.sectorRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = sectorListAdapter
        }

        getSectorList(sectorNum)

        sectorListAdapter.setItemClickListener(object : SectorThemeListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    putString("sector_name", sectorList[position].name)
                    putString("sector_value", sectorList[position].value)
                }
                replaceFragment(SectorResultFragment(), bundle)
            }
        })


        return view.root
    }

    fun getSectorList(num: Int) {
        retrofit.getSectorHighList(num).enqueue(object: Callback<ResultSectorThemeList> {
            override fun onResponse(
                call: Call<ResultSectorThemeList>,
                response: Response<ResultSectorThemeList>
            ) {
                if(response.code()==200) {
                    addResultSectorList(response.body())
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

    private fun addResultSectorList(body: ResultSectorThemeList?) {
        sectorList.clear()
        if(body != null) {
            for(item in body) {
                Log.d("API결과",item.toString())
                if(item.rate >= 0.0){
                    sectorList.add(RankInfo(item.keyword, "+"+item.rate.toString()+"%"))
                } else {
                    sectorList.add(RankInfo(item.keyword, item.rate.toString()+"%"))
                }
            }
        }
        Log.d("API결과리스트", sectorList.toString())
        sectorListAdapter.notifyDataSetChanged()
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
}