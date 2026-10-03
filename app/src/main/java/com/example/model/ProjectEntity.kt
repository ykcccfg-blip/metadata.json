package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val type: String, // "App" or "Game"
    val category: String,
    val iconName: String,
    val colorHex: String,
    val prompt: String,
    val generatedCode: String,
    val interactiveType: String,
    val timestamp: Long = System.currentTimeMillis()
)
