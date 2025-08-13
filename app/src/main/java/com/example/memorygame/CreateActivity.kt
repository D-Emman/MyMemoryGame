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

class CreateActivity : AppCompatActivity() {

    private lateinit var recyclerViewSelectedImages: RecyclerView
    private lateinit var selectedImagesAdapter: SelectedImagesAdapter
    private val selectedImageUris = mutableListOf<String>() // store URI as String for Glide

    private lateinit var editTextGameName: EditText
    private lateinit var btnPickImages: Button
    private lateinit var btnSaveGame: Button

    private val pickImagesLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
            if (uris.isNotEmpty()) {
                // Append newly picked images to the list
                selectedImageUris.addAll(uris.map { it.toString() })
                selectedImagesAdapter.notifyDataSetChanged()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        editTextGameName = findViewById(R.id.editTextGameName)
        btnPickImages = findViewById(R.id.btnPickImages)
        btnSaveGame = findViewById(R.id.btnSaveGame)
        recyclerViewSelectedImages = findViewById(R.id.recyclerViewSelectedImages)

        selectedImagesAdapter = SelectedImagesAdapter(selectedImageUris)
        recyclerViewSelectedImages.layoutManager = GridLayoutManager(this, 3)
        recyclerViewSelectedImages.adapter = selectedImagesAdapter

        btnPickImages.setOnClickListener {
            pickImagesLauncher.launch("image/*")
        }

        btnSaveGame.setOnClickListener {
            val gameName = editTextGameName.text.toString().trim()

            when {
                gameName.isEmpty() -> {
                    Toast.makeText(this, "Please enter a game name", Toast.LENGTH_SHORT).show()
                }
                selectedImageUris.isEmpty() -> {
                    Toast.makeText(this, "Please select at least one image", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // TODO: Upload images & save game logic to Supabase or local storage
                    Toast.makeText(
                        this,
                        "Game '$gameName' saved with ${selectedImageUris.size} images",
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            }
        }
    }
}
