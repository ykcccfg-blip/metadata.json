package com.example.model

data class GlobalServer(
    val id: String,
    val name: String,
    val nameAr: String,
    val flag: String,
    val country: String,
    val countryAr: String,
    val regionCode: String,
    val pingMs: Int,
    val bandwidthGbps: Int,
    val gpuCluster: String,
    val loadPercent: Int,
    val uptimePercent: Double,
    val isOptimal: Boolean = false,
    val descriptionAr: String
)

data class ShowcaseItem(
    val id: String,
    val title: String,
    val titleAr: String,
    val category: String,
    val categoryAr: String,
    val type: String, // "Game" or "App"
    val rating: Double,
    val activeUsers: String,
    val iconName: String,
    val colorHex: String,
    val descriptionAr: String,
    val interactiveType: String,
    val prompt: String
)
