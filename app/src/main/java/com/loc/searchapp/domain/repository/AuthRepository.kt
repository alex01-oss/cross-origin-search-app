package com.loc.searchapp.domain.repository

import com.loc.searchapp.data.network.dto.AuthResponse
import retrofit2.Response

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String
    ): Response<AuthResponse>

    suspend fun register(
        username: String,
        email: String,
        password:
        String
    ): Response<AuthResponse>

    suspend fun refresh(
        token: String
    ) : Response<AuthResponse>
}