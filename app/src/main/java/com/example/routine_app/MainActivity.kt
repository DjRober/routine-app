package com.example.routine_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.routine_app.ui.RoutineApp
import com.example.routine_app.ui.RoutineViewModel
import com.example.routine_app.ui.theme.RoutineappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val vm: RoutineViewModel = viewModel()
            val theme by vm.themeId.collectAsStateWithLifecycle()
            RoutineappTheme(themeId = theme) {
                RoutineApp(vm)
            }
        }
    }
}
