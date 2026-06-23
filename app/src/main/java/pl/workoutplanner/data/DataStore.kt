package pl.workoutplanner.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.workoutplanner.models.Exercise
import pl.workoutplanner.models.ScheduledWorkout
import pl.workoutplanner.models.WorkoutLog
import pl.workoutplanner.models.WorkoutPlan

class DataStore(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "workout_planner"
        private const val KEY_EXERCISES = "exercises"
        private const val KEY_PLANS = "plans"
        private const val KEY_SCHEDULED_WORKOUTS = "scheduled_workouts"
        private const val KEY_WORKOUT_LOGS = "workout_logs"
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        encodeDefaults = true
    }

    // --- Exercises ---

    fun loadExercises(): List<Exercise> {
        val raw = prefs.getString(KEY_EXERCISES, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<Exercise>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveExercises(list: List<Exercise>) {
        prefs.edit()
            .putString(KEY_EXERCISES, json.encodeToString(list))
            .apply()
    }

    // --- Workout Plans ---

    fun loadPlans(): List<WorkoutPlan> {
        val raw = prefs.getString(KEY_PLANS, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<WorkoutPlan>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun savePlans(list: List<WorkoutPlan>) {
        prefs.edit()
            .putString(KEY_PLANS, json.encodeToString(list))
            .apply()
    }

    // --- Scheduled Workouts ---

    fun loadScheduledWorkouts(): List<ScheduledWorkout> {
        val raw = prefs.getString(KEY_SCHEDULED_WORKOUTS, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<ScheduledWorkout>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveScheduledWorkouts(list: List<ScheduledWorkout>) {
        prefs.edit()
            .putString(KEY_SCHEDULED_WORKOUTS, json.encodeToString(list))
            .apply()
    }

    // --- Workout Logs ---

    fun loadWorkoutLogs(): List<WorkoutLog> {
        val raw = prefs.getString(KEY_WORKOUT_LOGS, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<WorkoutLog>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveWorkoutLogs(list: List<WorkoutLog>) {
        prefs.edit()
            .putString(KEY_WORKOUT_LOGS, json.encodeToString(list))
            .apply()
    }
}
