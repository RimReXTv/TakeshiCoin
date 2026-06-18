package com.example.network

import com.example.BuildConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * High-performance direct OkHttp client utilizing clean SDK-native JSON parsing.
 * Configured strictly with gemini-3.1-pro-preview and thinkingLevel = HIGH.
 */
object GeminiClient {
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.1-pro-preview:generateContent?key=$apiKey"

        val jsonRequest = JSONObject().apply {
            put("contents", JSONArray().put(
                JSONObject().put("parts", JSONArray().put(
                    JSONObject().put("text", prompt)
                ))
            ))
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.4)
                put("thinkingConfig", JSONObject().apply {
                    put("thinkingLevel", "HIGH")
                })
            })
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(
                    JSONObject().put("text", "You are the Takeshi Coin AI Node. Provide deeply technical blockchain details, with exact equations, cryptographic structures, and real equations when requested. Maintain an engaging, concise, but professional and exact senior engineer communication style.")
                ))
            })
        }

        val body = jsonRequest.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "Error: Cognitive node returned HTTP ${response.code}"
                }
                val bodyStr = response.body?.string() ?: return@withContext "Error: Empty response body"
                val jsonResponse = JSONObject(bodyStr)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            return@withContext parts.getJSONObject(0).optString("text", "No text part found")
                        }
                    }
                }
                "No response text."
            }
        } catch (e: Exception) {
            "Connection failure: ${e.localizedMessage ?: "Timeout connecting to Takeshi Cognitive Cluster."}"
        }
    }
}
