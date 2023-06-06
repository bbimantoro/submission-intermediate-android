package com.academy.bangkit.mystoryapp.ui.story.post

import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.network.response.CommonResponse
import com.academy.bangkit.mystoryapp.databinding.ActivityPostStoryBinding
import com.academy.bangkit.mystoryapp.utils.reduceFileImage
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.academy.bangkit.mystoryapp.ui.camera.CameraActivity
import com.academy.bangkit.mystoryapp.utils.uriToFile
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.ui.story.main.MainActivity
import com.academy.bangkit.mystoryapp.utils.rotateBitmap
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import java.io.File
import java.util.concurrent.TimeUnit

class PostStoryActivity : AppCompatActivity() {

    private val viewModel by viewModels<PostStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityPostStoryBinding

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var getFile: File? = null
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.topAppBar.setNavigationOnClickListener {
            moveToMain()
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.apply {
            contentPostStory.cameraBtn.setOnClickListener {
                if (!allPermissionGranted()) {
                    ActivityCompat.requestPermissions(
                        this@PostStoryActivity,
                        REQUIRED_PERMISSIONS,
                        REQUEST_CODE_PERMISSIONS
                    )
                } else {
                    val intent = Intent(this@PostStoryActivity, CameraActivity::class.java)
                    launcherIntentCamera.launch(intent)
                }
            }

            contentPostStory.galleryBtn.setOnClickListener {
                val intent = Intent().apply {
                    action = ACTION_GET_CONTENT
                    type = "image/*"
                }
                val chooser = Intent.createChooser(intent, "Choose a picture")
                launcherIntentGallery.launch(chooser)
            }
        }

        setupPostAction()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionGranted()) {
                showToast(getString(R.string.err_permission))
                finish()
            }
        }
    }

    private fun allPermissionGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupPostAction() {
        binding.contentPostStory.uploadBtn.setOnClickListener {
            val description = binding.contentPostStory.descriptionEdt.text.toString().trim()

            when {
                description.isEmpty() -> {
                    showToast(getString(R.string.err_description_field))
                }

                else -> {
                    getFile?.let { file ->
                        val compressFile = reduceFileImage(file)
                        viewModel.addNewStory(
                            compressFile,
                            description,
                            location
                        )
                    } ?: run {
                        showToast(getString(R.string.err_image_field))
                    }
                }
            }
        }

        binding.contentPostStory.switchGps.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getMyLastLocation()
                createLocationRequest()
            } else {
                location = null
            }
        }

        viewModel.result.observe(this, observerPostStory)
    }

    private val observerPostStory = Observer<Result<CommonResponse>> { result ->
        when (result) {
            is Result.Loading -> {
                binding.contentPostStory.progressbar.visibility = View.VISIBLE
            }

            is Result.Error -> {
                binding.contentPostStory.progressbar.visibility = View.GONE
                showToast(result.error)
            }

            is Result.Success -> {
                binding.contentPostStory.progressbar.visibility = View.GONE

                showToast(result.data.message)

                moveToMain()
            }
        }
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
            val isBackCamera =
                result.data?.getBooleanExtra(IS_BACK_CAMERA_EXTRA, true) as Boolean

            getFile = myFile

            binding.contentPostStory.previewIv.let {
                Glide.with(this)
                    .load(rotateBitmap(BitmapFactory.decodeFile(getFile?.path), isBackCamera))
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

            binding.contentPostStory.previewIv.let {
                Glide.with(this)
                    .load(myFile)
                    .into(it)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permission ->

        when {
            permission[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getMyLastLocation()
            }

            permission[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getMyLastLocation()
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission,
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                this.location = location
                if (location == null) showToast(getString(R.string.err_location))
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private val resolutionLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when (result.resultCode) {
            RESULT_OK -> {
                Log.i(TAG, "onActivityResult: All location settings are satisfied")
            }

            RESULT_CANCELED -> {
                showToast(getString(R.string.err_gps))
            }
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(1)
            maxWaitTime = TimeUnit.SECONDS.toMillis(1)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build())
            .addOnSuccessListener {
                getMyLastLocation()
            }
            .addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        resolutionLauncher.launch(
                            IntentSenderRequest.Builder(exception.resolution).build()
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        showToast(sendEx.message.toString())
                    }
                }
            }
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this@PostStoryActivity, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        const val CAMERA_RESULT = 200
        const val PICTURE_EXTRA = "picture_extra"
        const val IS_BACK_CAMERA_EXTRA = "is_back_camera_extra"
        const val TAG = "PostStoryActivity"
    }
}