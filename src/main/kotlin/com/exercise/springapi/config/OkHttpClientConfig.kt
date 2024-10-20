package com.exercise.springapi.config

import java.time.Duration
import java.time.temporal.ChronoUnit
import okhttp3.OkHttpClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OkHttpClientConfig {

    @Bean
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .connectTimeout(Duration.of(10, ChronoUnit.SECONDS))
            .readTimeout(Duration.of(10, ChronoUnit.SECONDS))
            .writeTimeout(Duration.of(10, ChronoUnit.SECONDS))
            .build()
    }
}
