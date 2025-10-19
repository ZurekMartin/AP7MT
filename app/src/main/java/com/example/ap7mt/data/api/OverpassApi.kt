package com.example.ap7mt.data.api

import com.example.ap7mt.ui.screens.ElectronicStore
import retrofit2.http.GET
import retrofit2.http.Query

interface OverpassApi {

    @GET("api/interpreter")
    suspend fun searchStores(
        @Query("data") query: String
    ): OverpassResponse
}

data class OverpassResponse(
    val elements: List<OverpassElement>
)

data class OverpassElement(
    val type: String,
    val id: Long,
    val lat: Double? = null,
    val lon: Double? = null,
    val tags: Map<String, String>? = null
) {
    fun toElectronicStore(): ElectronicStore? {
        val name = tags?.get("name") ?: return null
        val shop = tags?.get("shop")
        val amenity = tags?.get("amenity")

        // Filtrujeme pouze relevantní obchody
        val isRelevant = when {
            shop == "electronics" -> true
            shop == "computer" -> true
            shop == "mobile_phone" -> true
            shop == "hifi" -> true
            shop == "appliance" -> true
            amenity == "shop" && shop != null -> true
            else -> false
        }

        if (!isRelevant || lat == null || lon == null) return null

        val address = buildAddress(tags)
        val type = when (shop) {
            "electronics" -> "Elektro obchod"
            "computer" -> "Počítačový obchod"
            "mobile_phone" -> "Mobilní telefony"
            "hifi" -> "Hi-fi obchod"
            "appliance" -> "Elektrospotřebiče"
            else -> "Obchod"
        }

        return ElectronicStore(
            name = name,
            address = address,
            latitude = lat,
            longitude = lon,
            type = type
        )
    }

    private fun buildAddress(tags: Map<String, String>?): String {
        val street = tags?.get("addr:street")
        val housenumber = tags?.get("addr:housenumber")
        val city = tags?.get("addr:city") ?: tags?.get("addr:place")
        val postcode = tags?.get("addr:postcode")

        val parts = mutableListOf<String>()
        if (street != null) parts.add(street)
        if (housenumber != null) parts.add(housenumber)
        if (city != null) parts.add(city)
        if (postcode != null) parts.add(postcode)

        return if (parts.isNotEmpty()) parts.joinToString(", ") else "Adresa není k dispozici"
    }
}
