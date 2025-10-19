package com.example.ap7mt.ui.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ap7mt.data.api.ApiClient
import com.example.ap7mt.data.repository.MapRepository
import com.example.ap7mt.ui.screens.ElectronicStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
        val defaultLocation = Location("").apply {
            latitude = 49.2308443
            longitude = 17.657083
        }
        _userLocation.value = defaultLocation
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
        userLocation.value?.let {
            loadStores()
        }
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
}
