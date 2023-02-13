package com.fromjin.htss.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fromjin.htss.Model.RankInfo
import com.fromjin.htss.R
import com.fromjin.htss.RecyclerView.SectorThemeListAdapter
import com.fromjin.htss.Util.ResultModel.ResultSectorThemeList
import com.fromjin.htss.Util.RetrofitClient
import com.fromjin.htss.databinding.FragmentSectorListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SectorListFragment : Fragment() {

    private lateinit var view: FragmentSectorListBinding
    private val retrofit = RetrofitClient.create()
    private var num = 20

    private val sectorList = mutableListOf<RankInfo>()
    private val sectorListAdapter = SectorThemeListAdapter(sectorList)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentSectorListBinding.inflate(inflater, container, false)

        view.sectorRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = sectorListAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // 화면에 보이는 마지막 아이템의 position
                    val itemTotalCount =
                        recyclerView.adapter!!.itemCount - 1 // 어댑터에 등록된 아이템의 총 개수 -1

                    //최하단에 도달 1
                    if (!view.sectorRecyclerview.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && itemTotalCount + 1 >= num) {
                        num *= 2
                        getSectorList(num)
                    }
                }
            })
        }

        getSectorList(num)

        sectorListAdapter.setItemClickListener(object : SectorThemeListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    putString("name", sectorList[position].name)
                    putString("rate", sectorList[position].rate)
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