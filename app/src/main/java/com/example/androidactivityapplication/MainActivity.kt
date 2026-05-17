package com.example.androidactivityapplication

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp

// Define a data class for each button
data class ButtonData(
    val colorName: String,
    val color: Color,
    val icon: String
)

@Composable
fun MyScreen() {
    val buttons = listOf(
        ButtonData("Red", Color.Red, "🟥"),
        ButtonData("Blue", Color.Blue, "🟦"),
        ButtonData("Green", Color.Green, "🟩"),
        ButtonData("Yellow", Color.Yellow, "🟨"),
        ButtonData("Purple", Color(0xFF800080), "🟪"),
        ButtonData("Orange", Color(0xFFFFA500), "🟧"),
        ButtonData("Pink", Color(0xFFFFC0CB), "🟥"),
        ButtonData("Gray", Color.Gray, "🟥"),
        ButtonData("Black", Color.Black, "🟥")
    )

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                // Split the buttons into 3 rows
                buttons.chunked(3).forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { button ->
                            Button(
                                onClick = {
                                    // Log the color and icon when clicked
                                    Log.d("ButtonClicked", "Color: ${button.colorName}, Icon: ${button.icon}")
                                },
                                modifier = Modifier
                                    .size(100.dp)
                                    .background(button.color)
                                    .padding(8.dp)
                                    .clickable { /* Optional for tapping behavior */ }
                            ) {
                                Text(
                                    text = button.icon,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = false)
@Composable
fun MyScreenPreview() {
    MyScreen()
}
