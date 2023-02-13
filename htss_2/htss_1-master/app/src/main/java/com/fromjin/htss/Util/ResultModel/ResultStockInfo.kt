package com.fromjin.htss.Util.ResultModel

class ResultStockInfo : ArrayList<ResultStockInfoItem>()

data class ResultStockInfoItem(
        val ticker: String,
        val company_name: String,
        val company_info: String
)