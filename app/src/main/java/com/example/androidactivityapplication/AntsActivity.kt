package com.example.androidactivityapplication

// Standard Android and Compose imports for UI and lifecycle management
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * AntsActivity is a simple activity that displays educational information about ants.
 */
class AntsActivity : ComponentActivity() {
    /**
     * Called when the activity is starting. This is where most initialization 
     * should go: calling setContent(Composable) to define the UI.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Set the UI content to the AntsActivityContent Composable
            AntsActivityContent()
        }
    }
}

/**
 * AntsActivityContent defines the layout for the activity using Jetpack Compose.
 */
@Composable
fun AntsActivityContent() {
    // Surface provides a background color and can handle elevation (shadows)
    Surface(
        modifier = Modifier.fillMaxSize(), // Make the surface fill the entire screen
        color = MaterialTheme.colorScheme.background // Use the theme's background color
    ) {
        // Column stacks its children vertically.
        Column(modifier = Modifier.fillMaxSize()) {

            /**
             * Top Half: Image Section
             * We use a Box with weight(1f) to take up exactly half of the vertical space.
             */
            Box(
                modifier = Modifier
                    .weight(1f) // Give this box equal weight compared to the bottom section
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Image displays a drawable resource
                Image(
                    painter = painterResource(id = R.drawable.ants), // Load 'ants' from res/drawable
                    contentDescription = "Ants Image", // Accessibility description
                    modifier = Modifier.fillMaxSize(), // Make image fill the Box container
                    contentScale = ContentScale.Crop // Crop the image to fit the container without distortion
                )
            }

            /**
             * Bottom Half: Educational Text Section
             * Another Box with weight(1f) to occupy the remaining half of the screen.
             */
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp), // Add inner spacing around the text
                contentAlignment = Alignment.TopStart
            ) {
                // Text Composable used to display a multi-line string with educational facts
                Text(
                    text = """
                        Ants are among the most successful insects on Earth. 
                        They live in highly organized colonies that can contain millions of individuals.
                        
                        Key facts:
                        • They have a caste system (queen, workers, soldiers)
                        • Many species practice "farming" — growing fungus or herding aphids
                        • They communicate using pheromones and can solve complex problems
                        • Some ants can carry up to 50 times their own body weight
                        
                        There are over 12,000 known species of ants, and they play a vital role in ecosystems as decomposers and predators.
                    """.trimIndent(),
                    fontSize = 16.sp, // Set font size in sp (scalable pixels)
                    lineHeight = 24.sp, // Set spacing between lines
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}
