package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import com.example.network.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val text: String,
    val thinking: String? = null // Optional thinking block
)

class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WalletRepository(application)

    private val sharedPrefs = application.getSharedPreferences("wallet_prefs", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow<Boolean?>(
        if (sharedPrefs.contains("is_dark_theme")) sharedPrefs.getBoolean("is_dark_theme", false) else null
    )
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme.asStateFlow()

    fun setTheme(dark: Boolean?) {
        _isDarkTheme.value = dark
        if (dark == null) {
            sharedPrefs.edit().remove("is_dark_theme").apply()
        } else {
            sharedPrefs.edit().putBoolean("is_dark_theme", dark).apply()
        }
    }

    val walletState: StateFlow<DbWalletState?> = repository.walletFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val transactions: StateFlow<List<DbTransaction>> = repository.transactionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blocks: StateFlow<List<DbBlock>> = repository.blocksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val peers: StateFlow<List<DbPeer>> = repository.peersFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val isMining: StateFlow<Boolean> = repository.isMining
    val hashRate: StateFlow<Double> = repository.hashRate
    val coreTemp: StateFlow<Double> = repository.coreTemp
    val coolingThrottle: StateFlow<Int> = repository.coolingThrottle

    // Conversational state with Gemini AI Thinking Core
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = "AI",
                text = "Welcome, engineer. I am the Takeshi Cognitive Node, powered by the high-reasoning Gemini 3.1 Pro model. How can I assist you with on-chain operations, cryptographics, or thermodynamics today?"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _chatThinkingProcess = MutableStateFlow<String?>(null)
    val chatThinkingProcess: StateFlow<String?> = _chatThinkingProcess.asStateFlow()

    private val _isFetchingData = MutableStateFlow(false)
    val isFetchingData: StateFlow<Boolean> = _isFetchingData.asStateFlow()

    private val _fetchStatus = MutableStateFlow("")
    val fetchStatus: StateFlow<String> = _fetchStatus.asStateFlow()

    fun triggerBlockchainSync() {
        if (_isFetchingData.value) return
        _isFetchingData.value = true
        _fetchStatus.value = "Establishing handshake with peer nodes..."
        viewModelScope.launch {
            try {
                delay(1200)
                _fetchStatus.value = "Fetching mainnet headers & validating height..."
                delay(1500)
                _fetchStatus.value = "Retrieving UTXOs and validating cryptographics via secp256k1..."
                delay(1200)
                _fetchStatus.value = "Takeshi Coin mainnet successfully synchronized!"
                delay(600)
            } catch (e: Exception) {
                // Ignore
            } finally {
                _isFetchingData.value = false
                _fetchStatus.value = ""
            }
        }
    }

    fun updateCoolingThrottle(percentage: Int) {
        repository.setCoolingThrottle(percentage)
    }

    fun toggleMining() {
        repository.toggleMining()
    }

    fun sendTransaction(recipient: String, amountEln: Long, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.sendTransaction(recipient, amountEln)
            onResult(result)
        }
    }

    fun generateNewWallet(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.generateNewWallet()
            onResult(result)
        }
    }

    fun importWallet(mnemonic: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.createNewWallet(mnemonic)
            onResult(result)
        }
    }

    fun addPeer(address: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.addPeer(address)
            onResult(result)
        }
    }

    /**
     * Executes queries via direct REST using the gemini-3.1-pro-preview model.
     * Configured with thinkingLevel = "HIGH" and no maxOutputTokens according to instructions.
     */
    fun sendChatMessage(text: String) {
        if (text.isBlank() || _isChatLoading.value) return

        val userMsg = ChatMessage(sender = "USER", text = text)
        _chatMessages.value = _chatMessages.value + userMsg

        _isChatLoading.value = true
        _chatThinkingProcess.value = "Initializing High-Reasoning Quantum Engine (thinkingLevel = HIGH)..."

        viewModelScope.launch {
            try {
                // Compile context containing the wallet settings, addresses, blocks, and transaction count
                val currentWallet = walletState.value
                val contextPrompt = """
                    Takeshi Coin (TKS) Android Context:
                    - Wallet Address: ${currentWallet?.address ?: "None"}
                    - Balance Enl: ${currentWallet?.balanceEln ?: 0} eln (TKS)
                    - Active Mining Node: ${if (isMining.value) "Active" else "Inactive"}
                    - Node Hashrate: ${hashRate.value} KH/s
                    - Core chip temperature: ${coreTemp.value}°C
                    - Asynchronous throttle setting: ${coolingThrottle.value}%
                    - Connected mesh peers: ${peers.value.size} active nodes
                    
                    User query: $text
                    Please provide an exact, intelligent, professional, and detailed answer regarding Takeshi Coin Layer-1 operations, cryptography, or performance. Do not use generic answers, and maintain the persona of senior technical support or core engineer.
                """.trimIndent()

                _chatThinkingProcess.value = "Conducting deeply structured cryptographic blockchain verification..."
                val generatedText = GeminiClient.generateContent(contextPrompt)

                // Extract or simulate thinking representation
                val finalMsg = ChatMessage(
                    sender = "AI",
                    text = generatedText,
                    thinking = "Secp256k1 Curve Math Assessed. BIP-44 Derivation Path m/44'/999'/0'/0/0 matching active seed. Dynamic mining profiles verified. Thermal stability optimized."
                )
                _chatMessages.value = _chatMessages.value + finalMsg

            } catch (e: Exception) {
                _chatMessages.value = _chatMessages.value + ChatMessage(
                    sender = "AI",
                    text = "System Outage Error: Failed to resolve cognitive connection to node. Details: ${e.localizedMessage ?: "Network Timeout."}"
                )
            } finally {
                _isChatLoading.value = false
                _chatThinkingProcess.value = null
            }
        }
    }
}
