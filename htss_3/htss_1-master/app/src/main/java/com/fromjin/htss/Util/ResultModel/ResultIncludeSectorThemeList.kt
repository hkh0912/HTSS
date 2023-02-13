package com.fromjin.htss.Util.ResultModel

class ResultIncludeSectorThemeList : ArrayList<ResultIncludeSectorThemeItem>()

data class ResultIncludeSectorThemeItem(
    val company_name: String,
    val keyword: String,
    val ticker: String,
    val end_price: Int,
    val rate: Float
)