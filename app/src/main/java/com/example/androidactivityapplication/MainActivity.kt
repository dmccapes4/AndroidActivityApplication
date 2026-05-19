// MainActivity.kt
package com.example.androidactivityapplication

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

open class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(this)   // Pass the Activity context
        }
    }
}

@Composable
fun MainScreen(activity: MainActivity) {   // Receive context here
    val colors = listOf(
        Color.Red, Color.Blue, Color.Green,
        Color.Yellow, Color.DarkGray, Color.LightGray,
        Color.Gray, Color.Cyan, Color.Magenta
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LazyColumn {
            items(3) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 0..2) {
                        val index = row * 3 + col
                        ColoredButton(
                            color = colors[index],
                            onClick = {
                                if (index == 0) { // Red button
                                    val intent = Intent(activity, AntsActivity::class.java)
                                    activity.startActivity(intent)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ColoredButton(
    color: Color,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(color, shape = CircleShape)
            .padding(4.dp)
            .clickable { onClick() }
    ) {
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

@Preview(showSystemUi = true)
@Composable
fun MainScreenPreview() {
    MainScreen(activity = object : MainActivity() {}) // For preview only
}