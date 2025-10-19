package com.example.ap7mt.data.repository

import com.example.ap7mt.data.api.OverpassApi
import com.example.ap7mt.ui.screens.ElectronicStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapRepository(
    private val overpassApi: OverpassApi
) {

    suspend fun searchStoresInArea(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Int = 5000
    ): List<ElectronicStore> = withContext(Dispatchers.IO) {
        try {
            val query = """
                [out:json][timeout:25];
                (
                  node["shop"="electronics"](around:$radiusInMeters,$latitude,$longitude);
                  node["shop"="computer"](around:$radiusInMeters,$latitude,$longitude);
                  node["shop"="mobile_phone"](around:$radiusInMeters,$latitude,$longitude);
                  node["shop"="hifi"](around:$radiusInMeters,$latitude,$longitude);
                  node["shop"="appliance"](around:$radiusInMeters,$latitude,$longitude);
                  way["shop"="electronics"](around:$radiusInMeters,$latitude,$longitude);
                  way["shop"="computer"](around:$radiusInMeters,$latitude,$longitude);
                  way["shop"="mobile_phone"](around:$radiusInMeters,$latitude,$longitude);
                  way["shop"="hifi"](around:$radiusInMeters,$latitude,$longitude);
                  way["shop"="appliance"](around:$radiusInMeters,$latitude,$longitude);
                );
                out center;
            """.trimIndent()

            val response = overpassApi.searchStores(query)
            response.elements
                .mapNotNull { it.toElectronicStore() }
                .distinctBy { it.name + it.latitude + it.longitude }
                .sortedBy { store ->
                    calculateDistance(latitude, longitude, store.latitude, store.longitude)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6378.0

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return earthRadius * c
    }
}
