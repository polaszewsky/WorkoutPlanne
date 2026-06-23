package pl.workoutplanner.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ScheduledWorkout(
    val id: String = UUID.randomUUID().toString(),
    val date: String, // format: yyyy-MM-dd
    val isCompleted: Boolean = false,
    val planId: String,
    val planName: String
)
