package com.example.htss.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.htss.Adapter.HomeAdapter
import com.example.htss.Adapter.InterestKeywordAdapter
import com.example.htss.Adapter.MainNewsAdapter
import com.example.htss.Adapter.StockRaiseListAdapter
import com.example.htss.Model.MainModel
import com.example.htss.Model.NewsModel
import com.example.htss.Model.StockRaiseListModel
import com.example.htss.R
import com.example.htss.Retrofit.Model.NewsList
import com.example.htss.Retrofit.Model.SectorThemeList
import com.example.htss.Retrofit.Model.StockHighRateList
import com.example.htss.Retrofit.Model.StockMarketList
import com.example.htss.Retrofit.MySharedPreferences
import com.example.htss.Retrofit.RetrofitClient
import com.example.htss.databinding.FragmentHomeBinding
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class HomeFragment : Fragment(), View.OnClickListener {

    var selectedPosition = 0

//    private lateinit var mUserViewModel: UserViewModel

    private lateinit var view: FragmentHomeBinding
    private val retrofit = RetrofitClient.create()
    private val sharedPreferences = MySharedPreferences

    /////////////????????????
    private val categoryRankList = mutableListOf<MainModel>()
    private var interestKeywordJson = JSONArray()
    private val interestKeywordList = mutableListOf<String>()
    private val themeRankList = mutableListOf<MainModel>()
    private val stockRaiseList = mutableListOf<StockRaiseListModel>()
    private val newsRankList = mutableListOf<NewsModel>()

    //////////////////////////???????????? ????????????
    private val interestKeywordListAdapter = InterestKeywordAdapter(interestKeywordList)
    private val categoryRankListAdapter = HomeAdapter(categoryRankList)
    private val themeRankListAdapter = HomeAdapter(themeRankList)
    private val newsRankListAdapter = MainNewsAdapter(newsRankList)
    private val stockRaiseListAdapter = StockRaiseListAdapter(stockRaiseList)

    val dec = DecimalFormat("#,###.##")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view = FragmentHomeBinding.inflate(inflater, container, false)

        // shared??? ????????? ???????????? ?????? ?????? ??????
        if (sharedPreferences.isNotEmpty(requireContext())) {
            // ????????? ?????? ????????? ???
            interestKeywordList.clear()
            // state == 1 ????????????
            // String > JSONArray > mutableList<String> ?????? ??????
            setInterestKeywordList( 1, null, null)
        }


        val items = resources.getStringArray(R.array.search_array)
        val myAdapter = object : ArrayAdapter<String>(requireContext(), R.layout.item_spinner) {
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

        myAdapter.addAll(items.toMutableList())
        myAdapter.add("????????????")
        view.searchSpinner.adapter = myAdapter
        view.searchSpinner.setSelection(myAdapter.count)
        view.searchSpinner.dropDownVerticalOffset = dipToPixels(35f).toInt()
        //????????? ????????? ????????? ??????
        view.searchSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedPosition = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Log.d("MyTag", "onNothingSelected")
            }
        }

        /////////????????????????????? ????????? ?????????
        view.recycle1.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = categoryRankListAdapter
        }

        view.recycle2.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = themeRankListAdapter

        }

        view.recycle3.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = newsRankListAdapter
            addItemDecoration(
                DividerItemDecoration(
                    view.recycle3.context,
                    LinearLayoutManager(context).orientation
                )
            )
        }
        view.recycleRate.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = stockRaiseListAdapter
        }

        view.keyword.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = interestKeywordListAdapter
        }

        stockRaiseListAdapter.setItemClickListener(object :
            StockRaiseListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    bundle.putString("stock_ticker", stockRaiseList[position].StockRaiseTicker)
                    bundle.putString("stock_name", stockRaiseList[position].StockRaisename)
                }
                replaceFragment(StockFragment(), bundle)
            }
        })

        categoryRankListAdapter.setItemClickListener(object : HomeAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    bundle.putString("category_name", categoryRankList[position].name)
                    bundle.putString("category_percent", categoryRankList[position].percent)
                }
                Log.d("argument", bundle.toString())
                replaceFragment(CategoryDetailFragment(), bundle)
            }
        })

        themeRankListAdapter.setItemClickListener(object : HomeAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.apply {
                    bundle.putString("theme_name", themeRankList[position].name)
                    bundle.putString("theme_percent", themeRankList[position].percent)
                }
                replaceFragment(ThemeDetailFragment(), bundle)

            }
        })

        newsRankListAdapter.setItemClickListener(object : MainNewsAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(newsRankList[position].rink)))

            }
        })

        newsRankListAdapter.setLinkClickListener(object : MainNewsAdapter.OnLinkClickListener {
            override fun onClick(v: View, position: Int) {
                getStockNameByTicker(newsRankList[position].ticker)
            }
        })

        // ??????????????? ?????? ??? ????????????????????? ???????????? ??????
        interestKeywordListAdapter.setItemClickListener(object :
            InterestKeywordAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val bundle = Bundle()
                bundle.putString("keyword", interestKeywordList[position])
                replaceFragment(KeyWordFragment(), bundle)
            }
        })
        // ??????????????? ?????? ???
        interestKeywordListAdapter.setDeleteClickListener(object :
            InterestKeywordAdapter.OnDeleteClickListener {
            override fun onClick(v: View, position: Int) {
                Log.d("receive_delete_position", position.toString())
                Log.d("InterestKeywordList", interestKeywordList.toString())
//                interestKeywordList.removeAt(position)
//                interestKeywordListAdapter.notifyDataSetChanged()

                // state == 3 ????????? ??????(????????? ????????? ??????????????? ????????????)
                setInterestKeywordList( 3, null, position)
            }
        })

        setListenerToEditText()
        getHighSectorList(3)
        getHighThemeList(3)
        getMainNewsList(3)
        getStockHighRate(15)
        getStockMarket("?????????")
        getStockMarket("?????????")

        view.seeMore1.setOnClickListener(this)
        view.seeMore2.setOnClickListener(this)
        view.rightArrow1.setOnClickListener(this)
        view.rightArrow2.setOnClickListener(this)
        view.searchBtn.setOnClickListener(this)
        view.open.setOnClickListener(this)
        view.close.setOnClickListener(this)
        view.searchBtn.setOnClickListener(this)
        view.seeMore3.setOnClickListener(this)
        view.rightArrow3.setOnClickListener(this)
        view.plus.setOnClickListener(this)
        view.deleteAll.setOnClickListener(this)


        return view.root
    }

    // ??????????????? ??????
    // 1, null, null
    // 2, keyword, null
    // 3, null, position
    private fun setInterestKeywordList(
        state: Int,
        keyword: String?,
        position: Int?
    ) {
        when (state) {
            1 -> { // ????????????
                // sharedPreference??? ????????? JSONArray > mutableList
                if (sharedPreferences.getKeywordList(requireContext()) != null) {
                    // String > JSONArray ??? ??????
                    interestKeywordJson =
                        JSONArray(sharedPreferences.getKeywordList(requireContext()))
                    // JSONArray > mutableList<String> ??? ?????? ??????
                    for (i in 0 until interestKeywordJson.length()) {
                        interestKeywordList.add(interestKeywordJson.optString(i))
                    }
                }

            }
            2 -> { // ?????? ????????? ????????????
                if (keyword != null) {
                    interestKeywordJson.put(keyword)
                    interestKeywordList.add(keyword)
                    sharedPreferences.setKeywordList(
                        requireContext(),
                        interestKeywordJson.toString()
                    )
                } else {
                    Toast.makeText(requireContext(),"????????? ???????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }
            }
            3 -> { // ?????? ????????? ????????????
                if (position != null) {
                    interestKeywordJson.remove(position)
                    interestKeywordList.removeAt(position)
                    sharedPreferences.setKeywordList(
                        requireContext(),
                        interestKeywordJson.toString()
                    )
                } else {
                    Toast.makeText(requireContext(),"????????? ???????????? ?????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }

            }
        }
        interestKeywordListAdapter.notifyDataSetChanged()
        Log.d("??????????????????", sharedPreferences.getKeywordList(requireContext()))
        Log.d("??????????????????22", interestKeywordList.toString())
    }


//    pr???vate fun insertDataToDatabase() {
//        //???????????? ????????? ??????????????? ???????????????.
//        val keyword = view.interestText.text.toString()
//
//        if(inputCheck(keyword)){ //inputcheck?????? ????????? ????????????????????????.
//            //?????? ??????????????? ??????????????????.??? ?????? database??? ???????????? ?????????.
//
//            val user = User(0,keyword)
//            //id??? 0??? ????????? ????????? PK??? ???????????? ???????????? ???????????? ?????? ?????????????????????.
//
//            //???????????? add user??? ??????????????? ????????????????????? user?????? ???????????? ?????????.
//            mUserViewModel.addUser(user)
//
//        }
//        else{
//            //?????? ????????? editText?????? ?????? ???????????? ????????? ???????????? ????????????.
//            Toast.makeText(requireContext(), "Please fill out all fields.", Toast.LENGTH_LONG).show()
//        }
//    }

//    private fun inputCheck(keyword: String): Boolean {
//        return !(TextUtils.isEmpty(keyword))
//    }

    //?????????????????? ??????????????? ???????????????.


    //??????????????? ??? ????????? ????????????
    fun softkeyboardHide() {
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm!!.hideSoftInputFromWindow(view.editText.windowToken, 0)
    }

    fun softkeyboardHide2() {
        val imm = getSystemService(requireContext(), InputMethodManager::class.java)
        imm!!.hideSoftInputFromWindow(view.interestText.windowToken, 0)
    }

    // ??????????????? ??? ????????? ????????????
    private fun setListenerToEditText() {
        view.editText.setOnKeyListener { view, keyCode, event ->
            // Enter Key Action
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // ????????? ?????????
                val imm = getSystemService(requireContext(), InputMethodManager::class.java)
                if (imm != null) {
                    imm.hideSoftInputFromWindow(view.edit_text.windowToken, 0)
                }
                // Toast Message
                showToastMessage(view.edit_text.text.toString())
                true
            }
            false
        }
    }

    private fun showToastMessage(msg: String?) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    fun getStockMarket(name: String) {
        retrofit.getStockMarket(name).enqueue(object : Callback<StockMarketList> {
            override fun onResponse(
                call: Call<StockMarketList>,
                response: Response<StockMarketList>
            ) {
                if (response.code() == 200) {
                    addStockMarketList(response.body()!!)
                    Log.d("API??????", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "????????? ??????????????????.\n?????? ??????????????????", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<StockMarketList>, t: Throwable) {
                Log.d("API??????2", t.message.toString())
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun addStockMarketList(body: StockMarketList) {
        if (body.market == "?????????") {
            view.kospiPrice.text = dec.format(body.now_value).toString()
            if (body.change_rate >= 0.0) {
                view.kospiChangePlusRate.text = "+" + body.change_rate.toString() + "%"
                view.kospiChangePlusRate.visibility = View.VISIBLE
                view.kospiChangeMinusRate.visibility = View.GONE
                view.kospiPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                view.kospiChangePlusValueShape.visibility = View.VISIBLE
                view.kospiChangeMinusValueShape.visibility = View.GONE
                view.kospiChangeValue.text = body.change_value.toString()
                view.kospiChangeValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
            } else {
                view.kospiChangeMinusRate.text = body.change_rate.toString() + "%"
                view.kospiChangePlusRate.visibility = View.GONE
                view.kospiChangeMinusRate.visibility = View.VISIBLE
                view.kospiPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue))
                view.kospiChangePlusValueShape.visibility = View.GONE
                view.kospiChangeMinusValueShape.visibility = View.VISIBLE
                view.kospiChangeValue.text = body.change_value.toString()
                view.kospiChangeValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
            }

        } else {
            view.kosdakPrice.text = dec.format(body.now_value).toString()
            if (body.change_rate >= 0.0) {
                view.kosdakChangePlusRate.text = "+" + body.change_rate.toString() + "%"
                view.kosdakChangePlusRate.visibility = View.VISIBLE
                view.kosdakChangeMinusRate.visibility = View.GONE
                view.kosdakPrice.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
                view.kosdakChangePlusValueShape.visibility = View.VISIBLE
                view.kosdakChangeMinusValueShape.visibility = View.GONE
                view.kosdakChangeValue.text = body.change_value.toString()
                view.kosdakChangeValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.red
                    )
                )
            } else {
                view.kosdakChangeMinusRate.text = body.change_rate.toString() + "%"
                view.kosdakChangePlusRate.visibility = View.GONE
                view.kosdakChangeMinusRate.visibility = View.VISIBLE
                view.kosdakPrice.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
                view.kosdakChangePlusValueShape.visibility = View.GONE
                view.kosdakChangeMinusValueShape.visibility = View.VISIBLE
                view.kosdakChangeValue.text = body.change_value.toString()
                view.kosdakChangeValue.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.blue
                    )
                )
            }
        }
    }

    fun getStockHighRate(num: Int) {
        retrofit.getStockHighRate(num).enqueue(object : Callback<StockHighRateList> {
            override fun onResponse(
                call: Call<StockHighRateList>,
                response: Response<StockHighRateList>
            ) {
                if (response.code() == 200) {
                    addStockHighRateList(response.body()!!)
                    Log.d("API??????", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "????????? ??????????????????.\n?????? ??????????????????", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<StockHighRateList>, t: Throwable) {
                Log.d("API??????2", t.message.toString())
            }
        })
    }

    private fun addStockHighRateList(body: StockHighRateList?) {
        stockRaiseList.clear()
        if (body.isNullOrEmpty()) {

        } else {
            for (item in body) {
                if (item.rate >= 0) {
                    stockRaiseList.add(
                        StockRaiseListModel(
                            item.ticker,
                            item.company_name,
                            dec.format(item.end_price).toString(),
                            "+" + item.rate.toString() + "%"
                        )
                    )
                } else {
                    stockRaiseList.add(
                        StockRaiseListModel(
                            item.ticker,
                            item.company_name,
                            dec.format(item.end_price).toString(),
                            item.rate.toString() + "%"
                        )
                    )
                }
            }
        }
        stockRaiseListAdapter.notifyDataSetChanged()
    }


    fun getTickerByStockName(name: String) {
        retrofit.getTickerByStockName(name).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() == 200) {
                    if (!response.body().isNullOrBlank()) {
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", response.body())
                        bundle.putString("stock_name", name)
                        replaceFragment(StockFragment(), bundle)
                    } else {
                        Toast.makeText(requireContext(), "???????????? ????????? ????????????.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else Toast.makeText(
                    requireContext(),
                    "????????? ?????????????????????.\n?????? ??????????????????.",
                    Toast.LENGTH_SHORT
                ).show()

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(), "????????? ?????????????????????.\n?????? ??????????????????.", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    fun getStockNameByTicker(ticker: String) {
        retrofit.getStockNameByTicker(ticker).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.code() == 200) {
                    if (!response.body().isNullOrBlank()) {
                        val bundle = Bundle()
                        bundle.putString("stock_ticker", ticker)
                        bundle.putString("stock_name", response.body())
                        Log.d("homefragment", bundle.toString())
                        replaceFragment(StockFragment(), bundle)
                    } else {
                        Toast.makeText(requireContext(), "???????????? ????????? ????????????.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else Toast.makeText(
                    requireContext(),
                    "????????? ?????????????????????.\n?????? ??????????????????.",
                    Toast.LENGTH_SHORT
                ).show()

            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(requireContext(), "???????????? ????????? ????????????.\n?????? ??????????????????.", Toast.LENGTH_SHORT)
                    .show()
            }

        })
    }

    fun getHighSectorList(num: Int) {
        retrofit.getHighSectorList(num).enqueue(object : Callback<SectorThemeList> {
            override fun onResponse(
                call: Call<SectorThemeList>,
                response: Response<SectorThemeList>
            ) {
                if (response.code() == 200) {
                    addResultSectorThemeHighList("sector", response.body())
                    Log.d("API??????", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "????????? ??????????????????.\n?????? ??????????????????", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<SectorThemeList>, t: Throwable) {
                Log.d("API??????2", t.message.toString())
            }

        })
    }

    fun getHighThemeList(num: Int) {
        retrofit.getHighThemeList(num).enqueue(object : Callback<SectorThemeList> {
            override fun onResponse(
                call: Call<SectorThemeList>,
                response: Response<SectorThemeList>
            ) {
                if (response.code() == 200) {
                    addResultSectorThemeHighList("theme", response.body())
                    Log.d("API??????", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "????????? ??????????????????.\n?????? ??????????????????", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<SectorThemeList>, t: Throwable) {
                Log.d("API??????2", t.message.toString())
            }

        })
    }

    fun getMainNewsList(num: Int) {
        retrofit.getMainNewsList(num).enqueue(object : Callback<NewsList> {
            override fun onResponse(
                call: Call<NewsList>,
                response: Response<NewsList>
            ) {
                if (response.code() == 200) {
                    addNewsList(response.body())
                    Log.d("API??????", response.raw().toString())
                } else {
                    Toast.makeText(requireContext(), "????????? ??????????????????.\n?????? ??????????????????", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<NewsList>, t: Throwable) {
                Log.d("API??????2", t.message.toString())
            }
        })
    }

    private fun addResultSectorThemeHighList(name: String, body: SectorThemeList?) {
        when (name) {
            "sector" -> {
                categoryRankList.clear()
                if (body != null) {
                    for (item in body) {
                        Log.d("API??????", item.toString())
                        if (item.rate >= 0.0) {
                            categoryRankList.add(
                                MainModel(
                                    item.keyword,
                                    "+" + item.rate.toString() + "%"
                                )
                            )
                        } else {
                            categoryRankList.add(
                                MainModel(
                                    item.keyword,
                                    item.rate.toString() + "%"
                                )
                            )
                        }
                    }
                }
                Log.d("API???????????????", categoryRankList.toString())
                categoryRankListAdapter.notifyDataSetChanged()
            }
            "theme" -> {
                themeRankList.clear()
                if (body != null) {
                    for (item in body) {
                        Log.d("API??????", item.toString())
                        if (item.rate >= 0.0) {
                            themeRankList.add(
                                MainModel(
                                    item.keyword,
                                    "+" + item.rate.toString() + "%"
                                )
                            )
                        } else {
                            themeRankList.add(MainModel(item.keyword, item.rate.toString() + "%"))
                        }
                    }
                }
                Log.d("API???????????????", themeRankList.toString())
                themeRankListAdapter.notifyDataSetChanged()

            }
        }
    }

    private fun addNewsList(body: NewsList?) {
        newsRankList.clear()
        if (body.isNullOrEmpty()) {

        } else {
            for (item in body) {
                Log.d("API????????????", item.toString())
                newsRankList.add(
                    NewsModel(
                        item.ticker,
                        item.provider,
                        item.date,
                        item.rink,
                        item.title,
                        item.sentiment
                    )
                )
            }
            newsRankListAdapter.notifyDataSetChanged()
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

    private fun replaceFragment2(fragment: Fragment) {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.mainFrameLayout, fragment)
            .addToBackStack(null)
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

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.see_more1 -> {
                val bundle = Bundle()
                Log.d("argument", "goodgood")
                bundle.putString("focus", "good")
                replaceFragment(AllListFragment(), bundle)
            }
            R.id.see_more2 -> {
                val bundle = Bundle()
                bundle.putString("foccus", "hue")
                replaceFragment(AllListFragment(), bundle)
            }
            R.id.right_arrow1 -> {
                val bundle = Bundle()
                bundle.putString("focus", "thue")
                replaceFragment(AllListFragment(), bundle)
            }
            R.id.right_arrow2 -> {
                val bundle = Bundle()
                bundle.putString("foccus", "hue")
                replaceFragment(AllListFragment(), bundle)
            }//////
            R.id.search_btn -> {
                softkeyboardHide()
                when (selectedPosition) {
                    1 -> { // ????????????
                        getStockNameByTicker(view.editText.text.toString().trim())
                    }
                    2 -> { // ?????????
                        getTickerByStockName(view.editText.text.toString().trim())
                    }
                    else -> { // ?????????, ??????, ??????
                        val bundle = Bundle()
                        bundle.putString("keyword", view.editText.text.toString().trim())
                        bundle.putInt("type", selectedPosition)
                        replaceFragment(KeyWordFragment(), bundle)
                    }
                }
                view.editText.text = null
            }
            R.id.open -> {
                getMainNewsList(10)
                view.close.visibility = View.VISIBLE
                view.open.visibility = View.GONE
            }
            R.id.close -> {
                getMainNewsList(3)
                view.close.visibility = View.GONE
                view.open.visibility = View.VISIBLE
            }
            R.id.see_more3 -> {
                replaceFragment2(StockHighRateListFragment())
            }
            R.id.right_arrow3 -> {
                replaceFragment2(StockHighRateListFragment())
            }
            /////////////////////////?????? ????????????
            R.id.plus -> {
                softkeyboardHide2()
                // state == 2 ????????? ??????
                setInterestKeywordList(
                    2,
                    view.interestText.text.toString().trim(),
                    null
                )
//                editor.putString("key",view.interestText.text.toString().trim()).apply()
                view.interestText.text = null
            }
            R.id.delete_all -> {
                MySharedPreferences.clear(requireContext())
            }
        }
    }


}