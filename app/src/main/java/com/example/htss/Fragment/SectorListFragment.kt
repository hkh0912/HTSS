package com.example.htss.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.htss.Adapter.CategoryListAdapter
import com.example.htss.Model.CategorylistModel
import com.example.htss.Model.MainModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.SectorThemeList
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentSectorListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SectorListFragment : Fragment() {
    private lateinit var view: FragmentSectorListBinding
    private val retrofit = RetrofitClient.create()
    private val categoryList = arrayListOf<CategorylistModel>()
    private val categoryListAdapter = CategoryListAdapter(categoryList)
    private var num = 20
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentSectorListBinding.inflate(inflater, container, false)

        getHighSectorList(num)

        view.recycle4.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categoryListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    view.recycle4.context,
                    LinearLayoutManager(context).orientation
                )
            )



            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition() // ????????? ????????? ????????? ???????????? position
                    val itemTotalCount =
                        recyclerView.adapter!!.itemCount - 1 // ???????????? ????????? ???????????? ??? ?????? -1

                    //???????????? ?????? 1
                    if (!view.recycle4.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount && itemTotalCount + 1 >= num) {
                        num += 20
                        getHighSectorList(num)
                    }
                }
            })
        }

//        view.back.setOnClickListener {
//            parentFragmentManager.popBackStack()
//        }

        categoryListAdapter.setItemClickListener(object : CategoryListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    putString("category_name", categoryList[position].categoryName)
                    putString("category_percent", categoryList[position].percent)
                }
                replaceFragment(CategoryDetailFragment(), bundle)
            }
        })

        return view.root
    }

    fun getHighSectorList(num: Int){
        retrofit.getHighSectorList(num).enqueue(object: Callback<SectorThemeList> {
            override fun onResponse(
                call: Call<SectorThemeList>,
                response: Response<SectorThemeList>
            ) {
                if(response.code()==200) {
                    addResultSectorThemeHighList("sector",response.body())
                    Log.d("API??????", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"????????? ??????????????????.\n?????? ??????????????????", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SectorThemeList>, t: Throwable) {
                Log.d("API??????2", t.message.toString())
            }

        })
    }
    private fun addResultSectorThemeHighList(name:String, body: SectorThemeList?) {
        when(name){
            "sector" -> {
                categoryList.clear()
                if(body != null) {
                    for(item in body) {
                        Log.d("API??????",item.toString())
                        if(item.rate >= 0.0){
                            categoryList.add(CategorylistModel(item.keyword, "+"+item.rate.toString()+"%"))
                        } else {
                            categoryList.add(CategorylistModel(item.keyword, item.rate.toString()+"%"))
                        }
                    }
                }
                Log.d("API???????????????", categoryList.toString())
                categoryListAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        Log.d("argument", bundle.toString())
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }

}

