package pl.workoutplanner.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import pl.workoutplanner.data.DataStore
import pl.workoutplanner.models.Exercise
import pl.workoutplanner.models.WorkoutLog
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class StatsViewModel(application: Application) : AndroidViewModel(application) {

    val workoutLogs = mutableStateListOf<WorkoutLog>()

    fun loadStats() {
        try {
            val context = getApplication<Application>()
            val loaded = DataStore(context).loadWorkoutLogs()
            workoutLogs.clear()
            workoutLogs.addAll(loaded)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun totalWorkouts(): Int {
        return completedWorkouts().size
    }

    fun totalMinutes(): Int {
        return completedWorkouts().sumOf { it.durationMinutes }
    }

    fun weeklyCount(): Int {
        return try {
            val today = LocalDate.now()
            val startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            completedWorkouts().count { log ->
                try {
                    val logDate = LocalDate.parse(log.date)
                    !logDate.isBefore(startOfWeek) && !logDate.isAfter(today)
                } catch (e: Exception) {
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun monthlyCount(): Int {
        return try {
            val today = LocalDate.now()
            completedWorkouts().count { log ->
                try {
                    val logDate = LocalDate.parse(log.date)
                    logDate.year == today.year && logDate.monthValue == today.monthValue
                } catch (e: Exception) {
                    false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    // Zwraca listę par (data, maxCiężar) dla wybranego ćwiczenia
    fun progressData(exerciseId: String): List<Pair<String, Double>> {
        return try {
            completedWorkouts()
                .sortedBy { it.date }
                .mapNotNull { log ->
                    val maxWeight = log.entries
                        .filter { it.exerciseId == exerciseId && it.isCompleted }
                        .maxByOrNull { it.weight }
                        ?.weight

                    if (maxWeight != null) {
                        Pair(log.date, maxWeight)
                    } else {
                        null
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Filtruje ćwiczenia do tych, które mają historię treningów
    fun exercisesWithHistory(exercises: List<Exercise>): List<Exercise> {
        return try {
            val exerciseIdsWithHistory = completedWorkouts()
                .flatMap { it.entries }
                .filter { it.isCompleted }
                .map { it.exerciseId }
                .toSet()

            exercises.filter { it.id in exerciseIdsWithHistory }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun completedWorkouts(): List<WorkoutLog> {
        return workoutLogs.filter { it.isFinished }
    }
}
