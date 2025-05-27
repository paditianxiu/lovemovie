package com.padi.lovemovie.service

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

object HotRetrofit {
    interface HotService {
        @GET("j/search_subjects")
        fun getHotData(
            @Query("type") type: String = "movie",
            @Query("sort") sort: String = "recommend",
            @Query("tag") tag: String = "热门",
            @Query("page_limit") pageLimit: Int = 50,
            @Query("page_start") pageStart: Int = 0
        ): Deferred<ResponseBody>
    }

    val client: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://movie.douban.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    }
    val hotService: HotService by lazy {
        client.create(HotService::class.java)
    }
}