package com.fromjin.htss.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.fromjin.htss.R
import com.fromjin.htss.databinding.FragmentStockBinding

class StockFragment : Fragment() {

    private lateinit var view: FragmentStockBinding

//    val unitList = listOf("1일", "1주", "3달", "1년", "5년")

    var stockName = ""
    var stockValue =""
    var stockCurrent = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentStockBinding.inflate(inflater, container, false)

        stockName = arguments?.getString("stock_name").toString()
        stockValue = arguments?.getString("stock_value").toString()
        stockCurrent = arguments?.getInt("stock_current").toString()

        view.stockName.text = stockName
        view.stockCurrent.text = stockCurrent
        if(stockValue.substring(0,1) == "+"){
            view.stockValue.setTextColor(ContextCompat.getColor(requireContext(),R.color.navy))
            view.stockValueWon.setTextColor(ContextCompat.getColor(requireContext(),R.color.navy))
        } else {
            view.stockValue.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))
            view.stockValueWon.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange))

        }
        view.stockValue.text = stockCurrent


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


}