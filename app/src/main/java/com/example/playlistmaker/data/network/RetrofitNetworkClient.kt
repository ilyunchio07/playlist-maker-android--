package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.BaseResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val imdbBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(imdbBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesApi::class.java)

    override fun doRequest(dto: Any): BaseResponse {
        if (dto is TracksSearchRequest) {
            return try {
                val resp = iTunesService.search(dto.expression).execute()
                val body = resp.body() ?: return BaseResponse().apply { resultCode = resp.code() }

                body.apply { resultCode = resp.code() }
            } catch (e: Exception) {
                BaseResponse().apply { resultCode = 400 }
            }
        } else {
            return BaseResponse().apply { resultCode = 400 }
        }
    }
}