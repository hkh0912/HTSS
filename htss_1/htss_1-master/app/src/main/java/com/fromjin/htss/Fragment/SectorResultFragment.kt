package com.fromjin.htss.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fromjin.htss.Model.StockInfo
import com.fromjin.htss.R
import com.fromjin.htss.RecyclerView.StockListAdapter
import com.fromjin.htss.databinding.FragmentSectorResultBinding

class SectorResultFragment : Fragment() {

    private lateinit var view: FragmentSectorResultBinding

    // 임시데이터
    private val stockList = arrayListOf<StockInfo>(
        StockInfo("1","주식1","+10%",27000),
        StockInfo("1","업종2","-10%",92000),
        StockInfo("1","업종3","+5.8%",5820)
    )

    private val stockListAdapter = StockListAdapter(stockList)

    private var sectorName = ""
    private var sectorValue = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentSectorResultBinding.inflate(inflater, container, false)


        sectorName = arguments?.getString("category_name").toString()
        sectorValue = arguments?.getString("category_value").toString()

        view.sectorName.text = sectorName
        view.sectorName2.text = sectorName

        if (sectorValue.substring(0, 1) == "+") {
            view.sectorValuePlus.text = sectorValue
            view.sectorValuePlus.visibility = View.VISIBLE
            view.sectorValueMinus.visibility = View.GONE
        } else {
            view.sectorValueMinus.text = sectorValue
            view.sectorValueMinus.visibility = View.VISIBLE
            view.sectorValuePlus.visibility = View.GONE
        }


        view.stockRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = stockListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    view.stockRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )

        }

        stockListAdapter.setItemClickListener(object : StockListAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                Log.d("주식", stockList[position].name)
                val bundle = Bundle()
                bundle.apply {
                    putString("stock_name", stockList[position].name)
                    putString("stock_value", stockList[position].value)
                    putInt("stock_current", stockList[position].current)
                }
                replaceFragment(StockFragment(), bundle)
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
}