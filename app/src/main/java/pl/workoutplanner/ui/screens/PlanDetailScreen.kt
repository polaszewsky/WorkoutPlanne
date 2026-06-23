package pl.workoutplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.workoutplanner.models.Exercise
import pl.workoutplanner.models.PlanExercise
import pl.workoutplanner.viewmodels.ExerciseViewModel
import pl.workoutplanner.viewmodels.PlanViewModel
import pl.workoutplanner.ui.components.CategoryBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanDetailScreen(
    planId: String,
    planViewModel: PlanViewModel,
    exerciseViewModel: ExerciseViewModel,
    onDone: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        planViewModel.loadPlans()
        exerciseViewModel.loadExercises()
    }

    val plan = planViewModel.plans.find { it.id == planId }

    if (plan == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Plan nie znaleziony",
                color = Color.White,
                fontSize = 18.sp
            )
        }
        return
    }

    var showExercisePickerDialog by remember { mutableStateOf(false) }
    var selectedExerciseForAdd by remember { mutableStateOf<Exercise?>(null) }
    var editingIndex by remember { mutableIntStateOf(-1) }
    var editingPlanExercise by remember { mutableStateOf<PlanExercise?>(null) }

    Scaffold(
        containerColor = Color(0xFF121212),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showExercisePickerDialog = true },
                containerColor = Color(0xFF6C63FF),
                contentColor = Color.White
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Dodaj ćwiczenie do planu")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = plan.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = { onDone() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Gotowe", fontWeight = FontWeight.SemiBold)
                }
            }

            if (plan.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = plan.notes,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (plan.exercises.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.FitnessCenter,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Brak ćwiczeń w planie",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Dodaj ćwiczenia do planu treningowego",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    itemsIndexed(plan.exercises) { index, pe ->
                        PlanExerciseCard(
                            index = index,
                            planExercise = pe,
                            onClick = {
                                editingIndex = index
                                editingPlanExercise = pe
                            },
                            onDelete = {
                                planViewModel.removeExerciseFromPlan(plan, pe)
                            }
                        )
                    }
                }
            }
        }
    }

    // Edit Dialog
    if (editingPlanExercise != null && editingIndex >= 0) {
        EditExerciseDialog(
            planExercise = editingPlanExercise!!,
            onDismiss = {
                editingIndex = -1
                editingPlanExercise = null
            },
            onSave = { sets, reps, weight ->
                planViewModel.updatePlanExercise(plan, editingPlanExercise!!, sets, reps, weight)
                editingIndex = -1
                editingPlanExercise = null
            }
        )
    }

    // Exercise Picker Dialog
    if (showExercisePickerDialog) {
        ExercisePickerDialog(
            exercises = exerciseViewModel.exercises,
            onDismiss = { showExercisePickerDialog = false },
            onSelect = { exercise ->
                showExercisePickerDialog = false
                selectedExerciseForAdd = exercise
            }
        )
    }

    // Add Sets/Reps/Weight Dialog
    selectedExerciseForAdd?.let { exercise ->
        AddExerciseToPlanDialog(
            exercise = exercise,
            onDismiss = { selectedExerciseForAdd = null },
            onAdd = { sets, reps, weight ->
                planViewModel.addExerciseToPlan(plan, exercise, sets, reps, weight)
                selectedExerciseForAdd = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanExerciseCard(
    index: Int,
    planExercise: PlanExercise,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFCF6679)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Filled.Delete,
                    contentDescription = "Usuń",
                    tint = Color.White,
                    modifier = Modifier.padding(end = 20.dp)
                )
            }
        },
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E2E)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}.",
                        color = Color(0xFF6C63FF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = planExercise.exerciseName,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${planExercise.sets} serii × ${planExercise.reps} powtórzeń × ${planExercise.weight} kg",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun EditExerciseDialog(
    planExercise: PlanExercise,
    onDismiss: () -> Unit,
    onSave: (Int, Int, Double) -> Unit
) {
    var sets by remember { mutableStateOf(planExercise.sets.toString()) }
    var reps by remember { mutableStateOf(planExercise.reps.toString()) }
    var weight by remember { mutableStateOf(planExercise.weight.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E2E),
        titleContentColor = Color.White,
        title = {
            Text(
                text = planExercise.exerciseName,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = { Text("Serie", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFF2A2A3E),
                        cursorColor = Color(0xFF6C63FF)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Powtórzenia", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFF2A2A3E),
                        cursorColor = Color(0xFF6C63FF)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Ciężar (kg)", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFF2A2A3E),
                        cursorColor = Color(0xFF6C63FF)
                    ),
                    singleLine = true
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = Color.Gray)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val s = sets.toIntOrNull() ?: planExercise.sets
                    val r = reps.toIntOrNull() ?: planExercise.reps
                    val w = weight.toDoubleOrNull() ?: planExercise.weight
                    onSave(s, r, w)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF)
                )
            ) {
                Text("Zapisz")
            }
        }
    )
}

@Composable
private fun ExercisePickerDialog(
    exercises: List<Exercise>,
    onDismiss: () -> Unit,
    onSelect: (Exercise) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(searchQuery, exercises) {
        if (searchQuery.isBlank()) exercises
        else exercises.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E2E),
        titleContentColor = Color.White,
        title = {
            Text(
                text = "Wybierz ćwiczenie",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Szukaj...", color = Color.Gray) },
                    leadingIcon = {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Szukaj",
                            tint = Color.Gray
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF2A2A3E),
                        unfocusedContainerColor = Color(0xFF2A2A3E),
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = Color(0xFF6C63FF)
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filtered) { exercise ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onSelect(exercise) }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = exercise.name,
                                color = Color.White,
                                fontSize = 15.sp,
                                modifier = Modifier.weight(1f),
                                maxLines = 2
                            )

                            CategoryBadge(category = exercise.category)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = Color.Gray)
            }
        }
    )
}

@Composable
private fun AddExerciseToPlanDialog(
    exercise: Exercise,
    onDismiss: () -> Unit,
    onAdd: (Int, Int, Double) -> Unit
) {
    var sets by remember { mutableStateOf("3") }
    var reps by remember { mutableStateOf("10") }
    var weight by remember { mutableStateOf("0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1E1E2E),
        titleContentColor = Color.White,
        title = {
            Text(
                text = exercise.name,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = sets,
                    onValueChange = { sets = it },
                    label = { Text("Serie", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFF2A2A3E),
                        cursorColor = Color(0xFF6C63FF)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Powtórzenia", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFF2A2A3E),
                        cursorColor = Color(0xFF6C63FF)
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = { weight = it },
                    label = { Text("Ciężar (kg)", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF6C63FF),
                        unfocusedBorderColor = Color(0xFF2A2A3E),
                        cursorColor = Color(0xFF6C63FF)
                    ),
                    singleLine = true
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj", color = Color.Gray)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val s = sets.toIntOrNull() ?: 3
                    val r = reps.toIntOrNull() ?: 10
                    val w = weight.toDoubleOrNull() ?: 0.0
                    onAdd(s, r, w)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6C63FF)
                )
            ) {
                Text("Dodaj")
            }
        }
    )
}
