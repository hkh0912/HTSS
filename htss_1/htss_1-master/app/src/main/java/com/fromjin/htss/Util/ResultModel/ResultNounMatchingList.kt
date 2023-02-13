package com.fromjin.htss.Util.ResultModel

class ResultNounMatchingList : ArrayList<ResultNounMatchingItem>()

data class ResultNounMatchingItem(
    val ticker : String,
    val noun : String,
    val count : Int,
    val company_name : String
)