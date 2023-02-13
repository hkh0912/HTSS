package com.fromjin.htss.Util.ResultModel

import java.util.Date

class ResultStockPriceList : ArrayList<ResultStockPriceItem>()

data class ResultStockPriceItem(
    val ticker: String,
    val date: Date,
    val end_price: Int,
    val rate: Float,
    val company_name: String
)