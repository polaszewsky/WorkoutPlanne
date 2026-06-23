package pl.workoutplanner.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pl.workoutplanner.viewmodels.*
import pl.workoutplanner.ui.screens.*

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun WorkoutPlannerApp() {
    val navController = rememberNavController()
    val exerciseViewModel: ExerciseViewModel = viewModel()
    val planViewModel: PlanViewModel = viewModel()
    val calendarViewModel: CalendarViewModel = viewModel()
    val workoutViewModel: WorkoutViewModel = viewModel()
    val statsViewModel: StatsViewModel = viewModel()

    val items = listOf(
        BottomNavItem("Ćwiczenia", Icons.Filled.FitnessCenter, "exercises"),
        BottomNavItem("Plany", Icons.Filled.List, "plans"),
        BottomNavItem("Kalendarz", Icons.Filled.DateRange, "calendar"),
        BottomNavItem("Trening", Icons.Filled.PlayArrow, "workout"),
        BottomNavItem("Statystyki", Icons.Filled.Star, "stats")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF6C63FF),
            onPrimary = Color.White,
            background = Color(0xFF121212),
            surface = Color(0xFF1E1E2E),
            onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Scaffold(
            containerColor = Color(0xFF121212),
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF1A1A2E),
                    tonalElevation = 0.dp
                ) {
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF6C63FF),
                                selectedTextColor = Color(0xFF6C63FF),
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0xFF6C63FF).copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = "exercises",
                modifier = Modifier.padding(padding)
            ) {
                composable("exercises") {
                    ExerciseListScreen(exerciseViewModel)
                }
                composable("plans") {
                    PlanListScreen(planViewModel) { planId ->
                        navController.navigate("plan_detail/$planId")
                    }
                }
                composable("plan_detail/{planId}") { backStackEntry ->
                    val planId = backStackEntry.arguments?.getString("planId") ?: ""
                    PlanDetailScreen(planId, planViewModel, exerciseViewModel) {
                        navController.popBackStack()
                    }
                }
                composable("calendar") {
                    CalendarScreen(calendarViewModel, planViewModel)
                }
                composable("workout") {
                    WorkoutScreen(workoutViewModel, calendarViewModel, planViewModel)
                }
                composable("stats") {
                    StatsScreen(statsViewModel, calendarViewModel, workoutViewModel, exerciseViewModel)
                }
            }
        }
    }
}
