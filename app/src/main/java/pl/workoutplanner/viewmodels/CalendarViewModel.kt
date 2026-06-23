package pl.workoutplanner.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import pl.workoutplanner.data.DataStore
import pl.workoutplanner.models.ScheduledWorkout
import pl.workoutplanner.models.WorkoutPlan
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    val scheduledWorkouts = mutableStateListOf<ScheduledWorkout>()
    val currentMonth = mutableStateOf(YearMonth.now())

    fun loadScheduledWorkouts() {
        try {
            val context = getApplication<Application>()
            val loaded = DataStore(context).loadScheduledWorkouts()
            scheduledWorkouts.clear()
            if (loaded.isEmpty()) {
                val exercises = pl.workoutplanner.data.SampleData.defaultExercises()
                val plans = pl.workoutplanner.data.SampleData.defaultPlans(exercises)
                val defaults = pl.workoutplanner.data.SampleData.defaultScheduledWorkouts(plans)
                scheduledWorkouts.addAll(defaults)
                saveScheduledWorkouts()
            } else {
                scheduledWorkouts.addAll(loaded)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveScheduledWorkouts() {
        try {
            val context = getApplication<Application>()
            DataStore(context).saveScheduledWorkouts(scheduledWorkouts.toList())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun scheduleWorkout(plan: WorkoutPlan, date: LocalDate) {
        try {
            val workout = ScheduledWorkout(
                id = UUID.randomUUID().toString(),
                planId = plan.id,
                planName = plan.name,
                date = date.toString(),
                isCompleted = false
            )
            scheduledWorkouts.add(workout)
            saveScheduledWorkouts()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeScheduledWorkout(workout: ScheduledWorkout) {
        try {
            scheduledWorkouts.remove(workout)
            saveScheduledWorkouts()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun workoutsForDate(date: LocalDate): List<ScheduledWorkout> {
        val dateString = date.toString()
        return scheduledWorkouts.filter { it.date == dateString }
    }

    fun daysWithWorkouts(): Set<Int> {
        val month = currentMonth.value
        return scheduledWorkouts
            .mapNotNull { workout ->
                try {
                    val date = LocalDate.parse(workout.date)
                    if (date.year == month.year && date.monthValue == month.monthValue) {
                        date.dayOfMonth
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            .toSet()
    }

    fun daysWithCompletedWorkouts(): Set<Int> {
        val month = currentMonth.value
        return scheduledWorkouts
            .filter { it.isCompleted }
            .mapNotNull { workout ->
                try {
                    val date = LocalDate.parse(workout.date)
                    if (date.year == month.year && date.monthValue == month.monthValue) {
                        date.dayOfMonth
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }
            .toSet()
    }

    fun goToNextMonth() {
        currentMonth.value = currentMonth.value.plusMonths(1)
    }

    fun goToPreviousMonth() {
        currentMonth.value = currentMonth.value.minusMonths(1)
    }

    fun markCompleted(workout: ScheduledWorkout) {
        try {
            val index = scheduledWorkouts.indexOf(workout)
            if (index == -1) return

            scheduledWorkouts[index] = workout.copy(isCompleted = true)
            saveScheduledWorkouts()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
