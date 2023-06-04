package com.academy.bangkit.mystoryapp.ui.story.maps

import android.content.pm.PackageManager
import android.content.res.Resources
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.academy.bangkit.mystoryapp.R
import com.academy.bangkit.mystoryapp.data.Result
import com.academy.bangkit.mystoryapp.data.local.entity.StoryEntity
import com.academy.bangkit.mystoryapp.databinding.FragmentMapsBinding
import com.academy.bangkit.mystoryapp.ui.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {

    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var _mapsBinding: FragmentMapsBinding? = null
    private val mapsBinding get() = _mapsBinding!!

    private lateinit var map: GoogleMap

    private val callback = OnMapReadyCallback { googleMap ->


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _mapsBinding = FragmentMapsBinding.inflate(layoutInflater, container, false)
        return mapsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isCompassEnabled = true

        setMapStyle()
        fetchStories()
    }

    private fun setMapStyle() {
        try {
            val success =
                map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        requireActivity(),
                        R.raw.map_style
                    )
                )
            if (!success) {
                showError(getString(R.string.err_maps))
                Log.e(TAG, "Style parsing failed")
            }
        } catch (e: Resources.NotFoundException) {
            showError(e.message.toString())
            Log.e(TAG, "Can't find style. Error: $e")
        }
    }

    private fun fetchStories() {
        mapsViewModel.storyMapsResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {}
                is Result.Error -> {
                    showError(result.error)
                }

                is Result.Success -> {
                    setupMarker(result.data)
                }
            }
        }
    }

    private fun setupMarker(stories: List<StoryEntity>) {

        val firstStories = stories.firstOrNull() ?: return
        val cameraUpdate =
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    firstStories.lat,
                    firstStories.lon
                ), 15f
            )

        map.animateCamera(cameraUpdate)

        stories.forEach {
            val marker = MarkerOptions()
            marker.position(LatLng(it.lat, it.lon))
                .title(it.name)
                .snippet(it.description)

            val markerTag = map.addMarker(marker)
            markerTag?.tag = stories
        }
    }

//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            getMyLocation()
//        }
//    }

    private fun showError(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

//    private fun getMyLocation() {
//        if (ContextCompat.checkSelfPermission(
//                this.requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//
//        } else {
//            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mapsBinding = null
    }

    companion object {
        const val TAG = "MapsFragment"
    }
}