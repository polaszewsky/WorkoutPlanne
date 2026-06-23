package pl.workoutplanner.models

import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Serializable
data class PlanExercise(
    val id: String = UUID.randomUUID().toString(),
    val exerciseId: String,
    val exerciseName: String,
    val orderIndex: Int,
    val sets: Int,
    val reps: Int,
    val weight: Double
)

@Serializable
data class WorkoutPlan(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val notes: String,
    val createdAt: String = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    val exercises: MutableList<PlanExercise> = mutableListOf()
)
