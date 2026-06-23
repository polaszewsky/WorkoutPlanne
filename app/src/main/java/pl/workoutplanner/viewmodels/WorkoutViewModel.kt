package pl.workoutplanner.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import pl.workoutplanner.data.DataStore
import pl.workoutplanner.models.LogEntry
import pl.workoutplanner.models.PlanExercise
import pl.workoutplanner.models.ScheduledWorkout
import pl.workoutplanner.models.WorkoutLog
import java.time.LocalDate
import java.util.UUID

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    val workoutLogs = mutableStateListOf<WorkoutLog>()
    val currentLog = mutableStateOf<WorkoutLog?>(null)
    val elapsedSeconds = mutableStateOf(0)

    fun loadLogs() {
        try {
            val context = getApplication<Application>()
            val loaded = DataStore(context).loadWorkoutLogs()
            workoutLogs.clear()
            if (loaded.isEmpty()) {
                val exercises = pl.workoutplanner.data.SampleData.defaultExercises()
                val plans = pl.workoutplanner.data.SampleData.defaultPlans(exercises)
                val defaults = pl.workoutplanner.data.SampleData.defaultWorkoutLogs(plans)
                workoutLogs.addAll(defaults)
                saveLogs()
            } else {
                workoutLogs.addAll(loaded)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveLogs() {
        try {
            val context = getApplication<Application>()
            DataStore(context).saveWorkoutLogs(workoutLogs.toList())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Zwraca ostatnie powtórzenia i ciężar dla danego ćwiczenia
    fun lastValues(exerciseId: String): Pair<Int, Double>? {
        return try {
            workoutLogs
                .sortedByDescending { it.date }
                .flatMap { it.entries }
                .firstOrNull { it.exerciseId == exerciseId && it.isCompleted }
                ?.let { Pair(it.reps, it.weight) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Rozpoczyna nową sesję treningową i wypełnia wpisy ostatnimi wartościami
    fun startWorkout(
        scheduled: ScheduledWorkout,
        exercises: List<PlanExercise>
    ): WorkoutLog {
        val entries = exercises.flatMap { planExercise ->
            val last = lastValues(planExercise.exerciseId)
            (1..planExercise.sets).map { setNumber ->
                LogEntry(
                    id = UUID.randomUUID().toString(),
                    exerciseId = planExercise.exerciseId,
                    exerciseName = planExercise.exerciseName,
                    setNumber = setNumber,
                    reps = last?.first ?: planExercise.reps,
                    weight = last?.second ?: planExercise.weight,
                    isCompleted = false
                )
            }
        }

        val log = WorkoutLog(
            id = UUID.randomUUID().toString(),
            scheduledWorkoutId = scheduled.id,
            planName = scheduled.planName,
            date = LocalDate.now().toString(),
            durationMinutes = 0,
            entries = entries.toMutableList(),
            isFinished = false
        )

        currentLog.value = log
        elapsedSeconds.value = 0
        return log
    }

    fun updateEntry(entryIndex: Int, reps: Int, weight: Double) {
        try {
            val log = currentLog.value ?: return
            if (entryIndex < 0 || entryIndex >= log.entries.size) return

            val updatedEntries = log.entries.toMutableList()
            updatedEntries[entryIndex] = updatedEntries[entryIndex].copy(
                reps = reps,
                weight = weight
            )
            currentLog.value = log.copy(entries = updatedEntries)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleEntryCompleted(entryIndex: Int) {
        try {
            val log = currentLog.value ?: return
            if (entryIndex < 0 || entryIndex >= log.entries.size) return

            val updatedEntries = log.entries.toMutableList()
            val entry = updatedEntries[entryIndex]
            updatedEntries[entryIndex] = entry.copy(isCompleted = !entry.isCompleted)
            currentLog.value = log.copy(entries = updatedEntries)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun finishWorkout(durationMinutes: Int) {
        try {
            val log = currentLog.value ?: return
            val finishedLog = log.copy(
                durationMinutes = durationMinutes,
                isFinished = true
            )
            workoutLogs.add(finishedLog)
            saveLogs()
            currentLog.value = null
            elapsedSeconds.value = 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
