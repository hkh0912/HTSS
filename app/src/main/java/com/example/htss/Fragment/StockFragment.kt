package com.example.htss.Fragment

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Model.NewsModel
import com.example.htss.Model.StockChartModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.*
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentStockBinding
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_home.view.*
import kotlinx.android.synthetic.main.fragment_stock.*
import kotlinx.android.synthetic.main.fragment_stock.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class StockFragment : Fragment(), View.OnClickListener {

    var selectedPosition = 0
    var selectedPosition2 = 0
    var newsNum = 3
    var first = "company_info"
///////////
    private lateinit var view: FragmentStockBinding
    private val retrofit = RetrofitClient.create()


    private var StockNewsList = arrayListOf<NewsModel>()

    private var StockNewsListAdapter = MainNewsAdapter(StockNewsList)

    private var StockTicker = ""
    private var StockName = ""
    val dec = DecimalFormat("#,###.##")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?



    ): View? {

        view = FragmentStockBinding.inflate(inflater, container, false)

        view.companyInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        view.companyInvestInfo.setTextColor(ContextCompat.getColor(requireContext(), R.color.hmmm))
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        val items = resources.getStringArray(R.array.search_array)
        val myAapter = object : ArrayAdapter<String>(requireContext(), R.layout.item_spinner) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                if (position == count) {
                    //????????? ???????????? textView ??? ?????? ????????? ???????????????.
                    (v.findViewById<View>(R.id.tvItemSpinner) as TextView).text = ""
                    //???????????? ????????? ?????? ????????? hint??? ????????? ?????????.
                    (v.findViewById<View>(R.id.tvItemSpinner) as TextView).hint = getItem(count)
                }
                return v
            }
            override fun getCount(): Int {
                //????????? ???????????? ?????????????????? ???????????? ????????? getCount??? 1??? ????????????.
                return super.getCount() - 1
            }
        }

        myAapter.addAll(items.toMutableList())
        myAapter.add("????????????")
        view.searchSpinner.adapter = myAapter
        view.searchSpinner.setSelection(myAapter.count)
        view.searchSpinner.dropDownVerticalOffset = dipToPixels(35f).toInt()

//????????? ????????? ????????? ??????
        view.searchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedPosition = position
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }
////////////////////////////// ?????? ?????????
        val items2 = resources.getStringArray(R.array.chart_array)
        val myAdapter2 = object : ArrayAdapter<String>(requireContext(), R.layout.chart_spinner) {
            override fun getView(position2: Int, convertView: View?, parent: ViewGroup): View {
                val v2 = super.getView(position2, convertView, parent)
//                if (position2 == count) {
//                    //????????? ???????????? textView ??? ?????? ????????? ???????????????.
//                    (v2.findViewById<View>(R.id.chartItemSpinner) as TextView).text = ""
//                    //???????????? ????????? ?????? ????????? hint??? ????????? ?????????.
//                    (v2.findViewById<View>(R.id.chartItemSpinner) as TextView).hint = getItem(count)
//                }
                return v2
            }

//            override fun getCount(): Int {
//                //????????? ???????????? ?????????????????? ???????????? ????????? getCount??? 1??? ????????????.
//                return super.getCount() - 1
//            }
        }
        myAdapter2.addAll(items2.toMutableList())
        view.chartSpinner.adapter = myAdapter2
        view.chartSpinner.setSelection(0)
        view.chartSpinner.dropDownVerticalOffset = dipToPixels(30f).toInt()
        //????????? ????????? ????????? ??????
        view.chartSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position2: Int,
                id: Long
            ) {
                selectedPosition2 = position2
                Log.d("position", selectedPosition2.toString())
                when (selectedPosition2) {
                    0 -> {
                        getStockPrice(StockTicker, 90)
                    }
                    1 -> {
                        getStockPrice(StockTicker, 180)
                    }
                    2 -> {
                        getStockPrice(StockTicker, 365)
                    }
                    3 -> {
                        getStockPrice(StockTicker, 730)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        StockTicker = arguments?.getString("stock_ticker").toString()
        StockName = arguments?.getString("stock_name").toString()


        val bundle = Bundle()
        bundle.putString("stock_ticker", StockTicker)
        when(first){
            "company_info" -> replaceFragment2(Company_info_Fragment1(),bundle)
        }

//
        view.newsRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = StockNewsListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    view.newsRecyclerview.context,
                    LinearLayoutManager(context).orientation
                )
            )
        }

        view.back.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        StockNewsListAdapter.setItemClickListener(object : MainNewsAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(StockNewsList[position].rink)))
            }
        })

        StockNewsListAdapter.setLinkClickListener(object : MainNewsAdapter.OnLinkClickListener{
            override fun onClick(v: View, position: Int) {
                getStockNameByTicker(StockNewsList[position].ticker)
            }
        })

        view.newsCloseBtn.setOnClickListener(this)
        view.newsOpenBtn.setOnClickListener(this)
        view.stockSearchBtn.setOnClickListener(this)
        view.companyInfo.setOnClickListener(this)
        view.companyInvestInfo.setOnClickListener(this)
//        view.thirtyDays.setOnClickListener(this)
//        view.ninetyDays.setOnClickListener(this)
//        view.oneYear.setOnClickListener(this)
//        view.hundredDays.setOnClickListener(this)

        setListenerToEditText()
        getStockNowPrice(StockTicker)
        getCompanyInfo(StockTicker)
//        getStockPrice(StockTicker,100)

        return view.root
    }

    fun getCompanyInfo(ticker: String){
        retrofit.getCompanyInfo(ticker).enqueue(object : Callback<CompanyInfoListItem> {
            override fun onResponse(
                call: Call<CompanyInfoListItem>,
                response: Response<CompanyInfoListItem>
            ) {
                if(response.code()==200) {
                    if(response.body() != null )
                        addcompanyInfo(response.body()!!)
                    Log.d("stock/info/API??????!!!!!!", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"????????? ??????????????????.\n?????? ??????????????????", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<CompanyInfoListItem>, t: Throwable) {
                Log.d("API??????3333", t.message.toString())
            }
        })
    }
    private fun addcompanyInfo(body: CompanyInfoListItem) {
        view.stockName.text = body.company_name
        view.stockName2.text = body.company_name
        getSectorThemeKeywordIncludeNews(body.company_name, newsNum)
    }



    fun getStockNowPrice(ticker: String){
        retrofit.getStockNowPrice(ticker).enqueue(object : Callback<StockNowPriceListItem> {
            override fun onResponse(
                call: Call<StockNowPriceListItem>,
                response: Response<StockNowPriceListItem>
            ) {
                if(response.code()==200) {
                    if(response.body()!=null)
                        addStockNowPrice(response.body()!!)
                    Log.d("stock/now-price/API??????", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"????????? ??????????????????.\n?????? ??????????????????", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StockNowPriceListItem>, t: Throwable) {
                Log.d("API??????2222222", t.message.toString())
            }
        })
    }
    private fun addStockNowPrice(body: StockNowPriceListItem) {
        view.ticker.text = StockTicker
        view.stockCurrent.text = dec.format(body.end_price).toString()
        if(body.rate >= 0.0){
            view.stockPercent.apply{
                setTextColor(ContextCompat.getColor(requireContext(),R.color.red))
                text = "+"+body.rate.toString()+"%"
            }
        } else {
            view.stockPercent.apply{
                setTextColor(ContextCompat.getColor(requireContext(),R.color.blue))
                text = body.rate.toString()+"%"
            }
        }
    }


    fun getTickerByStockName(name: String){
        retrofit.getTickerByStockName(name).enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.code() == 200){
                    if(!response.body().isNullOrBlank()){
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", response.body())
                        bundle.putString("stock_name", name)
                        replaceFragment(StockFragment(), bundle)
                    } else {
                        Toast.makeText(requireContext(),"???????????? ????????? ????????????.", Toast.LENGTH_SHORT).show()
                    }
                } else Toast.makeText(requireContext(),"????????? ?????????????????????.\n?????? ??????????????????.", Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(),"????????? ?????????????????????.\n?????? ??????????????????.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun getStockNameByTicker(ticker: String){
        retrofit.getStockNameByTicker(ticker).enqueue(object: Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if(response.code() == 200){
                    if(!response.body().isNullOrBlank()){
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", ticker)
                        bundle.putString("stock_name", response.body())
                        Log.d("hmmmddd",response.body().toString())
                        replaceFragment(StockFragment(), bundle)
                    } else {
                        Toast.makeText(requireContext(),"???????????? ????????? ????????????.", Toast.LENGTH_SHORT).show()
                    }
                } else Toast.makeText(requireContext(),"????????? ?????????????????????.\n?????? ??????????????????.", Toast.LENGTH_SHORT).show()

            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(),"???????????? ????????? ????????????.\n?????? ??????????????????.", Toast.LENGTH_SHORT).show()
            }

        })
    }


    fun getSectorThemeKeywordIncludeNews(keyword: String, num: Int){
        retrofit. getSectorThemeKeywordIncludeNews(keyword,num).enqueue(object : Callback<KeywordIncludeNewsList> {
            override fun onResponse(
                call: Call<KeywordIncludeNewsList>,
                response: Response<KeywordIncludeNewsList>
            ) {
                if(response.code()==200) {
                    addSectorthemeIncludeNewsList(response.body())
                    Log.d("stock/news/like/ API??????", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"????????? ??????????????????.\n?????? ??????????????????", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<KeywordIncludeNewsList>, t: Throwable) {
                Log.d("API??????2", t.message.toString())
            }
        })
    }
    private fun addSectorthemeIncludeNewsList(body: KeywordIncludeNewsList?){
        StockNewsList.clear()
        if(body.isNullOrEmpty()){

        }
        else{
            for(item in body){
                StockNewsList.add(NewsModel(item.ticker,item.provider,item.date,item.rink,item.title,item.sentiment))
            }
        }
        StockNewsListAdapter.notifyDataSetChanged()
        if(StockNewsList.size in 1..2){
            view.newsOpenBtn.visibility = View.GONE
        }
        else if(StockNewsList.size == 0){
            view.newsOpenBtn.visibility = View.GONE
            view.newsStockNoBtn.visibility = View.VISIBLE
        }
    }
    fun softkeyboardHide() {
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
        imm!!.hideSoftInputFromWindow(view.stockKeywordEdit.windowToken, 0)
    }



    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.news_open_btn -> {
                getSectorThemeKeywordIncludeNews(StockName, 10)
                view.newsCloseBtn.visibility = View.VISIBLE
                view.newsOpenBtn.visibility = View.GONE

            }
            R.id.news_close_btn -> {
                getSectorThemeKeywordIncludeNews(StockName, 3)
                view.newsCloseBtn.visibility = View.GONE
                view.newsOpenBtn.visibility = View.VISIBLE
            }
            R.id.stock_search_btn -> {
                softkeyboardHide()
                when (selectedPosition) {
                    1 -> { // ????????????
                        getStockNameByTicker(view.stockKeywordEdit.text.toString().trim())
                    }
                    2 -> { // ?????????
                        getTickerByStockName(view.stockKeywordEdit.text.toString().trim())
                    }
                    else -> { // ?????????
                        val bundle = Bundle()
                        bundle.putString("keyword", view.stockKeywordEdit.text.toString().trim())
                        bundle.putInt("type", selectedPosition)
                        replaceFragment(KeyWordFragment(), bundle)
                    }
                }
                view.stockKeywordEdit.text = null
            }
            R.id.company_info -> {
                val bundle = Bundle()
                bundle.putString("stock_ticker", StockTicker)
                bundle.putString("Focus", "hu")
                Log.d("good", bundle.toString())
                view.companyInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                view.companyInvestInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hmmm
                    )
                )
                replaceFragment2(Company_info_Fragment1(), bundle)
            }
            R.id.company_invest_info -> {
                val bundle = Bundle()
                bundle.putString("stock_ticker", StockTicker)
                bundle.putString("Focuss", "huu")
                view.companyInvestInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    )
                )
                view.companyInfo.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.hmmm
                    )
                )
                replaceFragment2(Company_info_Fragment2(), bundle)
            }

        }
    }
//

    private fun replaceFragment(fragment: Fragment, bundle: Bundle) {
        fragment.arguments = bundle
        Log.d("argument", bundle.toString())
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrameLayout, fragment)
            .addToBackStack(null)
            .commit()
    }
    private fun replaceFragment2(fragment: Fragment,bundle: Bundle) {
        fragment.arguments = bundle
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.company_frame_layout, fragment)
            .commit()
    }
    //dp ?????? px ????????? ????????? ?????? ??????
    private fun dipToPixels(dipValue: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dipValue,
            resources.displayMetrics
        )
    }
    // ???????????? ????????? ?????????
    private fun setListenerToEditText() {
        view.stockKeywordEdit.setOnKeyListener { view, keyCode, event ->
            // Enter Key Action
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // ????????? ?????????
                val imm =
                    ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.stock_keyword_edit.windowToken, 0)
                }
                // Toast Message
                showToastMessage(view.stock_keyword_edit.text.toString())
                true
            }
            false
        }
    }
    ////////////////////////////
    private fun showToastMessage(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
    fun getStockPrice(ticker: String, num: Int){
        retrofit.getStockPrice(ticker,num).enqueue(object : Callback<StockPriceList> {
            override fun onResponse(
                call: Call<StockPriceList>,
                response: Response<StockPriceList>
            ) {
                if(response.code()==200) {
                    if(response.body()!=null) {
                        var result = getStockData(response.body()!!)
                        drawStockChart(result)
                    }
                    Log.d("stock/price/API??????", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(),"????????? ??????????????????.\n?????? ??????????????????", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<StockPriceList>, t: Throwable) {
                Log.d("API??????2222222", t.message.toString())
            }
        })
    }
    fun drawStockChart(item: MutableList<StockChartModel>){
        Log.d("???????????????????????????", item.toString())
        // ????????? ?????????
        var average = 0F

        for( stock in item) {
            average += stock.price.toFloat()
        }
        average /= item.size

        // ???????????? ????????? ????????? ??????
        val entries = ArrayList<Entry>()
        val colors = Stack<Int>()
        for (stock in item) {
            if (stock.price >= average) {
                if (colors.isNotEmpty() && colors.peek() == Color.BLUE) {
                    entries.add(Entry(stock.createdAt.time.toFloat(), average))
                    colors.add(Color.TRANSPARENT)
                    entries.add(Entry(stock.createdAt.time.toFloat(), average))
                    colors.add(Color.RED)
                }
                entries.add(Entry(stock.createdAt.time.toFloat(), stock.price.toFloat()))
                colors.add(Color.RED)
            } else {
                if (colors.isNotEmpty() && colors.peek() == Color.RED) {
                    entries.add(Entry(stock.createdAt.time.toFloat(), average))
                    colors.add(Color.TRANSPARENT)
                    entries.add(Entry(stock.createdAt.time.toFloat(), average))
                    colors.add(Color.BLUE)
                }
                entries.add(Entry(stock.createdAt.time.toFloat(), stock.price.toFloat()))
                colors.add(Color.BLUE)
            }
            Collections.sort(entries, EntryXComparator())
        }

        val dataSet = LineDataSet(entries, "").apply {
            setDrawCircles(false)
            color = Color.RED
            highLightColor = Color.TRANSPARENT
            valueTextSize = 0F
            lineWidth = 1.5F
        }

        val lineData = LineData(dataSet)
        view.chart.run {
            data = lineData
            description.isEnabled = false // ?????? Description Label ?????????
            invalidate() // refresh
        }

        val averageLine = LimitLine(average).apply {
            lineWidth = 1F
            enableDashedLine(4F, 10F, 10F)
            lineColor = Color.DKGRAY
        }

        // ??????
        view.chart.legend.apply {
            isEnabled = false // ???????????? ??????
        }
        // Y ???
        view.chart.axisLeft.apply {
            // ??????, ?????????, ????????? ???????????? ??????
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)

            // ????????? ??????
            removeAllLimitLines()
            addLimitLine(averageLine)
        }
        view.chart.axisRight.apply {
            // ?????? Y?????? ?????????
            isEnabled = true
        }
        // X ???
        view.chart.xAxis.apply {
            // x??? ?????? ????????????
            textColor = Color.TRANSPARENT
            // ?????????, ????????? ???????????? ??????
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }
    }


    // ???????????? ????????? ????????????
    private fun getStockData(body: StockPriceList): MutableList<StockChartModel> {
        var result = mutableListOf<StockChartModel>()

        if(body.isNullOrEmpty()){

        }
        else{
            for(item in body) {
                result.add((StockChartModel(item.date, item.end_price.toLong())))
            }
//            Log.d("StockPrice", StockPrice.toString())
        }

        result.sortBy { it.createdAt }
        Log.d("StockPrice22",result.toString())
        return result

    }

}