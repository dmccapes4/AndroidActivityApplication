# Clinical Reasoning Sandbox
**Project Name**: Clinical Reasoning Sandbox
**Goal**: Build a three-Activity application with features such as displaying web content, managing timers, and reading news articles.

## Activities
1. **MainActivity**: The main entry point of the application, featuring a list of colored buttons that navigate to different activities.
2. **WindActivity**: Displays web content (Wikipedia page about Wind) using a WebView inside a Jetpack Compose UI.
3. **TabActivity**: Showcase a simple implementation of Bottom Navigation using Compose, toggling between three content views based on the selected tab.
4. **TimerActivity**: Demonstrates how to manage background tasks (timers) using Coroutines and update Compose state to reflect changes in the UI.
5. **NewsActivity**: Contains the main activity and UI components for a news reader app, including fetching news data from an API, displaying articles in a list, and showing full articles in a WebView.
6. **WeatherActivity**: Displays weather information using Jetpack Compose.
7. **AntsActivity**: A placeholder activity (not yet implemented).

## Next Immediate Steps
1. Create Room database schema + basic entities
2. Build PatientActivity with mock data
3. Implement search and filtering functionality in NewsActivity