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
import com.academy.bangkit.mystoryapp.data.local.datastore.UserPreferences
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.databinding.ActivityPostStoryBinding
import com.academy.bangkit.mystoryapp.utils.reduceFileImage
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.academy.bangkit.mystoryapp.ui.camera.CameraActivity
import com.academy.bangkit.mystoryapp.utils.uriToFile
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.ui.auth.login.LoginActivity
import com.academy.bangkit.mystoryapp.ui.story.main.MainActivity
import com.academy.bangkit.mystoryapp.utils.rotateBitmap
import com.bumptech.glide.Glide
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class PostStoryActivity : AppCompatActivity() {

    private val postStoryViewModel by viewModels<PostStoryViewModel> {
        ViewModelFactory(UserPreferences.getInstance(dataStore))
    }

    private lateinit var postStoryBinding: ActivityPostStoryBinding

    private var getFile: File? = null
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                Toast.makeText(this, getString(R.string.err_permission), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postStoryBinding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(postStoryBinding.root)

        supportActionBar?.title = getString(R.string.label_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        postStoryBinding.apply {
            cameraBtn.setOnClickListener {
                startCamera()
            }

            galleryBtn.setOnClickListener {
                startGallery()
            }
        }

        postStoryViewModel.result.observe(this) { result ->
            observerPostStory(result)
        }

    }

    override fun onResume() {
        super.onResume()
        isValidUserToken()
    }

    private fun isValidUserToken() {
        postStoryViewModel.getToken().observe(this) { token ->
            if (token.isEmpty()) {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            } else {
                setupPostStory("Bearer $token")
            }
        }
    }

    private fun setupPostStory(token: String) {
        postStoryBinding.uploadBtn.setOnClickListener {
            val description = postStoryBinding.descriptionEdt.text.toString()

            if (description.isEmpty()) {
                postStoryBinding.descriptionEdt.error = getString(R.string.err_description_field)
            } else {
                if (getFile != null) {
                    val file = reduceFileImage(getFile as File)
                    postStoryViewModel.addNewStory(token, file, description)
                }
            }
        }
    }

    private fun observerPostStory(result: Result<CommonResponse>) {
        when (result) {
            is Result.Success -> {
                postStoryBinding.progressbar.visibility = View.GONE
                Toast.makeText(
                    this,
                    getString(R.string.post_story_success_message),
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            is Result.Loading -> {
                postStoryBinding.progressbar.visibility = View.VISIBLE
            }

            is Result.Error -> {
                postStoryBinding.progressbar.visibility = View.GONE
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

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == CAMERA_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra(PICTURE_EXTRA, File::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getSerializableExtra(PICTURE_EXTRA)
            } as? File
            val isBackCamera = result.data?.getBooleanExtra(IS_BACK_CAMERA_EXTRA, true) as Boolean

            getFile = myFile

            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path), isBackCamera
            )

            postStoryBinding.previewIv.let {
                Glide.with(this)
                    .load(result)
                    .into(it)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this)
            getFile = myFile

            postStoryBinding.previewIv.let {
                Glide.with(this)
                    .load(myFile)
                    .into(it)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    companion object {
        const val CAMERA_RESULT = 200
        const val PICTURE_EXTRA = "picture_extra"
        const val IS_BACK_CAMERA_EXTRA = "is_back_camera_extra"
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}