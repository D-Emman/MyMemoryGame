package com.example.memorygame

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.adapters.SelectedImagesAdapter

class CreateActivity : AppCompatActivity() {

    // Set how many images are required for your board (adjust as needed)
    private var numImagesRequired = 6

    private lateinit var etGameName: EditText
    private lateinit var btnPickImages: Button
    private lateinit var btnSearchCloud: Button
    private lateinit var btnSaveGame: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SelectedImagesAdapter

    private val imageUris = mutableListOf<Uri>()

    // Pick multiple local images
    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            if (!uris.isNullOrEmpty()) {
                // Cap to required number
                val remaining = numImagesRequired - imageUris.size
                val toAdd = if (remaining > 0) uris.take(remaining) else emptyList()
                imageUris.addAll(toAdd)
                adapter.notifyDataSetChanged()
                updateSaveState()
            }
        }

    // From SearchActivity -> returns public URLs (strings)
    private val searchLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selected = result.data?.getStringArrayListExtra("SELECTED_IMAGE_URLS")
                selected?.let { list ->
                    val remaining = numImagesRequired - imageUris.size
                    val toAdd = if (remaining > 0) list.take(remaining) else emptyList()
                    imageUris.addAll(toAdd.map { Uri.parse(it) })
                    adapter.notifyDataSetChanged()
                    updateSaveState()
                }
            }
        }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        etGameName = findViewById(R.id.etGameName)
        btnPickImages = findViewById(R.id.btnPickImages)
        btnSearchCloud = findViewById(R.id.btnSearchCloud)
        btnSaveGame = findViewById(R.id.btnSaveGame)
        recyclerView = findViewById(R.id.recyclerViewSelectedImages)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        adapter = SelectedImagesAdapter(imageUris) { position: Int ->
            if (position in imageUris.indices) {
                imageUris.removeAt(position)
                adapter.notifyItemRemoved(position)
                updateSaveState()
            }
        }
        recyclerView.adapter = adapter

        updateSaveState()

        btnPickImages.setOnClickListener {
            pickImagesLauncher.launch("image/*")
        }

        btnSearchCloud.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            searchLauncher.launch(intent)
        }

        btnSaveGame.setOnClickListener {
            val gameName = etGameName.text.toString().trim()
            if (gameName.isEmpty()) {
                Toast.makeText(this, "Enter a game name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (imageUris.size != numImagesRequired) {
                Toast.makeText(this, "Select exactly $numImagesRequired images", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // TODO: Upload to Supabase / save Firestore as you already implemented
            Toast.makeText(this, "Game \"$gameName\" created successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateSaveState() {
        supportActionBar?.title = "Choose pics (${imageUris.size}/$numImagesRequired)"
        btnSaveGame.isEnabled = imageUris.size == numImagesRequired
    }
}
