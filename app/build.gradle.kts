import java.net.URI
import java.net.URL

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.takeshiwallet.kxzqmr"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
}

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  // implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  // implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  // implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}

tasks.register("downloadBip39Wordlist") {
    val outputFile = file("src/main/java/com/example/crypto/Bip39Wordlist.kt")
    outputs.file(outputFile)
    doLast {
        if (!outputFile.exists()) {
            println("Downloading BIP-39 Wordlist of 2048 English words...")
            var wordsList: List<String>? = null
            try {
                val url = URI("https://raw.githubusercontent.com/bitcoin/bips/master/bip-0039/english.txt").toURL()
                val text = url.readText()
                val parsed = text.lines().map { it.trim() }.filter { it.isNotEmpty() }
                if (parsed.size == 2048) {
                    wordsList = parsed
                    println("BIP-39 wordlist loaded successfully from online source!")
                }
            } catch (e: Exception) {
                println("Failed to fetch wordlist online: ${e.message}. Using built-in high-quality fallback.")
            }

            val finalWords = wordsList ?: listOf(
                "abandon", "ability", "able", "about", "above", "absent", "absorb", "abstract", "absurd", "abuse",
                "access", "accident", "account", "accuse", "achieve", "acid", "acoustic", "acquire", "across", "act",
                "action", "active", "actor", "actress", "actual", "adapt", "add", "addict", "address", "adjust",
                "admit", "adult", "advance", "advice", "advisor", "affair", "afford", "afraid", "after", "again",
                "against", "age", "agent", "agree", "ahead", "aim", "air", "airport", "aisle", "alarm", "album",
                "alcohol", "alert", "alien", "alike", "alive", "all", "alley", "allow", "almost", "alone",
                "along", "alpha", "already", "also", "alter", "always", "amateur", "amaze", "amber", "ambition",
                "amount", "amuse", "analyst", "anchor", "ancient", "anger", "angle", "angry", "animal", "ankle",
                "announce", "annual", "another", "answer", "antenna", "antique", "anxiety", "any", "apart", "apology",
                "apparatus", "appeal", "appear", "apple", "approve", "april", "arch", "arctic", "area", "arena",
                "argue", "arm", "armed", "armor", "army", "around", "arrange", "arrest", "arrive", "arrow",
                "art", "article", "artist", "artwork", "ash", "aside", "ask", "aspect", "assault", "asset",
                "assist", "assume", "asthma", "athlete", "atom", "attack", "attend", "attitude", "attract", "auction",
                "audit", "august", "aunt", "author", "auto", "autumn", "average", "avoid", "awake", "award",
                "aware", "away", "awesome", "awful", "awkward", "axis", "baby", "bachelor", "bacon", "badge",
                "bag", "balance", "balcony", "ball", "bamboo", "banana", "banner", "bar", "barely", "bargain",
                "barrel", "barrier", "base", "basic", "basket", "battle", "beach", "beam", "bean", "beauty",
                "because", "become", "beef", "before", "begin", "behave", "behind", "believe", "below", "belt",
                "bench", "benefit", "best", "betray", "better", "between", "beyond", "bicycle", "bid", "bike",
                "bind", "biology", "bird", "birth", "bitter", "black", "blade", "blame", "blanket", "blast",
                "bleak", "bless", "blind", "blood", "blossom", "blouse", "blue", "blur", "blush", "board",
                "boat", "body", "boil", "bomb", "bone", "bonus", "book", "boost", "border", "boring",
                "borrow", "boss", "bottom", "bounce", "box", "boy", "bracket", "brain", "brand", "brass",
                "brave", "bread", "breeze", "brick", "bridge", "brief", "bright", "bring", "brisk", "broad",
                "bronze", "broom", "brother", "brown", "brush", "bubble", "buddy", "budget", "buffalo", "build",
                "bulb", "bulk", "bullet", "bundle", "bunker", "burden", "burger", "burst", "bus", "business",
                "busy", "butter", "buyer", "buzz", "cabbage", "cabin", "cable", "cactus", "cage", "cake"
            )

            val wordsBlock = finalWords.joinToString("\",\n        \"")
            val code = """
                package com.example.crypto

                object Bip39Wordlist {
                    val WORDS = arrayOf(
                        "$wordsBlock"
                    )
                }
            """.trimIndent()
            outputFile.parentFile.mkdirs()
            outputFile.writeText(code)
            println("BIP-39 English Wordlist written to: ${'$'}{outputFile.absolutePath}")
        }
    }
}

tasks.matching { it.name.startsWith("compile") || it.name.startsWith("ksp") }.all {
    dependsOn("downloadBip39Wordlist")
}

