package com.example.mccagent.network

import android.content.Context
import android.util.Log
import com.example.mccagent.BuildConfig
import com.example.mccagent.R
import com.example.mccagent.config.ApiConfig
import com.example.mccagent.models.interfaces.IApiService
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
        val apiKeyHeader = ApiConfig.getApiKeyHeader(context)
        val apiKey = ApiConfig.getApiKey(context)

        val logging = HttpLoggingInterceptor().apply {
            redactHeader(apiKeyHeader)
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val certResId = when (currentEnv) {
            ApiConfig.Environment.DEV -> R.raw.dev_cert
            ApiConfig.Environment.PREPROD -> R.raw.mccserverca
            ApiConfig.Environment.PROD -> R.raw.mccserverca
        }

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder().apply {
                    if (apiKey.isNotBlank()) {
                        addHeader(apiKeyHeader, apiKey)
                    }
                }.build()
                chain.proceed(request)
            }

        if (apiKey.isBlank()) {
            Log.w("RetrofitClient", "No hay API key configurada; las requests pueden ser rechazadas")
        }

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

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IApiService::class.java)
    }
}
