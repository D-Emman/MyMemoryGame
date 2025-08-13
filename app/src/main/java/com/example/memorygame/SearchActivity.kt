package com.example.memorygame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.memorygame.network.SupabaseStorage
import com.example.memorygame.utils.SupabaseStorage
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity(), SearchAdapter.OnImageCheckedListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnConfirmSelection: Button

    private lateinit var searchAdapter: SearchAdapter
    private val searchResults = mutableListOf<String>()
    private val selectedUrls = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerViewSearchResults)
        progressBar = findViewById(R.id.progressBar)
        btnConfirmSelection = findViewById(R.id.btnConfirmSelection)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        searchAdapter = SearchAdapter(searchResults, this)
        recyclerView.adapter = searchAdapter

        btnConfirmSelection.setOnClickListener {
            if (selectedUrls.isEmpty()) {
                Toast.makeText(this, "Select at least one image", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent()
                intent.putStringArrayListExtra("SELECTED_IMAGE_URLS", ArrayList(selectedUrls))
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }

        loadSearchResults()
    }

    private fun loadSearchResults() {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bucket = SupabaseStorage.client.storage.from("custom")
                val fileObjects = bucket.list()

                searchResults.clear()
                searchResults.addAll(fileObjects.map { bucket.publicUrl(it.name) })

                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    searchAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@SearchActivity, "Error loading results", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onImageChecked(url: String, isChecked: Boolean) {
        if (isChecked) selectedUrls.add(url) else selectedUrls.remove(url)
    }
}
