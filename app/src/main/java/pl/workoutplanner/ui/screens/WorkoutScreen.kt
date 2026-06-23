package pl.workoutplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pl.workoutplanner.viewmodels.CalendarViewModel
import pl.workoutplanner.viewmodels.PlanViewModel
import pl.workoutplanner.viewmodels.WorkoutViewModel
import pl.workoutplanner.models.ScheduledWorkout
import java.time.LocalDate

@Composable
fun WorkoutScreen(
    workoutViewModel: WorkoutViewModel,
    calendarViewModel: CalendarViewModel,
    planViewModel: PlanViewModel
) {
    val currentLog = workoutViewModel.currentLog.value

    LaunchedEffect(Unit) {
        workoutViewModel.loadLogs()
        calendarViewModel.loadScheduledWorkouts()
        planViewModel.loadPlans()
    }

    if (currentLog == null) {
        NoActiveWorkoutView(workoutViewModel, calendarViewModel, planViewModel)
    } else {
        ActiveWorkoutView(workoutViewModel, calendarViewModel)
    }
}

@Composable
private fun NoActiveWorkoutView(
    workoutViewModel: WorkoutViewModel,
    calendarViewModel: CalendarViewModel,
    planViewModel: PlanViewModel
) {
    val allWorkouts = calendarViewModel.scheduledWorkouts
        .sortedWith(compareBy<ScheduledWorkout> { it.isCompleted }.thenBy { it.date })
    val polishMonthsGenitive = arrayOf(
        "stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca",
        "lipca", "sierpnia", "września", "października", "listopada", "grudnia"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Trening",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Rozpocznij trening",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (allWorkouts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.FitnessCenter,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Brak zaplanowanych treningów",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Zaplanuj trening w kalendarzu",
                        fontSize = 14.sp,
                        color = Color.Gray.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(allWorkouts) { workout ->
                    val plan = planViewModel.plans.find { it.id == workout.planId }
                        ?: planViewModel.plans.find { it.name == workout.planName }
                    val dateStr = try {
                        val date = LocalDate.parse(workout.date)
                        "${date.dayOfMonth} ${polishMonthsGenitive[date.monthValue - 1]} ${date.year}"
                    } catch (e: Exception) {
                        workout.date
                    }
                    val exerciseCount = plan?.exercises?.size ?: 0

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E1E2E),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = workout.planName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = dateStr,
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$exerciseCount ćwiczeń",
                                    fontSize = 13.sp,
                                    color = Color(0xFF6C63FF)
                                )
                            }
                            if (workout.isCompleted) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF4CAF50).copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.CheckCircle,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            "Ukończony",
                                            fontSize = 13.sp,
                                            color = Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            } else {
                                Button(
                                    onClick = {
                                        plan?.let {
                                            workoutViewModel.startWorkout(workout, it.exercises)
                                        }
                                    },
                                    enabled = plan != null && exerciseCount > 0,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
                                ) {
                                    Icon(
                                        Icons.Filled.PlayArrow,
                                        contentDescription = "Rozpocznij",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Rozpocznij")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ActiveWorkoutView(
    workoutViewModel: WorkoutViewModel,
    calendarViewModel: CalendarViewModel
) {
    val currentLog = workoutViewModel.currentLog.value ?: return
    val elapsedSeconds = workoutViewModel.elapsedSeconds.value

    var showSummaryDialog by remember { mutableStateOf(false) }
    var summaryTotalSets by remember { mutableIntStateOf(0) }
    var summaryDuration by remember { mutableIntStateOf(0) }

    // Timer
    LaunchedEffect(currentLog) {
        while (workoutViewModel.currentLog.value != null) {
            delay(1000)
            workoutViewModel.elapsedSeconds.value++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Timer display
        Text(
            text = "${elapsedSeconds / 60}:${"%02d".format(elapsedSeconds % 60)}",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6C63FF),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        Text(
            text = currentLog.planName,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Group entries by exerciseName
        val entriesByExercise = currentLog.entries.withIndex().groupBy { it.value.exerciseName }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            entriesByExercise.forEach { (exerciseName, indexedEntries) ->
                item(key = exerciseName) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF1E1E2E),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = exerciseName,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Previous weight hint
                            val exerciseId = indexedEntries.first().value.exerciseId
                            val lastVals = workoutViewModel.lastValues(exerciseId)
                            if (lastVals != null) {
                                Text(
                                    text = "Poprzednio: ${lastVals.first} powt. × ${if (lastVals.second % 1.0 == 0.0) lastVals.second.toInt().toString() else lastVals.second.toString()} kg",
                                    fontSize = 13.sp,
                                    color = Color.Gray,
                                    fontStyle = FontStyle.Italic,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                                )
                            } else {
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Sets
                            indexedEntries.forEach { (entryIndex, entry) ->
                                var repsText by remember { mutableStateOf(entry.reps.toString()) }
                                var weightText by remember { mutableStateOf(
                                    if (entry.weight % 1.0 == 0.0) entry.weight.toInt().toString() else entry.weight.toString()
                                ) }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .then(
                                            if (entry.isCompleted) Modifier.background(
                                                Color(0xFF4CAF50).copy(alpha = 0.08f),
                                                RoundedCornerShape(8.dp)
                                            ) else Modifier
                                        )
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Seria ${entry.setNumber}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (entry.isCompleted) Color(0xFF4CAF50) else Color.White,
                                        modifier = Modifier.width(56.dp)
                                    )

                                    OutlinedTextField(
                                        value = repsText,
                                        onValueChange = { repsText = it },
                                        modifier = Modifier.width(70.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true,
                                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, textAlign = TextAlign.Center),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF6C63FF),
                                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        label = { Text("Powt.", fontSize = 10.sp, color = Color.Gray) }
                                    )

                                    OutlinedTextField(
                                        value = weightText,
                                        onValueChange = { weightText = it },
                                        modifier = Modifier.width(80.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        singleLine = true,
                                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, textAlign = TextAlign.Center),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFF6C63FF),
                                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        ),
                                        label = { Text("Waga", fontSize = 10.sp, color = Color.Gray) }
                                    )

                                    IconButton(
                                        onClick = {
                                            val r = repsText.toIntOrNull() ?: entry.reps
                                            val w = weightText.toDoubleOrNull() ?: entry.weight
                                            workoutViewModel.updateEntry(entryIndex, r, w)
                                            workoutViewModel.toggleEntryCompleted(entryIndex)
                                        }
                                    ) {
                                        Icon(
                                            if (entry.isCompleted) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                            contentDescription = if (entry.isCompleted) "Ukończone" else "Oznacz jako ukończone",
                                            tint = if (entry.isCompleted) Color(0xFF4CAF50) else Color.Gray,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Finish button
        Button(
            onClick = {
                val durationMin = elapsedSeconds / 60
                summaryTotalSets = currentLog.entries.count { it.isCompleted }
                summaryDuration = durationMin
                workoutViewModel.finishWorkout(durationMin)
                // Mark scheduled workout as completed
                val scheduledId = currentLog.scheduledWorkoutId
                if (scheduledId != null) {
                    val scheduled = calendarViewModel.scheduledWorkouts.find { it.id == scheduledId }
                    if (scheduled != null) {
                        calendarViewModel.markCompleted(scheduled)
                    }
                }
                showSummaryDialog = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
        ) {
            Text(
                "Zakończ trening",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Summary dialog
    if (showSummaryDialog) {
        AlertDialog(
            onDismissRequest = { showSummaryDialog = false },
            containerColor = Color(0xFF1E1E2E),
            title = {
                Text(
                    "Trening zakończony! \uD83C\uDF89",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.White
                )
            },
            text = {
                Text(
                    "Ukończone serie: $summaryTotalSets\nCzas trwania: $summaryDuration min",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    lineHeight = 24.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showSummaryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
                ) {
                    Text("Świetnie!", fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}
