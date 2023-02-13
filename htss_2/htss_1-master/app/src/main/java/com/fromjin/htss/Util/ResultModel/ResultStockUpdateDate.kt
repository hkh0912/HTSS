package com.fromjin.htss.Util.ResultModel

import java.util.Date

data class ResultStockUpdateDate (
    val day_minute1: Date,
    val day_minute10 : Date,
    val day_date : Date,
    val day_week: Date
        )