package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ui.WalletApp
import com.example.ui.WalletViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    
    private val viewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Fully support premium full-screen edge-to-edge content
        enableEdgeToEdge()
        
        setContent {
            val isDarkThemeOpt by viewModel.isDarkTheme.collectAsState()
            val darkTheme = isDarkThemeOpt ?: isSystemInDarkTheme()
            MyApplicationTheme(darkTheme = darkTheme) {
                WalletApp(viewModel = viewModel)
            }
        }
    }
}
