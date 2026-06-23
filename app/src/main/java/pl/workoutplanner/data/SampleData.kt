package pl.workoutplanner.data

import pl.workoutplanner.models.*
import java.time.LocalDate
import java.util.UUID

object SampleData {

    fun defaultExercises(): List<Exercise> = listOf(
        // --- Klatka piersiowa ---
        Exercise(
            name = "Wyciskanie sztangi na ławce",
            descriptionText = "Połóż się na ławce płaskiej, chwyć sztangę nieco szerzej niż barki. Opuść sztangę do klatki piersiowej, a następnie wyciśnij ją do pełnego wyprostu ramion. Utrzymuj łopatki ściągnięte i stopy na podłodze.",
            category = "Klatka piersiowa"
        ),
        Exercise(
            name = "Wyciskanie hantli na ławce skośnej",
            descriptionText = "Usiądź na ławce ustawionej pod kątem 30-45 stopni. Chwyć hantle i wyciśnij je nad klatkę piersiową, a następnie powoli opuść do pozycji wyjściowej. Ćwiczenie angażuje górną część klatki piersiowej.",
            category = "Klatka piersiowa"
        ),
        Exercise(
            name = "Rozpiętki z hantlami",
            descriptionText = "Połóż się na ławce płaskiej z hantlami w wyprostowanych rękach nad klatką. Powoli rozłóż ramiona na boki, utrzymując lekko zgięte łokcie. Wróć do pozycji wyjściowej, napinając mięśnie klatki piersiowej.",
            category = "Klatka piersiowa"
        ),

        // --- Plecy ---
        Exercise(
            name = "Wiosłowanie sztangą",
            descriptionText = "Stań w rozkroku, pochyl tułów do przodu pod kątem ok. 45 stopni. Chwyć sztangę nachwytem i przyciągnij ją do brzucha, ściskając łopatki. Powoli opuść sztangę do pozycji wyjściowej.",
            category = "Plecy"
        ),
        Exercise(
            name = "Podciąganie na drążku",
            descriptionText = "Chwyć drążek nachwytem nieco szerzej niż barki. Podciągnij się aż broda znajdzie się powyżej drążka, napinając mięśnie pleców. Powoli opuść się do pełnego wyprostu ramion.",
            category = "Plecy"
        ),
        Exercise(
            name = "Ściąganie drążka wyciągu",
            descriptionText = "Usiądź przy wyciągu górnym, chwyć drążek szerokim nachwytem. Ściągnij drążek do górnej części klatki piersiowej, ściskając łopatki. Powoli wróć do pozycji wyjściowej kontrolując ruch.",
            category = "Plecy"
        ),

        // --- Nogi ---
        Exercise(
            name = "Przysiady ze sztangą",
            descriptionText = "Umieść sztangę na górnej części pleców (trapezach). Stań w rozkroku na szerokość barków i wykonaj przysiad do momentu, aż uda będą równoległe do podłogi. Wróć do pozycji stojącej napinając mięśnie nóg i pośladków.",
            category = "Nogi"
        ),
        Exercise(
            name = "Wyprosty nóg na maszynie",
            descriptionText = "Usiądź na maszynie do prostowania nóg, umieść kostki za wałkiem. Wyprostuj nogi do pełnego wyprostu, napinając mięśnie czworogłowe. Powoli wróć do pozycji wyjściowej.",
            category = "Nogi"
        ),
        Exercise(
            name = "Uginanie nóg na maszynie",
            descriptionText = "Połóż się na maszynie do uginania nóg, umieść kostki pod wałkiem. Ugnij nogi przyciągając wałek do pośladków, napinając mięśnie dwugłowe ud. Powoli wróć do pozycji wyjściowej.",
            category = "Nogi"
        ),

        // --- Barki ---
        Exercise(
            name = "Wyciskanie sztangi nad głową (OHP)",
            descriptionText = "Stań prosto, chwyć sztangę na szerokość barków na wysokości obojczyków. Wyciśnij sztangę nad głowę do pełnego wyprostu ramion. Powoli opuść sztangę do pozycji wyjściowej. Utrzymuj napięty core.",
            category = "Barki"
        ),
        Exercise(
            name = "Unoszenie hantli bokiem",
            descriptionText = "Stań prosto z hantlami przy bokach ciała. Unieś hantle na boki do poziomu barków, utrzymując lekko zgięte łokcie. Powoli opuść hantle do pozycji wyjściowej. Ćwiczenie izoluje boczne głowy naramiennych.",
            category = "Barki"
        ),
        Exercise(
            name = "Arnoldki",
            descriptionText = "Usiądź na ławce z oparciem, trzymając hantle na wysokości barków z dłońmi skierowanymi do siebie. Wyciśnij hantle nad głowę, jednocześnie obracając dłonie na zewnątrz. Wróć do pozycji wyjściowej odwracając ruch.",
            category = "Barki"
        ),

        // --- Ramiona ---
        Exercise(
            name = "Uginanie ramion ze sztangą",
            descriptionText = "Stań prosto, chwyć sztangę podchwytem na szerokość barków. Ugnij ramiona przyciągając sztangę do barków, napinając bicepsy. Powoli opuść sztangę do pozycji wyjściowej. Utrzymuj łokcie przy ciele.",
            category = "Ramiona"
        ),
        Exercise(
            name = "Prostowanie ramion na wyciągu",
            descriptionText = "Stań przed wyciągiem górnym, chwyć uchwyt nachwytem. Wyprostuj ramiona w łokciach, napinając tricepsy. Powoli wróć do pozycji wyjściowej. Utrzymuj łokcie przyciśnięte do ciała przez cały ruch.",
            category = "Ramiona"
        ),
        Exercise(
            name = "Uginanie ramion z hantlami",
            descriptionText = "Stań prosto lub usiądź na ławce z hantlami przy bokach ciała. Ugnij ramiona naprzemiennie lub jednocześnie, przyciągając hantle do barków. Powoli opuść hantle do pozycji wyjściowej.",
            category = "Ramiona"
        ),

        // --- Brzuch ---
        Exercise(
            name = "Brzuszki (Crunches)",
            descriptionText = "Połóż się na plecach z ugiętymi kolanami i stopami na podłodze. Ręce umieść za głową lub skrzyżuj na klatce. Unieś górną część tułowia napinając mięśnie brzucha. Powoli wróć do pozycji wyjściowej.",
            category = "Brzuch"
        ),
        Exercise(
            name = "Plank",
            descriptionText = "Oprzyj się na przedramionach i palcach stóp, utrzymując ciało w linii prostej od głowy do pięt. Napnij mięśnie brzucha i pośladków. Utrzymaj pozycję przez wyznaczony czas, nie pozwalając biorom opadać.",
            category = "Brzuch"
        ),
        Exercise(
            name = "Unoszenie nóg w zwisie",
            descriptionText = "Chwyć drążek nachwytem i zwis na wyprostowanych ramionach. Unieś wyprostowane nogi do poziomu lub wyżej, napinając mięśnie brzucha. Powoli opuść nogi do pozycji wyjściowej kontrolując ruch.",
            category = "Brzuch"
        )
    )

    // Domyślne plany treningowe z przypisanymi ćwiczeniami
    fun defaultPlans(exercises: List<Exercise>): List<WorkoutPlan> {
        fun findExercise(name: String): Exercise? = exercises.find { it.name == name }

        val pushExercises = listOf(
            "Wyciskanie sztangi na ławce" to Triple(4, 8, 60.0),
            "Wyciskanie hantli na ławce skośnej" to Triple(3, 10, 22.0),
            "Wyciskanie sztangi nad głową (OHP)" to Triple(3, 8, 40.0),
            "Unoszenie hantli bokiem" to Triple(3, 15, 8.0),
            "Prostowanie ramion na wyciągu" to Triple(3, 12, 25.0)
        )

        val pullExercises = listOf(
            "Podciąganie na drążku" to Triple(4, 8, 0.0),
            "Wiosłowanie sztangą" to Triple(4, 8, 50.0),
            "Ściąganie drążka wyciągu" to Triple(3, 10, 45.0),
            "Uginanie ramion ze sztangą" to Triple(3, 10, 25.0),
            "Uginanie ramion z hantlami" to Triple(3, 12, 12.0)
        )

        val legExercises = listOf(
            "Przysiady ze sztangą" to Triple(4, 8, 70.0),
            "Wyprosty nóg na maszynie" to Triple(3, 12, 40.0),
            "Uginanie nóg na maszynie" to Triple(3, 12, 30.0),
            "Brzuszki (Crunches)" to Triple(3, 20, 0.0),
            "Plank" to Triple(3, 60, 0.0)
        )

        fun buildPlanExercises(items: List<Pair<String, Triple<Int, Int, Double>>>): MutableList<PlanExercise> {
            return items.mapIndexedNotNull { index, (name, config) ->
                val ex = findExercise(name)
                if (ex != null) {
                    PlanExercise(
                        exerciseId = ex.id,
                        exerciseName = ex.name,
                        orderIndex = index,
                        sets = config.first,
                        reps = config.second,
                        weight = config.third
                    )
                } else null
            }.toMutableList()
        }

        return listOf(
            WorkoutPlan(
                name = "Push (Klatka/Barki/Triceps)",
                notes = "Trening pchnięć — klatka piersiowa, barki i triceps",
                exercises = buildPlanExercises(pushExercises)
            ),
            WorkoutPlan(
                name = "Pull (Plecy/Biceps)",
                notes = "Trening ciągnięć — plecy i biceps",
                exercises = buildPlanExercises(pullExercises)
            ),
            WorkoutPlan(
                name = "Nogi + Brzuch",
                notes = "Trening nóg z ćwiczeniami na brzuch",
                exercises = buildPlanExercises(legExercises)
            )
        )
    }

    // Domyślne zaplanowane treningi na bieżący i przyszły tydzień
    fun defaultScheduledWorkouts(plans: List<WorkoutPlan>): List<ScheduledWorkout> {
        if (plans.size < 3) return emptyList()
        val today = LocalDate.now()
        val push = plans[0]
        val pull = plans[1]
        val legs = plans[2]

        return listOf(
            // Poprzedni tydzień — ukończone
            ScheduledWorkout(date = today.minusDays(7).toString(), planId = push.id, planName = push.name, isCompleted = true),
            ScheduledWorkout(date = today.minusDays(5).toString(), planId = pull.id, planName = pull.name, isCompleted = true),
            ScheduledWorkout(date = today.minusDays(3).toString(), planId = legs.id, planName = legs.name, isCompleted = true),
            // Ten tydzień — zaplanowane
            ScheduledWorkout(date = today.minusDays(1).toString(), planId = push.id, planName = push.name, isCompleted = true),
            ScheduledWorkout(date = today.toString(), planId = pull.id, planName = pull.name, isCompleted = false),
            ScheduledWorkout(date = today.plusDays(2).toString(), planId = legs.id, planName = legs.name, isCompleted = false),
            // Przyszły tydzień
            ScheduledWorkout(date = today.plusDays(4).toString(), planId = push.id, planName = push.name, isCompleted = false),
            ScheduledWorkout(date = today.plusDays(6).toString(), planId = pull.id, planName = pull.name, isCompleted = false),
            ScheduledWorkout(date = today.plusDays(8).toString(), planId = legs.id, planName = legs.name, isCompleted = false)
        )
    }

    // Domyślne logi treningowe (historia ukończonych treningów)
    fun defaultWorkoutLogs(plans: List<WorkoutPlan>): List<WorkoutLog> {
        if (plans.size < 3) return emptyList()
        val today = LocalDate.now()

        fun buildLog(plan: WorkoutPlan, daysAgo: Int, durationMinutes: Int, weightMultiplier: Double): WorkoutLog {
            val entries = plan.exercises.flatMap { pe ->
                (1..pe.sets).map { setNum ->
                    LogEntry(
                        exerciseId = pe.exerciseId,
                        exerciseName = pe.exerciseName,
                        setNumber = setNum,
                        reps = pe.reps,
                        weight = pe.weight * weightMultiplier,
                        isCompleted = true
                    )
                }
            }.toMutableList()

            return WorkoutLog(
                date = today.minusDays(daysAgo.toLong()).toString(),
                durationMinutes = durationMinutes,
                isFinished = true,
                planName = plan.name,
                entries = entries
            )
        }

        return listOf(
            buildLog(plans[0], 7, 55, 0.9),   // Push tydzień temu
            buildLog(plans[1], 5, 50, 0.9),   // Pull
            buildLog(plans[2], 3, 45, 0.9),   // Nogi
            buildLog(plans[0], 1, 60, 1.0),   // Push wczoraj (cięższy!)
        )
    }
}
