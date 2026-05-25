package com.example.androidactivityapplication

// Required imports for Activity, WebView, and Compose Interoperability
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * WindActivity is designed to display web content (Wikipedia page about Wind).
 * It demonstrates how to use traditional Android Views (like WebView) 
 * inside a Jetpack Compose UI.
 */
class WindActivity : ComponentActivity() {
    /**
     * Standard entry point for activity setup.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // AndroidView is a special Composable that allows hosting 
            // a traditional View (View-based UI) in a Compose layout.
            AndroidView(
                // The factory lambda is called exactly once when the View is first created.
                factory = { context ->
                    // Initialize and return a new WebView instance.
                    WebView(context).apply {
                        // WebViewClient prevents the OS from opening a browser app
                        // when a link is clicked; it keeps navigation inside the app.
                        webViewClient = WebViewClient()
                        
                        // Enable JavaScript execution for the loaded page.
                        // Warning: Be careful when enabling this with untrusted content.
                        settings.javaScriptEnabled = true
                        
                        // Load the initial Wikipedia URL.
                        loadUrl("https://en.wikipedia.org/wiki/Wind")
                    }
                },
                // Apply a modifier to the AndroidView to ensure it takes the full screen.
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
