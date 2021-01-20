package com.chartiq.demo.network

data class NetworkException(override val message: String?, val code: Int) : Exception()