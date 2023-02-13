package com.fromjin.htss.Util

import com.fromjin.htss.Util.ResultModel.ResultNounMatchingList
import com.fromjin.htss.Util.ResultModel.ResultSectorThemeList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {

    //키워드매칭주식
    @GET("noun/matching")
    fun getNounMatchingList(
        @Query("noun") noun: String,
        @Query("num") num: Int
    ): Call<ResultNounMatchingList>

    //업종상위
    @GET("sector/high")
    fun getSectorHighList(
        @Query("num") num: Int
    ): Call<ResultSectorThemeList>

    //업종like
    @GET("sector/like")
    fun getSectorLikeList(
        @Query("keyword") noun: String
    ): Call<ResultSectorThemeList>

    // 테마상위
    @GET("thema/high")
    fun getThemeHighList(
        @Query("num") num: Int
    ): Call<ResultSectorThemeList>

    //업종like
    @GET("thema/like")
    fun getThemaLikeList(
        @Query("keyword") noun: String
    ): Call<ResultSectorThemeList>
}