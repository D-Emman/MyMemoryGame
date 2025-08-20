package com.example.memorygame
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.utils.SupabaseStorage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomGameActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FROM_SEARCH = "extra_from_search"
        const val EXTRA_GAME_NAME = "extra_game_name"
        private const val TAG = "CustomGameActivity"
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var gameGrid: RecyclerView
    private val matchedCards = mutableSetOf<Int>()
    private val flippedCards = mutableListOf<Int>()
    private lateinit var gameAdapter: MemoryGameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_game)

        progressBar = findViewById(R.id.progressLoadingGame)
        gameGrid = findViewById(R.id.rvGameGrid)

        SupabaseStorage.init()

        val fromSearch = intent.getBooleanExtra(EXTRA_FROM_SEARCH, false)
        val selectedImages = intent.getStringArrayListExtra("SELECTED_IMAGES") ?: arrayListOf()

        if (fromSearch && selectedImages.isNotEmpty()) {
            loadMultipleSupabaseImages(selectedImages)
        }
    }

    private fun loadMultipleSupabaseImages(gameNames: List<String>) {
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val urls = withContext(Dispatchers.IO) {
                    val bucket = SupabaseStorage.client.storage.from("custom")
                    val deferred = gameNames.map { name ->
                        async { bucket.publicUrl(name) }
                    }
                    deferred.awaitAll()
                }

                progressBar.visibility = View.GONE

                if (urls.isEmpty()) {
                    Toast.makeText(this@CustomGameActivity, "No images found", Toast.LENGTH_SHORT).show()
                } else {
                    startMemoryGame(urls)
                }

            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                e.printStackTrace()
                Toast.makeText(this@CustomGameActivity, "Error loading images: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun startMemoryGame(images: List<String>) {
        val pairedImages = (images + images).shuffled()

        gameGrid.layoutManager = GridLayoutManager(this, 4)
        gameAdapter = MemoryGameAdapter(pairedImages) { position ->
            onCardClicked(position, pairedImages)
        }
        gameGrid.adapter = gameAdapter
    }

    private fun onCardClicked(position: Int, pairedImages: List<String>) {
        if (matchedCards.contains(position) || flippedCards.contains(position)) return

        flippedCards.add(position)
        gameAdapter.revealCard(position)

        if (flippedCards.size == 2) {
            val firstPos = flippedCards[0]
            val secondPos = flippedCards[1]

            Handler(Looper.getMainLooper()).postDelayed({
                if (pairedImages[firstPos] == pairedImages[secondPos]) {
                    matchedCards.add(firstPos)
                    matchedCards.add(secondPos)
                } else {
                    gameAdapter.hideCard(firstPos)
                    gameAdapter.hideCard(secondPos)
                }
                flippedCards.clear()

                if (matchedCards.size == pairedImages.size) {
                    Toast.makeText(this, "🎉 You Win!", Toast.LENGTH_LONG).show()
                }
                
            }, 1000)
        }
    }
}
