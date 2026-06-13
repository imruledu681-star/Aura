package com.example.data.network

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    val text: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    @Json(name = "responseMimeType") val responseMimeType: String? = null,
    @Json(name = "responseSchema") val responseSchema: ResponseSchema? = null
)

@JsonClass(generateAdapter = true)
data class ResponseSchema(
    val type: String,
    val properties: Map<String, SchemaProperty>? = null,
    val required: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class SchemaProperty(
    val type: String,
    val description: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    val content: Content? = null
)

@JsonClass(generateAdapter = true)
data class TranslationResult(
    val welcomeTitle: String,
    val welcomeDesc: String,
    val getStartedText: String,
    val alreadyHaveProfileText: String
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun translateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiTranslationClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val service: GeminiApiService = retrofit.create(GeminiApiService::class.java)

    suspend fun translateWelcomeScreen(targetLanguage: String): TranslationResult? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // No custom API key provided, fallback directly
            return null
        }

        val prompt = """
            Translate the following welcome screen user interface elements of 'Aura' (a dynamic, beautiful, and hyper-intuitive social network app) into the language: "$targetLanguage".
            
            Source phrases:
            1. Welcome Title: "Join Aura"
            2. Welcome Description: "Connect with friends, family and communities of people who share your interests."
            3. "Get started"
            4. "I already have a profile"
            
            Rules:
            - Provide a natural-sounding, contextually appropriate translation that feels elegant and visually balanced.
            - Do not include raw translation markers or list numbering.
            - Ensure output strictly validates against the JSON schema.
        """.trimIndent()

        val schema = ResponseSchema(
            type = "OBJECT",
            properties = mapOf(
                "welcomeTitle" to SchemaProperty("STRING", "Natural translation of 'Join Aura'"),
                "welcomeDesc" to SchemaProperty("STRING", "Natural translation of the welcome description"),
                "getStartedText" to SchemaProperty("STRING", "Natural translation of 'Get started'"),
                "alreadyHaveProfileText" to SchemaProperty("STRING", "Natural translation of 'I already have a profile'")
            ),
            required = listOf("welcomeTitle", "welcomeDesc", "getStartedText", "alreadyHaveProfileText")
        )

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(prompt)))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.2f,
                responseMimeType = "application/json",
                responseSchema = schema
            ),
            systemInstruction = Content(parts = listOf(Part("You are a professional software localization engine that translates UI strings beautifully and returns structured JSON responses.")))
        )

        return try {
            val response = service.translateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                moshi.adapter(TranslationResult::class.java).fromJson(jsonText)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun translateText(text: String, targetLanguage: String): String? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return null
        }

        val prompt = "Translate the following text into the language \"$targetLanguage\". Return ONLY the direct translation of the text. Do not add any conversational words, quotes, introductions, or explanations.\n\nText to translate:\n$text"

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(prompt)))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.2f
            ),
            systemInstruction = Content(parts = listOf(Part("You are a professional software localization system that translates texts perfectly and returns ONLY the final translated string.")))
        )

        return try {
            val response = service.translateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
