package com.example.memorygame

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.utils.SupabaseStorage
//import com.example.memorygame.SearchAdapter
import com.example.yourapp.SearchAdapter
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var recyclerView: RecyclerView
//    private lateinit var progressBar: ProgressBar
    private lateinit var searchAdapter: SearchAdapter
    private lateinit var supabase: SupabaseClient

    private val gameList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Initialize Supabase client
        supabase = SupabaseStorage.client

        searchEditText = findViewById(R.id.etSearchGame)
        recyclerView = findViewById(R.id.rvSearchResults)
//        progressBar = findViewById(R.id.progressBarSearch)

        searchAdapter = SearchAdapter(gameList) { selectedGame ->
            val intent = Intent(this, CustomGameActivity::class.java)
            intent.putExtra(CustomGameActivity.EXTRA_FROM_SEARCH, true)
            intent.putExtra(CustomGameActivity.EXTRA_GAME_NAME, selectedGame)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = searchAdapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(query: CharSequence?, start: Int, before: Int, count: Int) {
                if (query.isNullOrBlank()) {
                    gameList.clear()
                    searchAdapter.notifyDataSetChanged()
                } else {
                    searchGames(query.toString())
                }
            }
        })
    }

    private fun searchGames(query: String) {
//        progressBar.visibility = View.VISIBLE
        gameList.clear()

        // Example search logic from Supabase storage
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bucket = supabase.storage.from("custom")
                val files = bucket.list() // returns list of StorageItem

                val matchingGames = files
                    .map { it.name }
                    .filter { it.contains(query, ignoreCase = true) }

                withContext(Dispatchers.Main) {
                    gameList.addAll(matchingGames)
                    searchAdapter.notifyDataSetChanged()
//                    progressBar.visibility = View.GONE
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
//                    progressBar.visibility = View.GONE
                    Toast.makeText(this@SearchActivity, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }}
}
