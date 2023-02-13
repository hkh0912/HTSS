package com.fromjin.htss.Util.ResultModel

import java.util.*
import kotlin.collections.ArrayList

class ResultStockNowPriceList : ArrayList<ResultStockNowPriceItem>()

data class ResultStockNowPriceItem(
    val ticker: String,
    val start_price: Int,
    val end_price: Int,
    val high_price: Int,
    val low_price: Int,
    val rate: Float
)