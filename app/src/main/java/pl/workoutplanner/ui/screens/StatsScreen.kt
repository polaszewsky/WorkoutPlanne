package pl.workoutplanner.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.workoutplanner.models.Exercise
import pl.workoutplanner.models.WorkoutLog
import pl.workoutplanner.viewmodels.CalendarViewModel
import pl.workoutplanner.viewmodels.ExerciseViewModel
import pl.workoutplanner.viewmodels.StatsViewModel
import pl.workoutplanner.viewmodels.WorkoutViewModel
import pl.workoutplanner.ui.components.StatCard
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel,
    calendarViewModel: CalendarViewModel,
    workoutViewModel: WorkoutViewModel,
    exerciseViewModel: ExerciseViewModel
) {
    val polishMonthsGenitive = arrayOf(
        "stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca",
        "lipca", "sierpnia", "września", "października", "listopada", "grudnia"
    )

    LaunchedEffect(Unit) {
        statsViewModel.loadStats()
        exerciseViewModel.loadExercises()
    }

    val totalWorkouts = statsViewModel.totalWorkouts()
    val totalMinutes = statsViewModel.totalMinutes()
    val thisWeek = statsViewModel.weeklyCount()
    val thisMonth = statsViewModel.monthlyCount()

    var selectedExercise by remember { mutableStateOf<Exercise?>(null) }
    var exerciseDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Statystyki",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Twoje postępy",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Stats grid - Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                value = totalWorkouts.toString(),
                label = "Treningi ogółem",
                icon = Icons.Filled.FitnessCenter,
                iconTint = Color(0xFF6C63FF),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = totalMinutes.toString(),
                label = "Minuty ogółem",
                icon = Icons.Filled.Timer,
                iconTint = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stats grid - Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                value = thisWeek.toString(),
                label = "Ten tydzień",
                icon = Icons.Filled.DateRange,
                iconTint = Color(0xFFFFC107),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = thisMonth.toString(),
                label = "Ten miesiąc",
                icon = Icons.Filled.DateRange,
                iconTint = Color(0xFFFF7043),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Exercise picker
        Text(
            text = "Progresja ciężaru",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        ExposedDropdownMenuBox(
            expanded = exerciseDropdownExpanded,
            onExpandedChange = { exerciseDropdownExpanded = !exerciseDropdownExpanded }
        ) {
            OutlinedTextField(
                value = selectedExercise?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Wybierz ćwiczenie", color = Color.Gray) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = exerciseDropdownExpanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            ExposedDropdownMenu(
                expanded = exerciseDropdownExpanded,
                onDismissRequest = { exerciseDropdownExpanded = false },
                containerColor = Color(0xFF2A2A3E)
            ) {
                exerciseViewModel.exercises.forEach { exercise ->
                    DropdownMenuItem(
                        text = { Text(exercise.name, color = Color.White) },
                        onClick = {
                            selectedExercise = exercise
                            exerciseDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Weight progression chart
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E1E2E),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedExercise != null) {
                    val data = statsViewModel.progressData(selectedExercise!!.id)

                    if (data.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Brak danych",
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                    } else {
                        // Chart
                        val primaryColor = Color(0xFF6C63FF)

                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(start = 40.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
                        ) {
                            val weights = data.map { it.second }
                            val minWeight = (weights.minOrNull() ?: 0.0) - 5.0
                            val maxWeight = (weights.maxOrNull() ?: 100.0) + 5.0
                            val weightRange = if (maxWeight - minWeight == 0.0) 1.0 else maxWeight - minWeight

                            val chartWidth = size.width
                            val chartHeight = size.height

                            // Draw grid lines
                            val gridLineCount = 4
                            for (i in 0..gridLineCount) {
                                val y = chartHeight - (chartHeight * i / gridLineCount)
                                val weightLabel = minWeight + (weightRange * i / gridLineCount)

                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.2f),
                                    start = Offset(0f, y),
                                    end = Offset(chartWidth, y),
                                    strokeWidth = 1f
                                )

                                // Draw weight labels
                                drawContext.canvas.nativeCanvas.drawText(
                                    "${weightLabel.toInt()} kg",
                                    -35.dp.toPx(),
                                    y + 4.dp.toPx(),
                                    android.graphics.Paint().apply {
                                        color = android.graphics.Color.GRAY
                                        textSize = 10.sp.toPx()
                                        textAlign = android.graphics.Paint.Align.LEFT
                                    }
                                )
                            }

                            // Calculate points
                            val points = data.mapIndexed { index, (_, weight) ->
                                val x = if (data.size == 1) chartWidth / 2f
                                else chartWidth * index / (data.size - 1)
                                val y = chartHeight - ((weight - minWeight) / weightRange * chartHeight).toFloat()
                                Offset(x, y)
                            }

                            // Draw line
                            if (points.size > 1) {
                                val path = Path()
                                path.moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    path.lineTo(points[i].x, points[i].y)
                                }
                                drawPath(
                                    path = path,
                                    color = primaryColor,
                                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                                )
                            }

                            // Draw points
                            points.forEach { point ->
                                drawCircle(
                                    color = primaryColor,
                                    radius = 6f,
                                    center = point
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 3f,
                                    center = point
                                )
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Wybierz ćwiczenie aby zobaczyć progresję",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // History section
        Text(
            text = "Historia treningów",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        val history = statsViewModel.completedWorkouts().sortedByDescending { it.date }

        if (history.isEmpty()) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1E1E2E),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Brak ukończonych treningów",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            history.forEach { workout ->
                val dateStr = try {
                    val date = LocalDate.parse(workout.date)
                    "${date.dayOfMonth} ${polishMonthsGenitive[date.monthValue - 1]} ${date.year}"
                } catch (e: Exception) {
                    workout.date
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E1E2E),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
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
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dateStr,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (workout.durationMinutes > 0) {
                                Text(
                                    text = "${workout.durationMinutes} min",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF6C63FF)
                                )
                            }
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = "Ukończony",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
