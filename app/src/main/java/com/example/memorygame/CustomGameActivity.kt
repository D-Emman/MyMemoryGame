package com.example.memorygame

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.memorygame.utils.SupabaseStorage
import com.google.android.material.textview.MaterialTextView
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomGameActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FROM_SEARCH = "extra_from_search"
        const val EXTRA_GAME_NAME = "extra_game_name"
        private const val TAG = "CustomGameActivity"
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var gameGrid: GridLayout
    private lateinit var supabase: SupabaseClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_game)

        progressBar = findViewById(R.id.progressLoadingGame)
        gameGrid = findViewById(R.id.rvGameGrid)

        //my try Init Supabase
        supabase = SupabaseStorage.client

        val fromSearch = intent.getBooleanExtra(EXTRA_FROM_SEARCH, false)
        val gameName = intent.getStringExtra(EXTRA_GAME_NAME)

        if (fromSearch && !gameName.isNullOrEmpty()) {

            // Instant load from search results
            loadGameFromSupabase(gameName)

        } else {
            // Normal flow from CreateActivity
            startNormalGame()
        }
    }

    private fun startNormalGame() {
        Toast.makeText(this, "Starting game in normal mode...", Toast.LENGTH_SHORT).show()
        // Your existing local game setup code goes here
    }

    private fun loadGameFromSupabase(gameName: String) {
        progressBar.visibility = View.VISIBLE
        gameGrid.visibility = View.GONE

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bucket = supabase.storage.from("custom")
                val path = "$gameName/"

                val fileList = bucket.list(path)

                val imageUrls = fileList.map { file ->
                    bucket.publicUrl("$path${file.name}")
                }

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    gameGrid.visibility = View.VISIBLE

                    if (imageUrls.isEmpty()) {
                        Toast.makeText(this@CustomGameActivity, "No images found for $gameName", Toast.LENGTH_SHORT).show()
                        return@withContext
                    }

                    // Load the images into your grid adapter
                    setupGameGrid(imageUrls)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading game from Supabase", e)
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@CustomGameActivity, "Failed to load game", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupGameGrid(imageUrls: List<String>) {
        // TODO: Implement your adapter or game UI logic using the imageUrls
        // For now, just display them in logs
        imageUrls.forEach { Log.d(TAG, "Image: $it") }

        Toast.makeText(this, "Game loaded with ${imageUrls.size} images!", Toast.LENGTH_SHORT).show()
    }
}
