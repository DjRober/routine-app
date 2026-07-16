package com.example.routine_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.routine_app.ui.RoutineApp
import com.example.routine_app.ui.theme.RoutineappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RoutineappTheme {
                RoutineApp()
            }
        }
    }
}
