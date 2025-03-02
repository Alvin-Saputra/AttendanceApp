package com.example.attendanceapp.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.example.attendanceapp.R
import com.example.attendanceapp.databinding.FragmentHomeBinding
import com.example.attendanceapp.databinding.FragmentMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private var marker: Marker? = null
    private var _binding: FragmentMapsBinding? = null
    private val binding get() = _binding!!
    private lateinit var geofencingClient: GeofencingClient

//    private val centerLat = -6.225432120342684
//    private val centerLng = 106.65717797155213
    private val centerLat = -6.118588
    private val centerLng = 106.686910
    private val geofenceRadius = 50.0

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(), GeofenceBroadcastReceiver::class.java).apply {
            action = GeofenceBroadcastReceiver.ACTION_GEOFENCE_EVENT
        }
        PendingIntent.getBroadcast(
            requireContext(), 0, intent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val geofenceReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val status = intent?.getStringExtra("status") ?: return
//            viewModel.setGeofenceStatus(status) // Perbarui ViewModel
            showToast(status)
            showToast("wkwkkkw")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnNext.setOnClickListener{
            findNavController().navigate(R.id.action_mapsFragment_to_selfieFragment)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        setupLocationClient()
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_mapsFragment_to_navigation_home)
        }
    }

    private fun setupLocationClient() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }


    private fun addMarker(location: LatLng, title: String, snippet: String? = null): Marker {
        val markerOptions = MarkerOptions().position(location).title(title)
        snippet?.let { markerOptions.snippet(it) }
        marker?.remove()
        marker = mMap.addMarker(markerOptions)
        return marker!!
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true

        mMap.uiSettings.isMapToolbarEnabled = true

        checkPermissions()

    }

    private val requestForegroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }

        if (allGranted) {
            // Jika izin foreground diberikan, cek apakah perlu izin background
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Cek apakah izin background sudah diberikan
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) {
                    // Minta izin background secara terpisah
                    showToast("Aplikasi memerlukan izin lokasi latar belakang")
                    requestBackgroundPermission()
                } else {
                    // Izin background sudah diberikan
                    setupMapWithLocation()
                }
            } else {
                // Untuk Android < 10, tidak perlu izin background khusus
                setupMapWithLocation()
            }
        } else {
            showToast("Aplikasi membutuhkan izin lokasi untuk berfungsi dengan baik")
//            Log.d(TAG, "Izin foreground ditolak: ${permissions.entries}")
        }
    }

    // Step 2: Launcher terpisah untuk background permission
    private val requestBackgroundPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            setupMapWithLocation()
        } else {
            showToast("Aplikasi membutuhkan izin lokasi latar belakang untuk geofencing")
//            Log.d(TAG, "Izin background ditolak")
        }
    }


    @SuppressLint("MissingPermission")
    private fun addGeofence() {
        geofencingClient = LocationServices.getGeofencingClient(requireContext())

        val geofence = Geofence.Builder()
            .setRequestId("kampus")
            .setCircularRegion(centerLat, centerLng, geofenceRadius.toFloat())
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_ENTER)
            .setLoiteringDelay(1000)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnCompleteListener {
                geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
                    addOnSuccessListener {
                        showToast("Geofencing added")
                    }
                    addOnFailureListener {
                        showToast("Geofencing not added: ${it.message}")
                        Log.d("MapsFragment", "Geofencing not added: ${it.message}")
                    }
                }
            }
        }
    }

    private fun showToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun checkPermissions(){
        if (checkForegroundLocationPermission()) {
            // Foreground izin sudah diberikan
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (checkBackgroundLocationPermission()) {
                    // Semua izin sudah diberikan
                    setupMapWithLocation()
                } else {
                    // Minta izin background
                    requestBackgroundPermission()
                }
            } else {
                // Android < 10, tidak perlu izin background
                setupMapWithLocation()
            }
        } else {
            // Minta izin foreground dulu
            requestForegroundPermissions()
        }

    }

    private fun checkForegroundLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkBackgroundLocationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Untuk Android < 10, tidak perlu izin background khusus
        }
    }


    private fun requestForegroundPermissions() {
        Log.d(TAG, "Meminta izin foreground location")
        requestForegroundPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private fun requestBackgroundPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Di Android 10+, perlu menampilkan dialog atau pesan
            // sebelum meminta izin background location
            showToast("Mohon aktifkan izin 'Izinkan sepanjang waktu' untuk fitur geofencing")
            Log.d(TAG, "Meminta izin background location")

            requestBackgroundPermissionLauncher.launch(
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }



    @SuppressLint("MissingPermission")
    private fun setupMapWithLocation() {
        if (!::mMap.isInitialized) return

        try {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val userLocation = LatLng(it.latitude, it.longitude)

                    // Tambahkan marker untuk lokasi pengguna
                    addMarker(userLocation, "Lokasi Anda")

                    // Tambahkan marker dan lingkaran untuk lokasi kampus
                    val kampusLocation = LatLng(centerLat, centerLng)

                    // Tambahkan marker kampus
                    mMap.addMarker(MarkerOptions()
                        .position(kampusLocation)
                        .title("Kampus"))

                    // Tambahkan lingkaran geofence
                    mMap.addCircle(
                        CircleOptions()
                            .center(kampusLocation)
                            .radius(geofenceRadius)
                            .fillColor(0x40ADD8E6)  // Light blue with transparency
                            .strokeColor(Color.BLUE)
                            .strokeWidth(5f)
                    )

                    // Gerakkan kamera ke lokasi kampus dengan zoom yang sesuai
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kampusLocation, 18f))
                } ?: run {
                    // Jika lokasi null, setidaknya tampilkan area kampus
                    val kampusLocation = LatLng(centerLat, centerLng)

                    mMap.addMarker(MarkerOptions()
                        .position(kampusLocation)
                        .title("Kampus"))

                    mMap.addCircle(
                        CircleOptions()
                            .center(kampusLocation)
                            .radius(geofenceRadius)
                            .fillColor(0x40ADD8E6)
                            .strokeColor(Color.BLUE)
                            .strokeWidth(5f)
                    )

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kampusLocation, 18f))

                    showToast("Tidak dapat menemukan lokasi Anda saat ini")
                }
            }
            addGeofence()

        } catch (e: Exception) {
            Log.e("MapsFragment", "Error setting up map with location: ${e.message}")
            showToast("Error: ${e.message}")
        }

    }


    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val BACKGROUND_LOCATION_PERMISSION_REQUEST_CODE = 2
        private const val TAG = "MapsFragment"
    }

    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("GeofenceStatusUpdate")
        requireContext().registerReceiver(geofenceReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(geofenceReceiver)
    }
}