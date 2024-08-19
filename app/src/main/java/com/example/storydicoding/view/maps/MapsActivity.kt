package com.example.storydicoding.view.maps

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.storydicoding.R
import com.example.storydicoding.api.response.ListStoryItem
import com.example.storydicoding.databinding.ActivityMapsBinding
import com.example.storydicoding.view.ViewModelFactory
import com.example.storydicoding.view.main.MainActivity
import com.example.storydicoding.view.welcome.WelcomeActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                viewModel.logout()
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_listStory -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val latLngIndonesia = LatLng(0.7893, 113.9213)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLngIndonesia))

        viewModel.getUser().observe(this) { data ->
            if (data != null && data.token?.isNotEmpty() == true) {
                viewModel.getStories(token = data.token.toString())
                viewModel.stories.observe(this) { stories ->
                    updateUserLocation(stories)
                }
            }
        }
    }

    private fun updateUserLocation(listStoryItems: List<ListStoryItem>?) {
        listStoryItems?.forEach { userLoc ->
            val lat = userLoc.lat ?: return@forEach
            val lon = userLoc.lon ?: return@forEach

            val marker = MarkerOptions()
                .position(LatLng(lat as Double, lon as Double))
                .title(userLoc.name)
                .snippet(userLoc.description)

            mMap.addMarker(marker)
        }
    }
}