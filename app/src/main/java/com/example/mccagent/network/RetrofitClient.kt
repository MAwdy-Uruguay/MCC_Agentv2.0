package com.example.mccagent.network

import android.content.Context
import android.util.Log
import com.example.mccagent.config.ApiConfig
import com.example.mccagent.models.interfaces.IApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    fun getApiService(context: Context): IApiService {
        val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
        ApiConfig.prefs = prefs // 💡 lo guardamos en ApiConfig por si se usa ahí

        val baseUrl = prefs.getString("base_url", ApiConfig.defaultBaseUrl) ?: ApiConfig.defaultBaseUrl

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val token = prefs.getString("token", null)
                val request = chain.request().newBuilder().apply {
                    if (!token.isNullOrEmpty()) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }.build()
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IApiService::class.java)
    }
    suspend fun getApiWithValidToken(context: Context): IApiService {
        val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
        var token = prefs.getString("token", null)

        if (token.isNullOrBlank() || isTokenExpired(token)) {
            token = renewToken(context)
            if (!token.isNullOrBlank()) {
                prefs.edit().putString("token", token).apply()
                Log.d("RetrofitClient", "\uD83D\uDD10 Token renovado y guardado: $token")
            } else {
                Log.e("RetrofitClient", "\u274C No se pudo renovar el token")
            }
        }

        return getApiService(context)
    }
    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            val payload = String(android.util.Base64.decode(parts[1], android.util.Base64.URL_SAFE))
            val regex = """"exp"\s*:\s*(\d+)""".toRegex()

            val match = regex.find(payload) ?: return true
            val exp = match.groupValues[1].toLong()
            val now = System.currentTimeMillis() / 1000
            exp < now
        } catch (e: Exception) {
            true
        }
    }
    suspend fun renewToken(context: Context): String? {
        return try {
            val prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)
            val oldToken = prefs.getString("token", null)

            val tempClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $oldToken")
                        .build()
                    chain.proceed(request)
                }
                .build()

            val tempRetrofit = Retrofit.Builder()
                .baseUrl(ApiConfig.defaultBaseUrl)
                .client(tempClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = tempRetrofit.create(IApiService::class.java)
            val response = service.renewToken()

            if (response.isSuccessful) {
                val newToken = response.body()?.token
                if (!newToken.isNullOrBlank()) {
                    prefs.edit().putString("token", newToken).apply()
                    Log.d("RetrofitClient", "🔁 Token renovado: $newToken")
                    return newToken
                }
            }

            null
        } catch (e: Exception) {
            Log.e("RetrofitClient", "💥 Error renovando token con Retrofit", e)
            null
        }
    }

}
