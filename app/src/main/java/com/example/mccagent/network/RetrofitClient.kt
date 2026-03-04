package com.example.mccagent.network

import android.content.Context
import android.util.Log
import com.example.mccagent.R
import com.example.mccagent.config.ApiConfig
import com.example.mccagent.models.interfaces.IApiService
import com.example.mccagent.utils.SecureSessionStorage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

object RetrofitClient {

    fun getApiService(context: Context): IApiService {
        ApiConfig.prefs = context.getSharedPreferences("mcc_prefs", Context.MODE_PRIVATE)

        val baseUrl = ApiConfig.getBaseUrl(context)
        val currentEnv = ApiConfig.getEnv(context)

        // Logging
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // 🔑 Seleccionar el certificado según el ambiente
        val certResId = when (currentEnv) {
            ApiConfig.Environment.DEV -> R.raw.dev_cert
            ApiConfig.Environment.PREPROD -> R.raw.mccserverca  // agregalo en res/raw
            ApiConfig.Environment.PROD -> R.raw.mccserverca // PROD debería usar un cert público válido
        }

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val token = SecureSessionStorage.obtenerToken(context)
                val request = chain.request().newBuilder().apply {
                    if (!token.isNullOrEmpty()) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }.build()
                chain.proceed(request)
            }

        if (certResId != null) {
            // 💡 Cargar el certificado desde res/raw
            val cf = CertificateFactory.getInstance("X.509")
            context.resources.openRawResource(certResId).use { caInput ->
                val ca = cf.generateCertificate(caInput)

                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null, null)
                keyStore.setCertificateEntry("papu_ca", ca)

                val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                tmf.init(keyStore)
                val trustManager = tmf.trustManagers[0] as X509TrustManager

                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, arrayOf(trustManager), null)

                clientBuilder.sslSocketFactory(sslContext.socketFactory, trustManager)
            }
        }

        val client = clientBuilder.build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IApiService::class.java)
    }

    suspend fun getApiWithValidToken(context: Context): IApiService {
        var token = SecureSessionStorage.obtenerToken(context)

        if (token.isNullOrBlank() || isTokenExpired(token)) {
            token = renewToken(context)
            if (!token.isNullOrBlank()) {
                SecureSessionStorage.guardarToken(context, token)
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
            val oldToken = SecureSessionStorage.obtenerToken(context)

            val tempClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer $oldToken")
                        .build()
                    chain.proceed(request)
                }
                .build()

            val tempRetrofit = Retrofit.Builder()
                .baseUrl(ApiConfig.getBaseUrl(context))
                .client(tempClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = tempRetrofit.create(IApiService::class.java)
            val response = service.renewToken()

            if (response.isSuccessful) {
                val newToken = response.body()?.token
                if (!newToken.isNullOrBlank()) {
                    SecureSessionStorage.guardarToken(context, newToken)
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
