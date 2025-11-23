package com.example.playlistmaker.domain.api

interface NetworkClient {
    fun doRequest(dto: Any): com.example.playlistmaker.data.dto.BaseResponse
}
