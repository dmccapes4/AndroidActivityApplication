// MainActivity.kt
package com.example.androidactivityapplication

// Import necessary Android and Jetpack Compose libraries
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * MainActivity serves as the entry point of the application.
 * It inherits from ComponentActivity, which is a base class for activities 
 * that use Jetpack Compose for their UI.
 */
open class MainActivity : ComponentActivity() {
    /**
     * onCreate is the first lifecycle method called when the activity is created.
     * We use setContent to define the UI layout using Compose functions.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Launch the main screen and pass the current activity instance
            // so we can use it to start other activities.
            MainScreen(this)
        }
    }
}

/**
 * MainScreen is a Composable function that defines the primary UI.
 * @param activity The instance of MainActivity used for navigation via Intents.
 */
@Composable
fun MainScreen(activity: MainActivity) {
    // Define a list of colors to be used for the circular buttons
    val colors = listOf(
        Color.Red, Color.Blue, Color.Green,
        Color.Yellow, Color.DarkGray, Color.LightGray,
        Color.Gray, Color.Cyan, Color.Magenta
    )

    // A Column arranges its children vertically. 
    // Here it acts as the root container with padding and filling the full screen.
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // LazyColumn is an efficient way to display a list of items vertically.
        // It only renders the items currently visible on the screen.
        LazyColumn {
            // We create 3 rows of buttons.
            items(3) { row ->
                // Row arranges its children horizontally.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Loop to create 3 buttons per row (total 9 buttons)
                    for (col in 0..2) {
                        val index = row * 3 + col
                        ColoredButton(
                            color = colors[index],
                            onClick = {
                                // Define navigation logic based on which button was clicked
                                when (index) {
                                    0 -> { // Red button: Open AntsActivity
                                        val intent = Intent(activity, AntsActivity::class.java)
                                        activity.startActivity(intent)
                                    }
                                    1 -> { // Blue button: Open WindActivity
                                        val intent = Intent(activity, WindActivity::class.java)
                                        activity.startActivity(intent)
                                    }
                                    2 -> { // Green button: Open TabActivity
                                        val intent = Intent(activity, TabActivity::class.java)
                                        activity.startActivity(intent)
                                    }
                                    3 -> { // button: Open TimerActivity
                                        val intent = Intent(activity, TimerActivity::class.java)
                                        activity.startActivity(intent)
                                    }
                                    4 -> { // button: Open WeatherActivity
                                        val intent = Intent(activity, WeatherActivity::class.java)
                                        activity.startActivity(intent)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * ColoredButton creates a custom circular button using Compose.
 * @param color The background color of the button.
 * @param onClick The action to perform when the button is clicked.
 */
@Composable
fun ColoredButton(
    color: Color,
    onClick: () -> Unit = {}
) {
    // Box is a layout container that stacks elements on top of each other.
    Box(
        modifier = Modifier
            .size(100.dp) // Set a fixed size for the button
            .background(color, shape = CircleShape) // Apply the color and circular shape
            .padding(4.dp)
            .clickable { onClick() } // Make the entire circle clickable
    ) {
        // Text element centered inside the Box
        Text(
            text = "Btn",
            color = Color.White,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.Center)
        )
    }
}

/**
 * Preview function to allow developers to see the UI layout in the 
 * Android Studio Design tab without running the app on a device.
 */
@Preview(showSystemUi = true)
@Composable
fun MainScreenPreview() {
    // We pass a dummy object for the preview since we don't have a real activity context.
    MainScreen(activity = object : MainActivity() {})
}
