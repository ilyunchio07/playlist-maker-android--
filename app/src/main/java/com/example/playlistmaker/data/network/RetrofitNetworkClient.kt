package com.example.playlistmaker.data.network

import com.example.playlistmaker.creator.Storage
import com.example.playlistmaker.data.dto.BaseResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.NetworkClient

class RetrofitNetworkClient(private val storage: Storage) : NetworkClient {

    override fun doRequest(dto: Any): BaseResponse {
        if (dto is TracksSearchRequest) {
            val result = storage.search(dto.expression)
            return TracksSearchResponse(result).apply { resultCode = 200 }
        } else {
            return BaseResponse().apply { resultCode = 400 }
        }
    }
}