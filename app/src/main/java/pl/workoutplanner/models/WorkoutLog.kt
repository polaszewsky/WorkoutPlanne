package pl.workoutplanner.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class LogEntry(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val exerciseName: String,
    val setNumber: Int,
    val reps: Int,
    val weight: Double,
    val isCompleted: Boolean = false
)

@Serializable
data class WorkoutLog(
    val id: String = UUID.randomUUID().toString(),
    val date: String,
    val durationMinutes: Int = 0,
    val isFinished: Boolean = false,
    val scheduledWorkoutId: String? = null,
    val planName: String = "",
    val entries: MutableList<LogEntry> = mutableListOf()
)
