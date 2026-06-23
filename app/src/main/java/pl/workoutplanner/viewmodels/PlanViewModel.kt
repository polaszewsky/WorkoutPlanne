package pl.workoutplanner.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import pl.workoutplanner.data.DataStore
import pl.workoutplanner.data.SampleData
import pl.workoutplanner.models.Exercise
import pl.workoutplanner.models.PlanExercise
import pl.workoutplanner.models.WorkoutPlan
import java.util.UUID

class PlanViewModel(application: Application) : AndroidViewModel(application) {

    val plans = mutableStateListOf<WorkoutPlan>()

    fun loadPlans() {
        try {
            val context = getApplication<Application>()
            val loaded = DataStore(context).loadPlans()
            plans.clear()
            if (loaded.isEmpty()) {
                val exercises = SampleData.defaultExercises()
                val defaults = SampleData.defaultPlans(exercises)
                plans.addAll(defaults)
                savePlans()
            } else {
                plans.addAll(loaded)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun savePlans() {
        try {
            val context = getApplication<Application>()
            DataStore(context).savePlans(plans.toList())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addPlan(name: String, notes: String): WorkoutPlan {
        val plan = WorkoutPlan(
            id = UUID.randomUUID().toString(),
            name = name,
            notes = notes,
            exercises = mutableListOf()
        )
        try {
            plans.add(plan)
            savePlans()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return plan
    }

    fun deletePlan(plan: WorkoutPlan) {
        try {
            plans.remove(plan)
            savePlans()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addExerciseToPlan(
        plan: WorkoutPlan,
        exercise: Exercise,
        sets: Int,
        reps: Int,
        weight: Double
    ) {
        try {
            val index = plans.indexOf(plan)
            if (index == -1) return

            val planExercise = PlanExercise(
                id = UUID.randomUUID().toString(),
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                orderIndex = plan.exercises.size,
                sets = sets,
                reps = reps,
                weight = weight
            )
            val updatedPlan = plan.copy(
                exercises = (plan.exercises + planExercise).toMutableList()
            )
            plans[index] = updatedPlan
            savePlans()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun removeExerciseFromPlan(plan: WorkoutPlan, planExercise: PlanExercise) {
        try {
            val index = plans.indexOf(plan)
            if (index == -1) return

            val updatedPlan = plan.copy(
                exercises = plan.exercises.filter { it.id != planExercise.id }.toMutableList()
            )
            plans[index] = updatedPlan
            savePlans()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updatePlanExercise(
        plan: WorkoutPlan,
        planExercise: PlanExercise,
        sets: Int,
        reps: Int,
        weight: Double
    ) {
        try {
            val planIndex = plans.indexOf(plan)
            if (planIndex == -1) return

            val updatedExercises = plan.exercises.map { existing ->
                if (existing.id == planExercise.id) {
                    existing.copy(sets = sets, reps = reps, weight = weight)
                } else {
                    existing
                }
            }
            val updatedPlan = plan.copy(exercises = updatedExercises.toMutableList())
            plans[planIndex] = updatedPlan
            savePlans()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
