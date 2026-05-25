package com.example.androidactivityapplication

// --- EXPLANATION FOR THE JUNIOR ---
// We are importing a lot of Compose, Coroutine, and Serialization tools.
// Standard practice: Keep imports clean, but don't be afraid of them. 
// We use Material3 because it's the current Android standard.
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale

// --- DATA MODELS (The "Blueprints") ---

/**
 * We use @Serializable because Kotlinx.Serialization needs to know which 
 * classes it can turn into JSON and vice-versa. 
 * Note: These match the OpenWeather API structure exactly.
 */
@Serializable
data class GeocodingResult(
    val name: String,
    val lat: Double,
    val lon: Double,
    val country: String
)

@Serializable
data class CurrentWeatherResponse(
    val main: MainData,
    val weather: List<WeatherDescription>,
    val name: String
)

@Serializable
data class ForecastResponse(
    val list: List<ForecastItem>
)

@Serializable
data class ForecastItem(
    val main: MainData,
    val weather: List<WeatherDescription>,
    val dt_txt: String // This is the date/time string from the API
)

@Serializable
data class MainData(
    val temp: Double
)

@Serializable
data class WeatherDescription(
    val description: String,
    val icon: String
)

// --- UI STATE MODEL ---

/**
 * Senior Tip: Always use a single data class to represent your UI state.
 * It makes "State Hoisting" much easier and prevents UI inconsistencies.
 * If something changes in the UI, it should be reflected here.
 */
data class ForecastDay(
    val date: String,
    val temp: Int,
    val condition: String
)

data class WeatherInfo(
    val city: String = "",
    val temperature: Int = 0,
    val condition: String = "",
    val forecast: List<ForecastDay> = emptyList(),
    val isCelsius: Boolean = true, // Track unit preference
    val isLoading: Boolean = false, // Track network activity
    val error: String? = null // Track if anything went wrong
)

// --- GLOBALS ---
// We create the Json instance once and reuse it for performance.
// 'ignoreUnknownKeys = true' is vital because APIs often return extra data we don't need.
private val weatherJson = Json { ignoreUnknownKeys = true }
private const val TAG = "WeatherApp"

/**
 * WeatherActivity: Our entry point. 
 * In a real app, you'd likely use a ViewModel, but for a standalone 
 * activity demonstration, keeping state in the Composable is fine.
 */
class WeatherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SetContent is where the magic happens. It connects our Activity to the Compose UI.
        setContent { WeatherScreen() }
    }
}

@Composable
fun WeatherScreen() {
    // --- STATE MANAGEMENT ---
    // 'remember' keeps the value across recompositions (UI updates).
    // 'mutableStateOf' tells Compose to watch this variable for changes.
    var weatherState by remember { mutableStateOf(WeatherInfo()) }
    var cityInput by remember { mutableStateOf("") }
    
    // CoroutineScope bound to the lifecycle of this Composable.
    // We need this to run our network calls off the Main Thread.
    val scope = rememberCoroutineScope()

    // Surface provides the background color and root container properties.
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Weather App", 
                fontSize = 28.sp, 
                fontWeight = FontWeight.Bold, 
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Search input field
            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                label = { Text("Enter city name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Row: Search Button + Unit Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {
                        if (cityInput.isNotBlank()) {
                            // Launching a coroutine! 
                            // We don't want to freeze the UI while waiting for the internet.
                            scope.launch {
                                // 1. Update state to show loading spinner
                                weatherState = weatherState.copy(city = cityInput, isLoading = true, error = null)
                                // 2. Perform the actual work
                                weatherState = fetchWeather(cityInput, weatherState.isCelsius)
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Get Weather")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Bonus: Celsius/Fahrenheit Toggle using FilterChip (Material 3 component)
                FilterChip(
                    selected = !weatherState.isCelsius,
                    onClick = { 
                        // Update state and immediately re-fetch if we already have a city
                        weatherState = weatherState.copy(isCelsius = !weatherState.isCelsius)
                        if (weatherState.city.isNotBlank() && !weatherState.isLoading) {
                            scope.launch {
                                weatherState = fetchWeather(cityInput, weatherState.isCelsius)
                            }
                        }
                    },
                    label = { Text(if (weatherState.isCelsius) "Metric (°C)" else "Imperial (°F)") }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- CONDITIONAL UI RENDERING ---
            // Senior Tip: This is how you handle different app states in Compose.
            
            if (weatherState.isLoading) {
                // Show a spinner while the network is busy
                CircularProgressIndicator()
            } else if (weatherState.error != null) {
                // Show the error message if something broke
                Text(text = weatherState.error ?: "Error", color = MaterialTheme.colorScheme.error)
            } else if (weatherState.city.isNotBlank()) {
                // --- Main Weather Card ---
                Card(
                    modifier = Modifier.fillMaxWidth(), 
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp), 
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = weatherState.city, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        val unit = if (weatherState.isCelsius) "°C" else "°F"
                        Text(text = "${weatherState.temperature}$unit", fontSize = 48.sp, fontWeight = FontWeight.Light)
                        Text(text = weatherState.condition, fontSize = 20.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                
                // --- Forecast Header ---
                Text(
                    text = "5-Day Forecast (Midday)", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                // --- Forecast List (LazyRow) ---
                // Junior: LazyRow is like a horizontal RecyclerView. 
                // It only draws what's on screen, which is super efficient.
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(weatherState.forecast) { day ->
                        ForecastCard(day, weatherState.isCelsius)
                    }
                }
            }
        }
    }
}

/**
 * A sub-composable for the forecast items. 
 * Breaking UI into small components is "Clean Code" 101.
 */
@Composable
fun ForecastCard(day: ForecastDay, isCelsius: Boolean) {
    Card(modifier = Modifier.width(100.dp)) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = day.date, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            val unit = if (isCelsius) "°C" else "°F"
            Text(text = "${day.temp}$unit", fontSize = 18.sp)
            Text(text = day.condition, fontSize = 12.sp, maxLines = 1)
        }
    }
}

// --- NETWORK LOGIC (The "Heavy Lifting") ---

/**
 * fetchWeather: This is our business logic function.
 * It's 'suspend' because it performs long-running network operations.
 */
private suspend fun fetchWeather(city: String, isCelsius: Boolean): WeatherInfo {
    val apiKey = "0bd22e6b79c4e675e4386ad572401681"
    val units = if (isCelsius) "metric" else "imperial"

    return try {
        // withContext(Dispatchers.IO) ensures this block runs on a background thread.
        // If you do this on the Main thread, Android will throw a NetworkOnMainThreadException.
        withContext(Dispatchers.IO) {
            
            // STEP 1: Geocoding (Convert city name to coordinates)
            // We MUST encode the city name (e.g. "New York" becomes "New+York") or the URL will be invalid.
            val encodedCity = URLEncoder.encode(city, "UTF-8")
            val geoUrl = "https://api.openweathermap.org/geo/1.0/direct?q=$encodedCity&limit=1&appid=$apiKey"
            val geoResponseText = readUrl(geoUrl)
            val geoResults = weatherJson.decodeFromString<List<GeocodingResult>>(geoResponseText)

            if (geoResults.isEmpty()) {
                return@withContext WeatherInfo(city = city, error = "City '$city' not found.", isCelsius = isCelsius)
            }

            val location = geoResults[0]
            val lat = location.lat
            val lon = location.lon

            // STEP 2: Current Weather
            val currentUrl = "https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$lon&units=$units&appid=$apiKey"
            val currentResponseText = readUrl(currentUrl)
            val currentData = weatherJson.decodeFromString<CurrentWeatherResponse>(currentResponseText)

            // STEP 3: 5-Day Forecast
            val forecastUrl = "https://api.openweathermap.org/data/2.5/forecast?lat=$lat&lon=$lon&units=$units&appid=$apiKey"
            val forecastResponseText = readUrl(forecastUrl)
            val forecastData = weatherJson.decodeFromString<ForecastResponse>(forecastResponseText)

            // --- DATA PROCESSING ---
            // The API returns weather for every 3 hours. 
            // We only want ONE entry per day, so we filter for entries at "12:00:00" (Noon).
            val processedForecast = forecastData.list
                .filter { it.dt_txt.contains("12:00:00") }
                .take(5) // Just in case, only take 5 days
                .map { item ->
                    ForecastDay(
                        date = item.dt_txt.substring(5, 10), // Truncate "2023-10-25..." to "10-25"
                        temp = item.main.temp.toInt(),
                        condition = item.weather.firstOrNull()?.description?.capitalizeFirst() ?: "Unknown"
                    )
                }

            // Return the final successful state
             WeatherInfo(
                city = "${location.name}, ${location.country}",
                temperature = currentData.main.temp.toInt(),
                condition = currentData.weather.firstOrNull()?.description?.capitalizeFirst() ?: "Unknown",
                forecast = processedForecast,
                isCelsius = isCelsius
            )
        }
    } catch (e: Exception) {
        // Senior Move: Always log your exceptions with a Tag so you can find them in Logcat!
        Log.e(TAG, "Error in fetchWeather", e)
        WeatherInfo(city = city, error = "Error: ${e.localizedMessage}", isCelsius = isCelsius)
    }
}

/**
 * readUrl: A helper function that handles the HTTP connection boilerplate.
 * Junior: In a massive project, you'd use a library like Retrofit or Ktor.
 * But knowing how to do it with standard Java/Kotlin is GREAT for interviews.
 */
private fun readUrl(urlPath: String): String {
    val url = URL(urlPath)
    val connection = url.openConnection() as HttpURLConnection
    return try {
        // Always check the response code. 200 means OK!
        val responseCode = connection.responseCode
        if (responseCode in 200..299) {
            // Read the stream into a string
            connection.inputStream.bufferedReader().readText()
        } else {
            // If it's not a success, read the Error Stream to see what the server said.
            val errorBody = connection.errorStream?.bufferedReader()?.readText()
            throw Exception("HTTP $responseCode: $errorBody")
        }
    } finally {
        // NEVER forget to disconnect, or you'll leak memory and connections!
        connection.disconnect()
    }
}

/**
 * Extension function to capitalize the first letter. 
 * Because 'cloudy' looks worse than 'Cloudy' in a UI.
 */
private fun String.capitalizeFirst(): String = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}
