package pl.workoutplanner.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Exercise(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val descriptionText: String,
    val category: String,
    val isCustom: Boolean = false
)
