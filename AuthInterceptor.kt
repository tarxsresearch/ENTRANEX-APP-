package com.tarxs.entranex.data.network

import com.tarxs.entranex.data.local.TokenStore
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Equivalent of the HTML app's authHeaders():
 *   function authHeaders() {
 *       const h = { "Content-Type": "application/json" };
 *       if (ennexToken) h["Authorization"] = "Bearer " + ennexToken;
 *       return h;
 *   }
 *
 * Attaches the bearer token to every outgoing request automatically.
 */
class AuthInterceptor(private val tokenStore: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = tokenStore.token

        val requestBuilder = original.newBuilder()
            .header("Content-Type", "application/json")

        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
