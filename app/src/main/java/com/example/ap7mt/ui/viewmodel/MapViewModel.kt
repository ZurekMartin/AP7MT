package com.example.ap7mt.ui.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.annotation.SuppressLint
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap7mt.data.api.ApiClient
import com.example.ap7mt.data.repository.MapRepository
import com.example.ap7mt.ui.screens.ElectronicStore
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class MapViewModel : ViewModel() {

    private val repository = MapRepository(ApiClient.overpassApi)

    private val _stores = MutableStateFlow<List<ElectronicStore>>(emptyList())
    val stores: StateFlow<List<ElectronicStore>> = _stores.asStateFlow()

    private val _userLocation = MutableStateFlow<Location?>(null)
    val userLocation: StateFlow<Location?> = _userLocation.asStateFlow()

    private val _selectedStore = MutableStateFlow<ElectronicStore?>(null)
    val selectedStore: StateFlow<ElectronicStore?> = _selectedStore.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        setDefaultZlinLocation()
        loadStores()
    }

    fun loadStores() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                userLocation.value?.let { location ->
                    val stores = repository.searchStoresInArea(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        radiusInMeters = 10000
                    )
                    _stores.value = stores
                } ?: run {
                    _error.value = "Poloha není k dispozici"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Chyba při načítání obchodů: ${e.localizedMessage}"
                _stores.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectStore(store: ElectronicStore?) {
        _selectedStore.value = store
    }

    fun requestLocationPermission(context: Context) {
        userLocation.value?.let { loadStores() }
    }

    suspend fun centerOnUserLocation(mapView: MapView) {
        userLocation.value?.let { location ->
            val geoPoint = GeoPoint(location.latitude, location.longitude)
            mapView.controller.animateTo(geoPoint)
            mapView.controller.setZoom(17.0)
        }
    }

    fun updateUserLocation(location: Location) {
        _userLocation.value = location
    }

    private fun setDefaultZlinLocation() {
        val defaultLocation = Location("").apply {
            latitude = 49.2308443
            longitude = 17.657083
        }
        _userLocation.value = defaultLocation
    }

    private fun hasLocationPermission(context: Context): Boolean {
        val fine = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    fun fetchUserLocation(context: Context) {
        viewModelScope.launch {
            val location = obtainBestLocation(context)
            if (location != null) {
                _userLocation.value = location
            } else {
                setDefaultZlinLocation()
            }
            loadStores()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun obtainBestLocation(context: Context): Location? {
        if (!hasLocationPermission(context)) return null

        val fused = LocationServices.getFusedLocationProviderClient(context)

        val cancellationTokenSource = CancellationTokenSource()
        val currentLocation = suspendCancellableCoroutine<Location?> { cont ->
            val task = fused.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.token)
            task.addOnSuccessListener { loc -> if (!cont.isCompleted) cont.resume(loc) }
            task.addOnFailureListener { _ -> if (!cont.isCompleted) cont.resume(null) }
            cont.invokeOnCancellation { cancellationTokenSource.cancel() }
        }
        if (currentLocation != null) return currentLocation

        return suspendCancellableCoroutine { cont ->
            val task = fused.lastLocation
            task.addOnSuccessListener { loc -> if (!cont.isCompleted) cont.resume(loc) }
            task.addOnFailureListener { _ -> if (!cont.isCompleted) cont.resume(null) }
        }
    }
}
