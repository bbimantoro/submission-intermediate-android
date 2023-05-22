package com.academy.bangkit.mystoryapp.ui.story.post

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.UserPreferences
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.databinding.ActivityPostStoryBinding
import com.academy.bangkit.mystoryapp.reduceFileImage
import com.academy.bangkit.mystoryapp.rotateFile
import com.academy.bangkit.mystoryapp.ui.UserViewModelFactory
import com.academy.bangkit.mystoryapp.ui.camera.CameraActivity
import com.academy.bangkit.mystoryapp.uriToFile
import com.academy.bangkit.mystoryapp.data.Result
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class PostStoryActivity : AppCompatActivity() {

    private val postStoryViewModel by viewModels<PostStoryViewModel> {
        UserViewModelFactory(UserPreferences.getInstance(dataStore))
    }

    private lateinit var binding: ActivityPostStoryBinding

    private var getFile: File? = null
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, "Tidak mendapatkan permission", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.previewIv.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedImg = it.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@PostStoryActivity)
                getFile = myFile
                binding.previewIv.setImageURI(uri)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupAction()
    }

    private fun setupAction() {

        with(binding) {
            cameraBtn.setOnClickListener {
                startCamera()
            }

            galleryBtn.setOnClickListener {
                startGallery()
            }

            uploadBtn.setOnClickListener {
                setupUpload()
            }
        }
    }

    private fun setupUpload() {

    }

    private fun uploadImage(token: String) {
        val description = binding.descriptionEdt.text.toString()

        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            postStoryViewModel.addNewStory(token, file, description)
            postStoryViewModel.result.observe(this) { result ->
                observerPostStory(result)
            }
        } else {
            Toast.makeText(this, getString(R.string.err_image_field), Toast.LENGTH_SHORT).show()

            if (description.isEmpty()) {
                Toast.makeText(this, getString(R.string.err_description_field), Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun observerPostStory(result: Result<CommonResponse>) {
        when (result) {
            is Result.Loading -> {
                binding.progressbar.visibility = View.VISIBLE
            }

            is Result.Success -> {
                binding.progressbar.visibility = View.GONE
                Toast.makeText(
                    this,
                    getString(R.string.post_story_success_message),
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent()
            }

            is Result.Error -> {
                binding.progressbar.visibility = View.GONE
                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun startCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCamera.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a picture")
        launcherIntentGallery.launch(chooser)
    }


    companion object {
        const val CAMERA_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}