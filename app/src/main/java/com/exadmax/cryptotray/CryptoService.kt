package com.exadmax.cryptotray

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoService {
  @GET("simple/price")
  fun getPrices(
    @Query("ids") ids: String,
    @Query("vs_currencies") vs: String
  ): Call<Map<String, Map<String, Double>>>
}
