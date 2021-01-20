package com.chartiq.demo.network

sealed class NetworkResult<out T> {

    data class Success<out T>(val data: T) : NetworkResult<T>()

    data class Failure(val exception: NetworkException) : NetworkResult<Nothing>()
}
