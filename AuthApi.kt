package com.tarxs.entranex.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Matches:
 *   POST {AUTH_BASE}/auth/signup
 *   POST {AUTH_BASE}/auth/login
 * from the original index.html.
 */
interface AuthApi {
    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): Response<SignupResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<LoginResponse>
}
