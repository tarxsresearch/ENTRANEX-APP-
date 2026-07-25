package com.tarxs.entranex.data.network

import com.tarxs.entranex.data.local.TokenStore
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Builds one Retrofit instance per backend, same idea as the HTML app having
 * separate api*() / auth*() / posts*() helper functions pointed at different
 * *_BASE constants.
 */
object RetrofitClient {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
    }

    private fun okHttpClient(tokenStore: TokenStore): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStore))
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun buildRetrofit(baseUrl: String, tokenStore: TokenStore): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient(tokenStore))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    fun authApi(tokenStore: TokenStore): AuthApi =
        buildRetrofit(ApiConfig.AUTH_BASE, tokenStore).create(AuthApi::class.java)

    // postsApi(), chatApi(), popupApi() will be added in later chunks
    // once we build the feed/chat/notifications screens.
}
