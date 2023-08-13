package com.steven.disaster.view

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.steven.disaster.viewmodel.MainViewModel
import com.steven.disaster.R
import com.steven.disaster.viewmodel.SettingViewModel
import com.steven.disaster.databinding.ActivityMainBinding
import com.steven.disaster.data.response.GeometriesItem
import com.steven.disaster.utils.SupportedArea
import com.steven.disaster.utils.WaterLevelWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    @Inject
    lateinit var workManager: WorkManager
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mainBinding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private val settingViewModel: SettingViewModel by viewModels()

    private val disasterAdapter = DisasterAdapter()
    private val locationSuggestionAdapter = LocationSuggestionAdapter { location ->
        mainBinding.searchViewLocation.setText(location)
    }

    private var map: GoogleMap? = null
    private var currentLocationLat: Double? = null
    private var currentLocationLng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        supportActionBar?.hide()
        setContentView(mainBinding.root)

        createWorkManager()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        map.uiSettings.isMapToolbarEnabled = false

        checkTheme()
        mainViewModel.getGeometriesItem()
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
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    val mapFragment =
                        supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
                    mapFragment.getMapAsync(this)
                }
            }
        }
    }

    private fun checkTheme() {
        settingViewModel.getTheme.observe(this) { isDarkMode ->
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                map?.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.night_mode_map))
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
                LOCATION_PERMISSION_CODE
            )
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
            mapFragment.getMapAsync(this)
            currentLocationLat = location.latitude
            currentLocationLng = location.longitude
        }
    }

    private fun showMarker(geometriesItem: List<GeometriesItem?>) {
        map?.clear()
        val bounds = LatLngBounds.builder()

        if (currentLocationLat != null && currentLocationLng != null) {
            val latLng = LatLng(currentLocationLat!!, currentLocationLng!!)
            bounds.include(latLng)
            map?.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(getString(R.string.your_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )?.showInfoWindow()
        }

        if (geometriesItem.isNotEmpty()) {
            for (item in geometriesItem) {
                val latLng =
                    LatLng(item?.coordinates?.get(1) as Double, item.coordinates[0] as Double)
                val type = item.properties?.disasterType
                val location = SupportedArea.area.entries.find {
                    it.value == item.properties?.tags?.instanceRegionCode
                }?.key

                bounds.include(latLng)
                map?.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(type)
                        .snippet(location)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                )
            }
        }

        if (geometriesItem.isNotEmpty()) {
            val mapPadding = (resources.displayMetrics.widthPixels * 0.2).toInt()
            map?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), mapPadding))
        } else if (currentLocationLat != null && currentLocationLng != null) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        currentLocationLat!!,
                        currentLocationLng!!
                    ), 4.0f
                )
            )
        }
    }

    private fun observeDisasterData() {
        mainViewModel.geometriesItem.observe(this) { geometriesItem ->
            disasterAdapter.submitList(geometriesItem)
            mainBinding.bottomSheet.tvNoData.visibility =
                if (geometriesItem?.isEmpty() as Boolean) View.VISIBLE else View.GONE
            showMarker(geometriesItem)
        }
    }

    private fun observeIsEmptyState() {
        mainViewModel.isEmpty.observe(this) { isEmpty ->
            mainBinding.bottomSheet.tvNoData.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }

    private fun observeIsFailedState() {
        mainViewModel.isFailure.observe(this) { isFailed ->
            if (isFailed) {
                Toast.makeText(this, getString(R.string.failed_to_get_data), Toast.LENGTH_SHORT)
                    .show()
            }
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
        mainBinding.chipGroupDisaster.setOnCheckedStateChangeListener { _, _ ->
            mainBinding.bottomSheet.tvNoData.visibility = View.GONE
            getDataBySearchAndChip()
        }
    }

    private fun searchDisasterFromSearchViewInput() {
        mainBinding.searchViewLocation.editText.setOnEditorActionListener { _, _, _ ->
            mainBinding.searchBarLocation.text = mainBinding.searchViewLocation.text
            mainBinding.searchViewLocation.hide()
            getDataBySearchAndChip()
            false
        }
    }

    private fun getDataBySearchAndChip() {
        val selectedId = mainBinding.chipGroupDisaster.checkedChipId
        val idLocation =
            SupportedArea.area[mainBinding.searchViewLocation.text.toString()]

        val selectedDisaster: String? = if (selectedId == View.NO_ID) {
            null
        } else {
            mainBinding.chipGroupDisaster.findViewById<Chip>(selectedId).text.toString().lowercase()
        }
        mainViewModel.getGeometriesItem(idLocation, selectedDisaster)
    }

    private fun createWorkManager() {
        val request =
            PeriodicWorkRequestBuilder<WaterLevelWorker>(3, TimeUnit.HOURS)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
                .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
                .build()

        workManager.enqueueUniquePeriodicWork(
            NOTIFICATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_CODE = 101
        private const val NOTIFICATION_WORK_NAME = "NOTIFICATION_WORK_NAME"
    }
}