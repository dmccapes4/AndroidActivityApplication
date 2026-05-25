package com.example.androidactivityapplication

// Imports for activity, state management, and Material Design components
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * TabActivity showcases a simple implementation of Bottom Navigation using Compose.
 * It toggles between three content views based on the selected tab.
 */
class TabActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Set the UI entry point for this activity.
            TabScreen()
        }
    }
}

/**
 * TabScreen manages the UI state and layout for the tab-based interface.
 */
@Composable
fun TabScreen() {
    /**
     * selectedTab holds the current index of the active tab (0, 1, or 2).
     * remember { mutableIntStateOf(0) } ensures the state persists during 
     * recomposition of the Composable.
     */
    var selectedTab by remember { mutableIntStateOf(0) }
    
    // Define the labels and content for each tab
    val tabs = listOf("X", "Y", "Z")
    val content = listOf(
        "Xenolith: A piece of rock of different origin from the igneous rock in which it is embedded.",
        "Yesteryear: Last year or the recent past.",
        "Zephyr: A gentle, mild breeze."
    )

    /**
     * Scaffold is a top-level Composable that provides a standard screen structure,
     * such as slots for a TopBar, BottomBar, and the main content area.
     */
    Scaffold(
        bottomBar = {
            // NavigationBar is the Material3 bottom navigation container.
            NavigationBar {
                // Iterate through the tab labels to create individual items
                tabs.forEachIndexed { index, label ->
                    NavigationBarItem(
                        // If this item's index matches the state, it will look 'selected'
                        selected = selectedTab == index,
                        // When clicked, update the state variable to trigger a UI refresh
                        onClick = { selectedTab = index },
                        // Label text shown below the icon slot
                        label = { Text(label) },
                        // An icon is required by the API, so we provide an empty box or placeholder
                        icon = { /* Icons could be added here */ }
                    )
                }
            }
        }
    ) { innerPadding ->
        /**
         * Main Content Area
         * The 'innerPadding' provided by the Scaffold ensures that our content
         * is not obscured by the bottom navigation bar.
         */
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Display the specific definition corresponding to the selected index.
            Text(
                text = content[selectedTab],
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
