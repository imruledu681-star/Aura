package com.example.data.network

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GiphyResponse(
    val data: List<GiphyGifObject>?
)

@JsonClass(generateAdapter = true)
data class GiphyGifObject(
    val id: String,
    val title: String,
    val images: GiphyImages?
)

@JsonClass(generateAdapter = true)
data class GiphyImages(
    val original: GiphyImageDetail?,
    @Json(name = "fixed_width") val fixedWidth: GiphyImageDetail?,
    @Json(name = "fixed_height") val fixedHeight: GiphyImageDetail?
)

@JsonClass(generateAdapter = true)
data class GiphyImageDetail(
    val url: String
)

interface GiphyService {
    @GET("v1/gifs/trending")
    suspend fun getTrendingGifs(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int = 20,
        @Query("rating") rating: String = "g"
    ): GiphyResponse

    @GET("v1/gifs/search")
    suspend fun searchGifs(
        @Query("api_key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = 20,
        @Query("rating") rating: String = "g"
    ): GiphyResponse
}

object GiphyClient {
    private const val BASE_URL = "https://api.giphy.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GiphyService = retrofit.create(GiphyService::class.java)

    private fun getApiKey(): String {
        val apiKey = BuildConfig.GIPHY_API_KEY
        return if (apiKey.isBlank() || apiKey == "YOUR_GIPHY_API_KEY" || apiKey.length < 10) {
            "f6QexA8ZSgAJ9cYy97xfbiJexJQCXXtA" // Use user's real custom developer API key
        } else {
            apiKey
        }
    }

    suspend fun getTrending(): List<GiphyGifObject> {
        return try {
            val response = service.getTrendingGifs(apiKey = getApiKey())
            response.data ?: emptyList()
        } catch (e: Exception) {
            android.util.Log.e("GiphyClient", "Giphy trending request failed: ${e.message}")
            emptyList()
        }
    }

    suspend fun search(query: String): List<GiphyGifObject> {
        if (query.isBlank()) return getTrending()
        return try {
            val response = service.searchGifs(apiKey = getApiKey(), query = query)
            response.data ?: emptyList()
        } catch (e: Exception) {
            android.util.Log.e("GiphyClient", "Giphy search request failed: ${e.message}")
            emptyList()
        }
    }
}
