package com.chartiq.demo.network

import kotlinx.coroutines.Deferred
import retrofit2.Response
import java.net.ConnectException
import java.net.HttpURLConnection

private const val DEFAULT_ERROR_MESSAGE = "Something went wrong"

suspend fun <T> Deferred<Response<T>>.safeExtractNetworkResult():
        NetworkResult<T> {
    return try {
        val result = this.await()
        return if (result.isSuccessful) {
            NetworkResult.Success(result.body()!!)
        } else {
            NetworkResult.Failure(result.code(), result.message())
        }
    } catch (exception: Exception) {
        val code = if (exception::class.java == ConnectException::class.java) {
            HttpURLConnection.HTTP_UNAVAILABLE
        } else {
            2000
        }
        NetworkResult.Failure(code, DEFAULT_ERROR_MESSAGE)
    }
}
