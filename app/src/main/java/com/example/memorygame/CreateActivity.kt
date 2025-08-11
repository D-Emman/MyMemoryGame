package com.example.memorygame

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.memorygame.models.BoardSize
import com.example.memorygame.utils.BitmapScaler
import com.example.memorygame.utils.EXTRA_BOARD_SIZE
import com.example.memorygame.utils.SupabaseStorage
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import io.github.jan.supabase.storage.UploadData

import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.math.log

class CreateActivity : AppCompatActivity() {

    private lateinit var btnSearchGames: Button

    private val photoPickerLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            // Display or use the selected image
            findViewById<ImageView>(R.id.ivCustomView)
                .setImageURI(uri)
        } else {
            Toast.makeText(this, "No photo selected", Toast.LENGTH_SHORT).show()
        }
    }
    companion object{
        private const val TAG = "CreateActivity"
        private const val PICK_PHOTOS_CODE = 312
        private const val READ_EXTERNAL_PHOTOS_CODE = 512
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        private const val READ_PHOTOS_PERMISSION = Manifest.permission.READ_MEDIA_IMAGES
        private const val MIN_GAME_LENGTH = 3
        private const val MAX_GAME_LENGTH = 14
    }

    private lateinit var rvImagePicker: RecyclerView
    private lateinit var etGameName: EditText
    private lateinit var btnSave: Button

    private lateinit var adapter: ImagePickerAdapter
    private lateinit var boardSize: BoardSize
    private var numImagesRequired = -1
    private val chosenImageUris = mutableListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        SupabaseStorage.init()





        // trail block
//        val pickBtn = findViewById<ImageView>(R.id.rvImagePicker)
//        pickBtn.setOnClickListener {
//            photoPickerLauncher.launch(
//                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
//            )
//        }


        setContentView(R.layout.activity_create)

        rvImagePicker = findViewById(R.id.rvImagePicker)
        etGameName = findViewById(R.id.etGameName)
        btnSave = findViewById(R.id.btnSave)

        // Link the button from XML
        btnSearchGames = findViewById(R.id.btnSearchGames)


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        boardSize = intent.getSerializableExtra(EXTRA_BOARD_SIZE) as BoardSize
        numImagesRequired = boardSize.getNumPairs()
        supportActionBar?.title = "choose pics (0/ $numImagesRequired)"


        // Handle click to go to SearchActivity
        btnSearchGames.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        btnSave.setOnClickListener{
            saveDataToFirebase()
        }

        etGameName.filters = arrayOf(InputFilter.LengthFilter(MAX_GAME_LENGTH))
        etGameName.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun onTextChanged(
                p0: CharSequence?,
                p1: Int,
                p2: Int,
                p3: Int
            ) {

            }

            override fun afterTextChanged(p0: Editable?) {
                btnSave.isEnabled = shouldEnableSaveButton()
            }

        })

        adapter = ImagePickerAdapter(this, chosenImageUris, boardSize,
            object: ImagePickerAdapter.ImageClickListener{
                @RequiresApi(Build.VERSION_CODES.TIRAMISU)
                override fun onPlaceholderClicked() {
                    if (isPermissionGranted(this@CreateActivity, READ_PHOTOS_PERMISSION)) {

                        // trail block
                        val pickBtn = findViewById<RecyclerView>(R.id.rvImagePicker)
                        pickBtn.setOnClickListener {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }


                        launchIntentForPhotos()
                        } else{
                            requestPermission(this@CreateActivity,READ_PHOTOS_PERMISSION, READ_EXTERNAL_PHOTOS_CODE)
                        }

                }



            } )
        rvImagePicker.adapter = adapter
        rvImagePicker.setHasFixedSize(true)
        rvImagePicker.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }

    

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        if(requestCode == READ_EXTERNAL_PHOTOS_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                launchIntentForPhotos()
            } else{
                Toast.makeText(this, "In order to create a custom game, you need to provide access to your photos", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean{
        if (item.itemId == android.R.id.home){
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode != PICK_PHOTOS_CODE || resultCode != RESULT_OK || data ==null){
           Log.w(TAG, "Did you not get data back from the launched activity, user likely canceled flow")
            return
        }
        val selectedUri:Uri? = data.data
        val clipData: ClipData? = data.clipData
        if(clipData != null){
            Log.i(TAG, "clipData numImages ${clipData.itemCount}: $clipData")
            for(i in 0 until clipData.itemCount) {
                val clipItem = clipData.getItemAt(i)
                if(chosenImageUris.size < numImagesRequired){
                   chosenImageUris.add(clipItem.uri)
                }
            }
        } else if (selectedUri != null){
            Log.i(TAG, "data: $selectedUri")
            chosenImageUris.add(selectedUri)
        }
        adapter.notifyDataSetChanged()
        supportActionBar?.title = "Choose pics (${chosenImageUris.size}/ $numImagesRequired)"
        btnSave.isEnabled = shouldEnableSaveButton()
    }

//    private fun saveDataToFirebase() {
//        Log.i(TAG, "saveDataToFirebase")
//        for ((index, photoUri) in chosenImageUris.withIndex()){
//            val imageByteArray = getImageByteArray(photoUri)
//        }



        private fun saveDataToFirebase() {
            if (chosenImageUris.size != numImagesRequired || etGameName.text.isBlank()) {
                Toast.makeText(this, "Select enough images and enter a game name", Toast.LENGTH_SHORT).show()
                return
            }

            val gameName = etGameName.text.toString().trim()
            val imageUrls = mutableListOf<String>()

            Thread {
                try {
                    for ((index, uri) in chosenImageUris.withIndex()) {
                        val imageBytes = getImageByteArray(uri)
                        val fileName = "game_${gameName}_${System.currentTimeMillis()}_$index.jpg"

                        lifecycleScope.launch {
                            val result = SupabaseStorage.client.storage.from("custom")


                            result.upload(
                                fileName,
                                imageBytes
                            )
                            {
                                upsert = false
                            }


                            val publicUrl = SupabaseStorage.client.storage
                                .from("custom")
                                .publicUrl(fileName)

                            imageUrls.add(publicUrl)
                            Log.i(TAG, "Uploaded $fileName → $publicUrl")
                        }

                        //lifecyclescope end
                    }

                    // Save to Firestore
                    val db = Firebase.firestore
                    val gameData = hashMapOf(
                        "name" to gameName,
                        "images" to imageUrls,
                        "boardSize" to boardSize.name,
                        "createdAt" to System.currentTimeMillis()
                    )

                    db.collection("custom").document(gameName).set(gameData)
                        .addOnSuccessListener {
                            runOnUiThread {
                                Toast.makeText(this, "Game saved to cloud!", Toast.LENGTH_SHORT).show()

                                // Launch CustomGameActivity with fresh images
                                val intent = Intent(this, CustomGameActivity::class.java).apply {
                                    putExtra("EXTRA_GAME_NAME", gameName)
                                    putExtra("EXTRA_BOARD_SIZE", boardSize)
                                    putStringArrayListExtra("EXTRA_IMAGE_URLS", ArrayList(imageUrls))
                                }
                                startActivity(intent)
                                finish()
                            }
                        }
                        .addOnFailureListener {
                            runOnUiThread {
                                Toast.makeText(this, "Failed to save metadata: ${it.message}", Toast.LENGTH_LONG).show()
                            }
                        }

                } catch (e: Exception) {
                    e.printStackTrace()
                    runOnUiThread {
                        Toast.makeText(this, "Upload error: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }


   // }

    private fun getImageByteArray(photoUri: Uri): ByteArray {
        val originalBitmap = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            val source = ImageDecoder.createSource(contentResolver, photoUri)
            ImageDecoder.decodeBitmap(source)
        } else{
            MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
        }
        Log.i(TAG, "Original width ${originalBitmap.width} and height ${originalBitmap.height}")
        val scaledBitmap = BitmapScaler.scaleToFitHeight(originalBitmap, 250)
        Log.i(TAG, "Scaled width ${scaledBitmap.width} and height ${scaledBitmap.height}")
        val byteOutputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteOutputStream)
        return byteOutputStream.toByteArray()
    }

    private fun shouldEnableSaveButton(): Boolean {
        //check
        if( chosenImageUris.size != numImagesRequired){
            return false
        }
        if( etGameName.text.isBlank() || etGameName.text.length < MIN_GAME_LENGTH){
            return false
        }
        return true
    }

    private fun launchIntentForPhotos() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(Intent.createChooser(intent, "Choose pics"), PICK_PHOTOS_CODE)

    }




}