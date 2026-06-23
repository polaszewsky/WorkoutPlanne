package pl.workoutplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.workoutplanner.viewmodels.CalendarViewModel
import pl.workoutplanner.viewmodels.PlanViewModel
import pl.workoutplanner.models.WorkoutPlan
import pl.workoutplanner.models.ScheduledWorkout
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel,
    planViewModel: PlanViewModel
) {
    val polishMonths = arrayOf(
        "Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec",
        "Lipiec", "Sierpień", "Wrzesień", "Październik", "Listopad", "Grudzień"
    )
    val polishMonthsGenitive = arrayOf(
        "stycznia", "lutego", "marca", "kwietnia", "maja", "czerwca",
        "lipca", "sierpnia", "września", "października", "listopada", "grudnia"
    )
    val dayHeaders = listOf("Pn", "Wt", "Śr", "Cz", "Pt", "So", "Nd")

    val currentMonth = calendarViewModel.currentMonth.value
    val daysWithWorkouts = calendarViewModel.daysWithWorkouts()
    val daysWithCompleted = calendarViewModel.daysWithCompletedWorkouts()
    val today = LocalDate.now()

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(Unit) {
        calendarViewModel.loadScheduledWorkouts()
        planViewModel.loadPlans()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Kalendarz",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = "Planuj swoje treningi",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Month navigation
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1E1E2E),
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { calendarViewModel.goToPreviousMonth() }) {
                        Icon(
                            Icons.Filled.ChevronLeft,
                            contentDescription = "Poprzedni miesiąc",
                            tint = Color.White
                        )
                    }
                    Text(
                        text = "${polishMonths[currentMonth.monthValue - 1]} ${currentMonth.year}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    IconButton(onClick = { calendarViewModel.goToNextMonth() }) {
                        Icon(
                            Icons.Filled.ChevronRight,
                            contentDescription = "Następny miesiąc",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Day headers
                Row(modifier = Modifier.fillMaxWidth()) {
                    dayHeaders.forEach { day ->
                        Text(
                            text = day,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Calendar grid
                val firstDayOffset = currentMonth.atDay(1).dayOfWeek.value - 1
                val daysInMonth = currentMonth.lengthOfMonth()

                for (row in 0 until 6) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0..6) {
                            val cellIndex = row * 7 + col
                            val dayNum = cellIndex - firstDayOffset + 1

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .size(48.dp)
                                    .then(
                                        if (dayNum in 1..daysInMonth) {
                                            Modifier.clickable {
                                                selectedDate = currentMonth.atDay(dayNum)
                                            }
                                        } else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayNum in 1..daysInMonth) {
                                    val date = currentMonth.atDay(dayNum)
                                    val isToday = date == today
                                    val hasWorkout = dayNum in daysWithWorkouts
                                    val hasCompleted = dayNum in daysWithCompleted

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .then(
                                                    if (isToday) {
                                                        Modifier.border(
                                                            2.dp,
                                                            Color(0xFF6C63FF),
                                                            CircleShape
                                                        )
                                                    } else Modifier
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = dayNum.toString(),
                                                fontSize = 14.sp,
                                                color = Color.White,
                                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }

                                        if (hasWorkout) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (hasCompleted) Color(0xFF4CAF50)
                                                        else Color(0xFF1E88E5)
                                                    )
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.height(8.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E1E2E),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E88E5))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Zaplanowany", fontSize = 12.sp, color = Color.Gray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ukończony", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }

    // Bottom Sheet
    if (selectedDate != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedDate = null },
            sheetState = sheetState,
            containerColor = Color(0xFF1E1E2E)
        ) {
            val date = selectedDate!!
            val dateWorkouts = calendarViewModel.workoutsForDate(date)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "${date.dayOfMonth} ${polishMonthsGenitive[date.monthValue - 1]} ${date.year}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (dateWorkouts.isNotEmpty()) {
                    dateWorkouts.forEach { workout ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF2A2A3E),
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
                                Text(
                                    text = workout.planName,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (workout.isCompleted) Color(0xFF4CAF50).copy(alpha = 0.15f)
                                    else Color(0xFFFFC107).copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            if (workout.isCompleted) Icons.Filled.Check else Icons.Filled.Schedule,
                                            contentDescription = null,
                                            tint = if (workout.isCompleted) Color(0xFF4CAF50) else Color(0xFFFFC107),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = if (workout.isCompleted) "Ukończony" else "Zaplanowany",
                                            fontSize = 12.sp,
                                            color = if (workout.isCompleted) Color(0xFF4CAF50) else Color(0xFFFFC107)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = "Brak treningów na ten dzień",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { showScheduleDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Zaplanuj trening", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }

    // Schedule dialog
    if (showScheduleDialog && selectedDate != null) {
        var selectedPlan by remember { mutableStateOf<WorkoutPlan?>(null) }

        AlertDialog(
            onDismissRequest = { showScheduleDialog = false },
            containerColor = Color(0xFF1E1E2E),
            title = {
                Text(
                    "Zaplanuj trening",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Column {
                    Text(
                        text = "Wybierz plan treningowy:",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    if (planViewModel.plans.isEmpty()) {
                        Text(
                            text = "Brak planów. Najpierw stwórz plan w zakładce Plany.",
                            fontSize = 13.sp,
                            color = Color(0xFFFFC107),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        planViewModel.plans.forEach { plan ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedPlan?.id == plan.id) Color(0xFF6C63FF).copy(alpha = 0.2f)
                                        else Color(0xFF2A2A3E),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clickable { selectedPlan = plan }
                                    .then(
                                        if (selectedPlan?.id == plan.id)
                                            Modifier.border(1.5.dp, Color(0xFF6C63FF), RoundedCornerShape(12.dp))
                                        else Modifier
                                    )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedPlan?.id == plan.id,
                                        onClick = { selectedPlan = plan },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFF6C63FF),
                                            unselectedColor = Color.Gray
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = plan.name,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "${plan.exercises.size} ćwiczeń",
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedPlan?.let { plan ->
                            selectedDate?.let { date ->
                                calendarViewModel.scheduleWorkout(plan, date)
                            }
                        }
                        showScheduleDialog = false
                    },
                    enabled = selectedPlan != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
                ) {
                    Text("Zaplanuj")
                }
            },
            dismissButton = {
                TextButton(onClick = { showScheduleDialog = false }) {
                    Text("Anuluj", color = Color.Gray)
                }
            }
        )
    }
}
