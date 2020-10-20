package com.chartiq.demo.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import java.net.ConnectException
import java.net.HttpURLConnection

suspend fun <T> Deferred<Response<T>>.safeExtractNetworkResult():
        NetworkResult<T> {
    return try {
        val result = this.await()
        return if (result.isSuccessful) {
            NetworkResult.Success(result.body()!!)
        } else {
            NetworkResult.Failure(NetworkException(result.message(), result.code()))
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
