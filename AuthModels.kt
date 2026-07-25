package com.tarxs.entranex.data.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class SignupResponse(
    val message: String? = null,
    val detail: String? = null
)

@Serializable
data class LoginRequest(
    val identifier: String,
    val password: String
)

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String? = null,
    val user: UserInfo? = null,
    val detail: String? = null
)

@Serializable
data class UserInfo(
    val username: String? = null
)
