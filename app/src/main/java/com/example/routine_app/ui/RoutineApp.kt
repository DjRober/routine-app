package com.example.routine_app.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.routine_app.ui.screens.DataScreen
import com.example.routine_app.ui.screens.GoalsScreen
import com.example.routine_app.ui.screens.TodayScreen
import com.example.routine_app.ui.screens.WeekScreen

private enum class Dest(val route: String, val label: String, val icon: ImageVector) {
    TODAY("today", "Hoy", Icons.Filled.Today),
    WEEK("week", "Semana", Icons.Filled.CalendarMonth),
    GOALS("goals", "Metas", Icons.Filled.TrackChanges),
    DATA("data", "Datos", Icons.Filled.Storage),
}

@Composable
fun RoutineApp() {
    val navController = rememberNavController()
    val vm: RoutineViewModel = viewModel()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStack by navController.currentBackStackEntryAsState()
                val current = backStack?.destination
                Dest.entries.forEach { dest ->
                    val selected = current?.hierarchy?.any { it.route == dest.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Dest.TODAY.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Dest.TODAY.route) { TodayScreen(vm) }
            composable(Dest.WEEK.route) { WeekScreen(vm) }
            composable(Dest.GOALS.route) { GoalsScreen(vm) }
            composable(Dest.DATA.route) { DataScreen(vm) }
        }
    }
}
