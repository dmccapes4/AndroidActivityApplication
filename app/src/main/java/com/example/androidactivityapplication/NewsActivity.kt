package com.example.androidactivityapplication

// --- SENIOR EXPLANATION ---
// We're building a real-world news reader. 
// This involves:
// 1. Networking (fetching JSON from a REST API).
// 2. Image Loading (using the Coil library for efficiency).
// 3. Complex UI (List + Search + WebView).
// 4. Proper State Management (Handling loading, results, and user interaction).
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

// --- DATA MODELS ---
// Senior Tip: Keep your data models clean and use @Serializable. 
// Match the JSON structure of NewsAPI exactly.

@Serializable
data class ArticleSource(
    val id: String? = null,
    val name: String = ""
)

@Serializable
data class ArticleInfo(
    val source: ArticleSource? = null,
    val author: String? = null,
    val title: String = "",
    val description: String? = null,
    val url: String = "",
    val urlToImage: String? = null,
    val publishedAt: String = "",
    val content: String? = null
)

@Serializable
data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<ArticleInfo>
)

// --- UI STATE ---
// We encapsulate everything about the screen in one data class.
// This is "State Hoisting" 101.
data class NewsState(
    val articles: List<ArticleInfo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedArticleUrl: String? = null // When not null, we show WebView
)

// Create the Json engine once.
private val newsJson = Json { ignoreUnknownKeys = true }
private const val TAG = "NewsActivity"

class NewsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewsScreen()
        }
    }
}

@Composable
fun NewsScreen() {
    // --- STATE MANAGEMENT ---
    var newsState by remember { mutableStateOf(NewsState()) }
    var searchQuery by remember { mutableStateOf("Android") }
    
    // We need a scope to launch our 'suspend' networking functions.
    val scope = rememberCoroutineScope()

    // Trigger initial load. LaunchedEffect runs once when the screen starts.
    LaunchedEffect(Unit) {
        newsState = newsState.copy(isLoading = true)
        newsState = fetchNewsData(searchQuery)
    }

    // --- NAVIGATION LOGIC ---
    // If the user selected an article, we "intercept" the back button 
    // to close the WebView instead of closing the whole Activity.
    if (newsState.selectedArticleUrl != null) {
        BackHandler {
            newsState = newsState.copy(selectedArticleUrl = null)
        }
        
        // Show WebView (Reusing WindActivity logic)
        NewsWebView(url = newsState.selectedArticleUrl!!)
    } else {
        // Main News Feed UI
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                
                Text(
                    text = "Daily News", 
                    fontSize = 32.sp, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // --- SEARCH BAR ---
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search keywords...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        Button(
                            onClick = {
                                scope.launch {
                                    newsState = newsState.copy(isLoading = true, error = null)
                                    newsState = fetchNewsData(searchQuery)
                                }
                            },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text("Go")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- CONTENT AREA ---
                if (newsState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (newsState.error != null) {
                    Text(text = newsState.error!!, color = MaterialTheme.colorScheme.error)
                } else {
                    // LazyColumn is the "RecyclerView" of Compose. 
                    // It only renders items currently on screen. Super efficient!
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(newsState.articles) { article ->
                            ArticleCard(
                                article = article,
                                onClick = { 
                                    // Senior Tip: We update the state, which triggers a UI recomposition.
                                    newsState = newsState.copy(selectedArticleUrl = article.url) 
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * ArticleCard: A beautiful, modern Material 3 card for our news items.
 */
@Composable
fun ArticleCard(article: ArticleInfo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // --- IMAGE LOADING (COIL) ---
            // AsyncImage handles background loading, caching, and error placeholders.
            AsyncImage(
                model = article.urlToImage,
                contentDescription = "Article Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = article.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = article.source?.name ?: "Unknown Source",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${article.publishedAt.take(10)}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Text(
                    text = article.description ?: "No description available.",
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * NewsWebView: Shows the full article using Android's legacy WebView.
 * Demonstrates View Interoperability.
 */
@Composable
fun NewsWebView(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

// --- NETWORK LOGIC ---

/**
 * fetchNewsData: Performs the API call and parses the result.
 */
private suspend fun fetchNewsData(query: String): NewsState {
    // Junior: In a real project, NEVER hardcode your API key. Use Gradle properties or a Secrets file.
    val apiKey = "0f69ee92555545f38447c21d99bd44be"
    
    return withContext(Dispatchers.IO) {
        try {
            val encodedQ = URLEncoder.encode(query, "UTF-8")
            // NewsAPI 'everything' endpoint
            val newsURL = "https://newsapi.org/v2/everything?q=$encodedQ&sortBy=popularity&apiKey=$apiKey"
            
            Log.d(TAG, "Requesting: $newsURL")
            val jsonResponse = readUrlFromNet(newsURL)
            val data = newsJson.decodeFromString<NewsResponse>(jsonResponse)
            
            Log.d(TAG, "Fetched ${data.articles.size} articles")
            NewsState(articles = data.articles)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching news", e)
            NewsState(error = "Failed to load news: ${e.localizedMessage}")
        }
    }
}

/**
 * readUrlFromNet: Boilerplate for HTTP networking. 
 * Senior Tip: In a production app, use Retrofit or Ktor. 
 * But knowing the basics shows you understand the underlying protocol!
 */
private fun readUrlFromNet(urlString: String): String {
    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    // Identify our app to the server. Some APIs require this.
    connection.setRequestProperty("User-Agent", "Mozilla/5.0") 
    
    return try {
        val code = connection.responseCode
        if (code in 200..299) {
            connection.inputStream.bufferedReader().readText()
        } else {
            val error = connection.errorStream?.bufferedReader()?.readText()
            throw Exception("HTTP $code: $error")
        }
    } finally {
        connection.disconnect()
    }
}
