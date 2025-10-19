package com.example.ap7mt.data.model

enum class SortBy(val displayName: String, val apiValue: String) {
    RELEVANCE("Relevance", "relevance"),
    RELEASE_DATE("Datum vydání", "release-date"),
    POPULARITY("Popularita", "popularity"),
    ALPHABETICAL("Abecedně", "alphabetical")
}
