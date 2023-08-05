package com.steven.disaster.view

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.steven.disaster.viewmodel.MainViewModel
import com.steven.disaster.R
import com.steven.disaster.data.SettingPreference
import com.steven.disaster.viewmodel.SettingViewModel
import com.steven.disaster.databinding.ActivityMainBinding
import com.steven.disaster.data.prefDataStore
import com.steven.disaster.utils.DisasterType
import com.steven.disaster.utils.SupportedArea

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

    private lateinit var mainBinding: ActivityMainBinding
    private val disasterAdapter = DisasterAdapter()
    private val listLatLng: MutableList<LatLng> = mutableListOf()
    private val listMarker: MutableList<Marker?> = mutableListOf()
    private var map: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        supportActionBar?.hide()
        setContentView(mainBinding.root)

        val mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

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

        mainBinding.imgBtnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }

        mainViewModel.geometriesItem.observe(this) { geometriesItem ->
            disasterAdapter.submitList(geometriesItem)
            bottomSheetLayout.findViewById<TextView>(R.id.tv_no_data).visibility =
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

        with(bottomSheetLayout.findViewById<RecyclerView>(R.id.rv_disaster)) {
            layoutManager = LinearLayoutManager(context)
            adapter = disasterAdapter
            isNestedScrollingEnabled = true
        }

        mainViewModel.isLoading.observe(this) { isLoading ->
            bottomSheetLayout.findViewById<ProgressBar>(R.id.progressBar).visibility =
                if (isLoading) View.VISIBLE else View.GONE
        }

        mainViewModel.isFailed.observe(this) { isFailed ->
            bottomSheetLayout.findViewById<TextView>(R.id.tv_no_data).visibility =
                if (isFailed) View.VISIBLE else View.GONE
        }

        val listAreaValues: MutableList<String> = SupportedArea.area.values.toMutableList()
        val listAreaKeys = SupportedArea.area.keys.toList()
        mainBinding.spinnerArea.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, listAreaValues)
        mainBinding.spinnerArea.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    bottomSheetLayout.findViewById<TextView>(R.id.tv_no_data).visibility = View.GONE
                    listLatLng.clear()
                    if (position == 0) {
                        mainViewModel.getGeometriesItem()
                    } else {
                        mainViewModel.getGeometriesItemByLocation(listAreaKeys[position])
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        mainBinding.spinnerType.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, DisasterType.type)
        mainBinding.spinnerType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    bottomSheetLayout.findViewById<TextView>(R.id.tv_no_data).visibility = View.GONE
                    listLatLng.clear()
                    if (position == 0) {
                        mainViewModel.getGeometriesItem()
                    } else {
                        mainViewModel.getGeometriesItemByType(DisasterType.type[position].lowercase())
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
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
}