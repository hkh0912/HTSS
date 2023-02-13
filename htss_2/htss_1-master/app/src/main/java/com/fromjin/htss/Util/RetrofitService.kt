package com.fromjin.htss.Util

import com.fromjin.htss.Util.ResultModel.*
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
        @Query("keyword") keyword: String,
        @Query("num") num: Int
    ): Call<ResultSectorThemeList>

    //검색한 업종에 포함된 종목들
    @GET("sector/include")
    fun getSectorIncludeList(
        @Query("sector") keyword: String,
        @Query("num") num: Int
    ): Call<ResultSectorThemeList>

    // 테마상위
    @GET("thema/high")
    fun getThemeHighList(
        @Query("num") num: Int
    ): Call<ResultSectorThemeList>

    //업종like
    @GET("thema/like")
    fun getThemaLikeList(
        @Query("keyword") keyword: String,
        @Query("num") num: Int
    ): Call<ResultSectorThemeList>

    //최근뉴스
    @GET("news/recent")
    fun getRecentNewsList(
        @Query("num") num: Int
    ): Call<ResultNewsList>

    //뉴스like
    @GET("news/like")
    fun getNewsLikeList(
        @Query("keyword") keyword: String,
        @Query("num") num: Int
    ): Call<ResultNewsList>

    @GET("stock/info")
    fun getStockInfoByTicker(
        @Query("ticker") ticker:String
    ):Call<ResultStockInfo>

    @GET("stock/name")
    fun getStockNameByTicker(
        @Query("ticker") ticker:String
    ):Call<String>

    @GET("stock/ticker")
    fun getStockTickerByName(
        @Query("company_name") company_name:String
    ):Call<String>

    @GET("stock/price")
    fun getStockPriceByTicker(
        @Query("ticker") ticker: String,
        @Query("num") num: Int
    ): Call<ResultStockPriceList>

    @GET("stock/now-price")
    fun getStockNowPriceByTicker(
        @Query("ticker") ticker: String
    ): Call<ResultStockNowPriceList>

    @GET("stock/like")
    fun getStockLikeList(
        @Query("keyword") keyword: String
    ): Call<ArrayList<String>>

    @GET("stock/date")
    fun getStockUpdateDate(
    ): Call<ResultStockUpdateDate>

}