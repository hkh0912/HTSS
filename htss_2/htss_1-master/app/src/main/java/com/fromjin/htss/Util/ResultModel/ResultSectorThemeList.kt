package com.fromjin.htss.Util.ResultModel

class ResultSectorThemeList : ArrayList<ResultSectorThemeItem>()

data class ResultSectorThemeItem(
    val keyword: String,
    val rate: Float
)