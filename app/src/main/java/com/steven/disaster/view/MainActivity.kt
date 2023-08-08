package com.steven.disaster.view

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.steven.disaster.viewmodel.MainViewModel
import com.steven.disaster.R
import com.steven.disaster.data.SettingPreference
import com.steven.disaster.viewmodel.SettingViewModel
import com.steven.disaster.databinding.ActivityMainBinding
import com.steven.disaster.data.prefDataStore
import com.steven.disaster.utils.SupportedArea

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel

    private val disasterAdapter = DisasterAdapter()
    private val locationSuggestionAdapter = LocationSuggestionAdapter { location ->
        mainBinding.searchViewLocation.setText(location)
    }
    private val listLatLng: MutableList<LatLng> = mutableListOf()
    private val listMarker: MutableList<Marker?> = mutableListOf()
    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        supportActionBar?.hide()
        setContentView(mainBinding.root)

        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

        observeDisasterData()
        observeIsEmptyState()
        observeIsFailedState()
        observeIsLoadingState()

        setUpSearchBar()
        setUpSearchBarMenu()
        setUpBottomSheetRecyclerView()
        setUpSearchView()
        setUpChipDisasterFilter()
        setUpLocationSuggestionRecyclerView()

        searchDisasterFromSearchViewInput()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        val pref = SettingPreference.getInstance(application.prefDataStore)
        val settingViewModel = ViewModelProvider(this)[SettingViewModel::class.java]
        settingViewModel.setPref(pref)

        settingViewModel.getThemeSetting().observe(this) { isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                permissionCode
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val mapFragment =
                    supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4.0f))
        map.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(getString(R.string.your_location))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
        )
        showMarker()
    }

    private fun showMarker() {
        clearMarker()
        for (latLong in listLatLng) {
            val disasterMarker = map?.addMarker(
                MarkerOptions()
                    .position(latLong)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            listMarker.add(disasterMarker)
        }
    }

    private fun clearMarker() {
        for (disasterMarker in listMarker) {
            disasterMarker?.remove()
        }
        listMarker.clear()
    }

    private fun observeDisasterData() {
        mainViewModel.geometriesItem.observe(this) { geometriesItem ->
            disasterAdapter.submitList(geometriesItem)
            mainBinding.bottomSheet.tvNoData.visibility =
                if (geometriesItem?.isEmpty() as Boolean) View.VISIBLE else View.GONE
            for (i in geometriesItem.indices) {
                listLatLng.add(
                    LatLng(
                        geometriesItem[i]?.coordinates?.get(1) as Double,
                        geometriesItem[i]?.coordinates?.get(0) as Double
                    )
                )
            }
            showMarker()
        }
    }

    private fun observeIsEmptyState() {
        mainViewModel.isEmpty.observe(this) { isEmpty ->
            mainBinding.bottomSheet.tvNoData.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun observeIsFailedState() {
        mainViewModel.isFailure.observe(this) { isFailed ->
            mainBinding.bottomSheet.tvNoData.visibility =
                if (isFailed) View.VISIBLE else View.GONE
        }
    }

    private fun observeIsLoadingState() {
        mainViewModel.isLoading.observe(this) { isLoading ->
            mainBinding.bottomSheet.progressBar.visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setUpBottomSheetRecyclerView() {
        with(mainBinding.bottomSheet.rvDisaster) {
            layoutManager = LinearLayoutManager(context)
            adapter = disasterAdapter
            isNestedScrollingEnabled = true
        }
    }

    private fun setUpLocationSuggestionRecyclerView() {
        with(mainBinding.rvLocationSuggestion) {
            layoutManager = LinearLayoutManager(context)
            adapter = locationSuggestionAdapter
        }
    }

    private fun setUpSearchView() {
        val listAreaValues = SupportedArea.area.keys.toList()
        mainBinding.searchViewLocation.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filterLocation = listAreaValues.filter {
                    it.contains(s ?: "", ignoreCase = true)
                }
                locationSuggestionAdapter.submitList(filterLocation)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setUpSearchBar() {
        mainBinding.searchViewLocation.setupWithSearchBar(mainBinding.searchBarLocation)
    }

    private fun setUpSearchBarMenu() {
        mainBinding.searchBarLocation.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_settings -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }

    private fun setUpChipDisasterFilter() {
        mainBinding.chipGroupDisaster.setOnCheckedStateChangeListener { group, _ ->
            val selectedId = group.checkedChipId
            mainBinding.bottomSheet.tvNoData.visibility = View.GONE
            listLatLng.clear()
            if (selectedId == View.NO_ID) {
                mainViewModel.getGeometriesItem()
            } else {
                val selectedDisaster =
                    group.findViewById<Chip>(selectedId).text.toString().lowercase()
                mainViewModel.getGeometriesItemByType(selectedDisaster)
            }
        }
    }

    private fun searchDisasterFromSearchViewInput() {
        mainBinding.searchViewLocation.editText.setOnEditorActionListener { _, _, _ ->
            mainBinding.searchBarLocation.text = mainBinding.searchViewLocation.text
            mainBinding.searchViewLocation.hide()
            val idLocation =
                SupportedArea.area[mainBinding.searchViewLocation.text.toString()]
            mainViewModel.getGeometriesItemByLocation(idLocation)
            false
        }
    }
}