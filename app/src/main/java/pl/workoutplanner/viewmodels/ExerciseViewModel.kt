package pl.workoutplanner.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import pl.workoutplanner.data.DataStore
import pl.workoutplanner.data.SampleData
import pl.workoutplanner.models.Exercise
import java.util.UUID

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    val exercises = mutableStateListOf<Exercise>()
    val searchQuery = mutableStateOf("")

    fun loadExercises() {
        try {
            val context = getApplication<Application>()
            val loaded = DataStore(context).loadExercises()
            exercises.clear()
            if (loaded.isEmpty()) {
                val defaults = SampleData.defaultExercises()
                exercises.addAll(defaults)
                saveExercises()
            } else {
                exercises.addAll(loaded)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveExercises() {
        try {
            val context = getApplication<Application>()
            DataStore(context).saveExercises(exercises.toList())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addExercise(name: String, description: String, category: String) {
        try {
            val exercise = Exercise(
                id = UUID.randomUUID().toString(),
                name = name,
                descriptionText = description,
                category = category,
                isCustom = true
            )
            exercises.add(exercise)
            saveExercises()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteExercise(exercise: Exercise): Boolean {
        return try {
            if (exercise.isCustom) {
                exercises.remove(exercise)
                saveExercises()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun filteredExercises(): List<Exercise> {
        val query = searchQuery.value.trim().lowercase()
        return if (query.isEmpty()) {
            exercises.toList()
        } else {
            exercises.filter { exercise ->
                exercise.name.lowercase().contains(query) ||
                    exercise.category.lowercase().contains(query) ||
                    exercise.descriptionText.lowercase().contains(query)
            }
        }
    }

    fun exercisesByCategory(): Map<String, List<Exercise>> {
        return filteredExercises().groupBy { it.category }
    }

    fun categories(): List<String> {
        return exercises.map { it.category }.distinct().sorted()
    }
}
