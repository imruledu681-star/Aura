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

@JsonClass(generateAdapter = true)
data class CaptionCue(
    val startSecond: Int,
    val endSecond: Int,
    val text: String
)

@JsonClass(generateAdapter = true)
data class CaptionResponse(
    val captions: List<CaptionCue>
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

    suspend fun generateAutoCaptions(
        title: String,
        description: String,
        tags: String,
        authorName: String,
        durationSeconds: Int = 60
    ): List<CaptionCue>? {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return null
        }

        val prompt = """
            Generate realistic, contextually synchronized closed captions/subtitles for a video with the following metadata:
            Title: "$title"
            Description: "$description"
            Tags: "$tags"
            Creator: "$authorName"
            
            Provide about 8 to 15 chronological caption entries spanning from 0 seconds up to $durationSeconds seconds.
            Ensure the captions are highly relevant to the video content, mood, and genre. For example:
            - If it's a WWE or wrestling video, include exciting commentary, referee actions, crowd chants, and audio cues like [crowd cheers], [slam].
            - If it's recipe/cooking, technology, music, comedy, gaming, vlogs, etc., match that mood perfectly.
            
            CRITICAL LANGUAGE REQUIREMENT:
            - Auto-detect the primary language used in the Title, Description, or Tags.
            - Generate the caption subtitles in that EXACT same detected language (e.g., Bengali, English, Hindi, Spanish, Arabic, German, French, etc.).
            - If the video is mostly in Bengali, the captions must be beautifully written in natural Bengali.
            - If the video is in Hindi, the captions must be beautifully written in Hindi.
            - Ensure any background audio descriptions like [bg music playing] or [crowd cheering] are translated to that matching language too!
            
            Each caption must have startSecond, endSecond, and text.
            The output must strictly conform to the JSON schema.
        """.trimIndent()

        val schema = ResponseSchema(
            type = "OBJECT",
            properties = mapOf(
                "captions" to SchemaProperty("ARRAY", "The list of chronological subtitle captions")
            ),
            required = listOf("captions")
        )

        val request = GenerateContentRequest(
            contents = listOf(
                Content(parts = listOf(Part(prompt)))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.4f,
                responseMimeType = "application/json",
                responseSchema = schema
            ),
            systemInstruction = Content(parts = listOf(Part("You are a state-of-the-art multilingual video speech recognition and AI subtitle generator. You detect the language of the metadata, translate background sounds and spoken content accurately to that exact language, and return beautiful timed captions strictly matching the JSON schema.")))
        )

        return try {
            val response = service.translateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (jsonText != null) {
                val adapter = moshi.adapter(CaptionResponse::class.java)
                adapter.fromJson(jsonText)?.captions
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun generateFallbackCaptions(
        title: String,
        tags: String,
        authorName: String,
        durationSeconds: Int = 60
    ): List<CaptionCue> {
        val titleUpper = title.uppercase()
        val tagsUpper = tags.uppercase()
        val isWrestling = titleUpper.contains("WWE") || titleUpper.contains("WRESTLING") || tagsUpper.contains("WWE") || tagsUpper.contains("WRESTLE")
        val isFood = titleUpper.contains("RECIPE") || titleUpper.contains("COOK") || titleUpper.contains("FOOD") || titleUpper.contains("KITCHEN") || tagsUpper.contains("FOOD")
        val isTech = titleUpper.contains("TECH") || titleUpper.contains("UNBOX") || titleUpper.contains("PHONE") || titleUpper.contains("GADGET") || tagsUpper.contains("TECH")

        val duration = if (durationSeconds <= 0) 60 else durationSeconds
        val step = duration / 9
        
        return when {
            isWrestling -> listOf(
                CaptionCue(0, step, "[উত্তেজনাপূর্ণ আবহ সঙ্গীত বাজছে] 🎵"),
                CaptionCue(step, step * 2, "সবাইকে WWE-তে স্বাগতম! আজকের ম্যাচটি অত্যন্ত রোমাঞ্চকর হতে চলেছে।"),
                CaptionCue(step * 2, step * 3, "[দর্শকদের তুমুল চিৎকার ও করতালি] 📣"),
                CaptionCue(step * 3, step * 4, "$authorName নিয়ে আসছে অবিস্মরণীয় কুস্তিগীরদের লড়াইয়ের ঝলক!"),
                CaptionCue(step * 4, step * 5, "রিংয়ের ভেতরে উত্তেজনা তুঙ্গে! একের পর এক আক্রমণ শুরু হয়েছে।"),
                CaptionCue(step * 5, step * 6, "[বড় ধরণের স্ল্যাম এবং রেফারির কাউন্টডাউন শুরু] ⚡"),
                CaptionCue(step * 6, step * 7, "দর্শক যেন তাদের চোখ বিশ্বাস করতে পারছে না! অসাধারণ পারফরম্যান্স।"),
                CaptionCue(step * 7, step * 8, "কুস্তি এবং স্পোর্টস এন্টারটেইনমেন্টের অনন্য উৎসব আজ এডিট ফিল্ডে!"),
                CaptionCue(step * 8, duration, "Aura-তে WWE Together দেখার জন্য ধন্যবাদ! লাইক ও ফলো করুন। 💜")
            )
            isFood -> listOf(
                CaptionCue(0, step, "[শান্ত ও মৃদু কিচেন মিউজিক বাজছে] 🍳"),
                CaptionCue(step, step * 2, "সবাইকে স্বাগতম! আজকে আমরা তৈরি করতে যাচ্ছি চমৎকার একটি রেসিপি।"),
                CaptionCue(step * 2, step * 3, "প্রথমে আপনার ফ্রেশ সবজি এবং উপকরণগুলো সুন্দর করে কেটে নিন।"),
                CaptionCue(step * 3, step * 4, "[গরম তেলের মৃদু ফুটন্ত শব্দ শুরু] 🔥"),
                CaptionCue(step * 4, step * 5, "উপকরণগুলো কড়াইয়ে দিয়ে সুগন্ধি ছড়ানো পর্যন্ত ভালোভাবে নাড়ুন।"),
                CaptionCue(step * 5, step * 6, "রান্নার কালারটা দেখেই বোঝা যাচ্ছে অত্যন্ত সুস্বাদু এবং পুষ্টিকর হবে!"),
                CaptionCue(step * 6, step * 7, "এক চিমটি লবণ এবং ধনেপাতা বা পুদিনা পাতা দিয়ে একটু গার্নিশ করে নিন।"),
                CaptionCue(step * 7, step * 8, "অসাধারণ সুবাস বের হচ্ছে পুরো রান্নাঘর জুড়ে।"),
                CaptionCue(step * 8, duration, "রান্না সম্পন্ন হয়েছে! পরিবেশনের জন্য একদম রেডি। শুভকামনা! ✨")
            )
            isTech -> listOf(
                CaptionCue(0, step, "[আধুনিক ইলেকট্রনিক বিট বাজছে] 💻"),
                CaptionCue(step, step * 2, "হ্যালো বন্ধুরা! আজকে আমরা আনবক্সিং এবং রিভিউ করতে যাচ্ছি এই ডিভাইসটি।"),
                CaptionCue(step * 2, step * 3, "ডিভাইসটির ডিজাইন চমৎকার এবং মেটাল বডিটি হাতে দারুণ গ্রিপ দেয়।"),
                CaptionCue(step * 3, step * 4, "[প্লাস্টিক প্রটেক্টর খোলার সুন্দর শব্দ] ✨"),
                CaptionCue(step * 4, step * 5, "চলুন ডিভাইসটি অন করা যাক এবং এর ডিসপ্লে ও রিফ্রেশ রেট চেক করি।"),
                CaptionCue(step * 5, step * 6, "কালারগুলো অসাধারণ প্রাণবন্ত এবং কনট্রাস্ট অত্যন্ত নিখুঁত ও চমৎকার।"),
                CaptionCue(step * 6, step * 7, "প্রসেসিং স্পীড এবং গেমিং পারফরমেন্স টেস্টে এটি অসাধারণ ফলাফল দিয়েছে।"),
                CaptionCue(step * 7, step * 8, "এই বাজেটে আপনার জন্য এটি সেরা আপগ্রেড হতে পারে কি না বলুন?"),
                CaptionCue(step * 8, duration, "আপনার মতামত নিচে কমেন্ট বক্সে জানান এবং Aura-তে সাবস্ক্রাইব করুন! 💜")
            )
            else -> listOf(
                CaptionCue(0, step, "[আবহ সঙ্গীত] Aura Presenting: \"$title\" 🎬💜"),
                CaptionCue(step, step * 2, "$authorName এর সৃষ্টিশীল মন থেকে নির্মিত একটি চমৎকার ভিডিও উপস্থাপনা।"),
                CaptionCue(step * 2, step * 3, "[ভিডিওর মূল দৃশ্যের সূচনা ও আবহ শব্দ] 🌟"),
                CaptionCue(step * 3, step * 4, "প্রতিটি ফ্রেম এবং দৃশ্যের পেছনে রয়েছে কঠোর পরিশ্রম ও দারুণ পরিকল্পনা।"),
                CaptionCue(step * 4, step * 5, "Aura-তে ভিডিওটি উচ্চ রেজুলেশনে দেখার অভিজ্ঞতা উপভোগ করুন।"),
                CaptionCue(step * 5, step * 6, "[অটো ভয়েস রেকগনিশন ও ট্রান্সক্রিপশন সক্রিয়] 🛡️"),
                CaptionCue(step * 6, step * 7, "বিশ্বজুড়ে ক্রিয়েটরদের কাজকে একত্রিত করে এক নতুন কমিউনিটি গড়ছে Aura।"),
                CaptionCue(step * 7, step * 8, "ভিডিওটি ভালো লাগলে লাইক, কমেন্ট এবং বন্ধুদের সাথে শেয়ার করুন!"),
                CaptionCue(step * 8, duration, "আমাদের সাথে থাকার জন্য ধন্যবাদ। Aura 2026 - সর্বস্বত্ব সংরক্ষিত।")
            )
        }
    }
}
