package com.chartiq.demo.network

import retrofit2.Response
import java.net.ConnectException
import java.net.HttpURLConnection

fun <T> Response<T>.safeExtractNetworkResult(): NetworkResult<T> {
    return try {
        return if (isSuccessful) {
            NetworkResult.Success(body()!!)
        } else {
            NetworkResult.Failure(NetworkException(message(), code()))
        }
    } catch (exception: Exception) {
        val code = if (exception::class.java == ConnectException::class.java) {
            HttpURLConnection.HTTP_UNAVAILABLE
        } else {
            2000
        }
        NetworkResult.Failure(NetworkException(null, code))
    }
}
