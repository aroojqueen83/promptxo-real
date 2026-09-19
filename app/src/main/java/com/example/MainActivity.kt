package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import coil.Coil
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.promptxo.ui.MainScreen
import com.example.promptxo.ui.PromptXoViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PromptXoDarkBg

class MainActivity : ComponentActivity() {
    private val viewModel: PromptXoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configure optimized Coil ImageLoader for instant caching and fast loading
        try {
            val imageLoader = ImageLoader.Builder(this)
                .memoryCache {
                    MemoryCache.Builder(this)
                        .maxSizePercent(0.35)
                        .build()
                }
                .diskCache {
                    DiskCache.Builder()
                        .directory(cacheDir.resolve("image_cache"))
                        .maxSizeBytes(250L * 1024 * 1024)
                        .build()
                }
                .respectCacheHeaders(false)
                .allowHardware(true)
                .crossfade(true)
                .build()
            Coil.setImageLoader(imageLoader)
        } catch (e: Exception) {
            // Fallback to default loader if error
        }

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = PromptXoDarkBg
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}
