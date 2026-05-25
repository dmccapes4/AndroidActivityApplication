package com.example.androidactivityapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * TimerActivity demonstrates how to manage background tasks (timers) 
 * using Coroutines and update Compose state to reflect changes in the UI.
 */
class TimerActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Entry point for our Compose UI
            TimerScreen()
        }
    }

    @Composable
    fun TimerScreen() {
        /**
         * We use 'mutableStateOf' wrapped in 'remember' to keep track of our timer.
         * When 'timerState' changes, Compose will automatically re-render the screen.
         */
        var timerState by remember { mutableStateOf(TimerState()) }

        /**
         * 'rememberCoroutineScope' provides a CoroutineScope bound to this Composable's lifecycle.
         * It is used to launch background tasks (like timers) from non-composable 
         * contexts like button click listeners.
         */
        val scope = rememberCoroutineScope()
        
        // We keep track of the current active job so we can cancel it if a new timer starts.
        var activeTimerJob by remember { mutableStateOf<Job?>(null) }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Select a timer duration (seconds):")

            Row {
                val durations = listOf("1", "2", "3")
                for (duration in durations) {
                    Button(
                        modifier = Modifier.padding(8.dp),
                        onClick = {
                            // Cancel any existing timer before starting a new one
                            activeTimerJob?.cancel()
                            
                            // Launch a new coroutine to handle the timer logic
                            activeTimerJob = scope.launch {
                                // 1. Set initial state: running and countdown value
                                timerState = TimerState(
                                    currentValue = duration, 
                                    isRunning = true
                                )
                                
                                // 2. Wait for the specified amount of time (converted to milliseconds)
                                delay(duration.toLong() * 1000L)
                                
                                // 3. Update state: timer is no longer running
                                timerState = timerState.copy(isRunning = false)
                            }
                        }
                    ) {
                        Text(text = duration)
                    }
                }
            }

            // Display area for the timer status
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (timerState.isRunning) {
                    Text(text = "Timer is running: Waiting ${timerState.currentValue}s...")
                } else {
                    Text(text = "No timer is running")
                }
            }
        }
    }
}

/**
 * TimerState represents the UI state of the timer.
 * Using an immutable data class is best practice in Compose.
 */
data class TimerState(
    val currentValue: String = "",
    val isRunning: Boolean = false
)
