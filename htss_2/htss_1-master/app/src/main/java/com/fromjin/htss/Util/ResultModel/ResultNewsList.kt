package com.fromjin.htss.Util.ResultModel

import java.util.*
import kotlin.collections.ArrayList

class ResultNewsList : ArrayList<ResultNewsItem>()

data class ResultNewsItem(
    val ticker: String,
    val provider: String,
    val date: Date,
    val title: String,
    val rink: String
)