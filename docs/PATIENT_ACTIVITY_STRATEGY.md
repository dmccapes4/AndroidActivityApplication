# Implementation Strategy for PatientActivity

## Overview
PatientActivity is the primary interface for patients, displaying their clinical graph and providing search functionality. This activity will utilize Room for data storage, Jetpack Compose for UI, ViewModel with StateFlow for state management, and a RecyclerView to display the list of clinical nodes.

## Key Requirements
1. **Data Model**: Load patient's clinical graph from Room.
2. **UI Components**:
   - Searchable list of clinical nodes (Diagnosis, Medication, Lab Results, Notes, etc.).
   - Nodes displayed as rich Material 3 Cards.
3. **State Management**: Use ViewModel + StateFlow to manage the state of the activity.
4. **Navigation**: Integrate with Jetpack Navigation for seamless navigation within the app.

## Implementation Steps
### Step 1: Set Up Room Database
- Define `Patient`, `ClinicalNode`, `ClinicalEdge`, and `Record` entities in Room.
- Create a DAO to handle database operations.

### Step 2: Create ViewModel with StateFlow
- Implement a ViewModel that fetches the patient's clinical graph from Room using the DAO.
- Use StateFlow to emit the state of the activity (e.g., loading, success, error).

### Step 3: Design UI Layouts
- Create Composable functions for displaying each type of clinical node as a Material 3 Card.
- Implement a RecyclerView with a custom adapter to display the list of nodes.

### Step 4: Integrate Search Functionality
- Add an EditText for search input and use it to filter the list of nodes displayed in the RecyclerView.

### Step 5: Handle Navigation
- Use Jetpack Navigation to navigate between PatientActivity, DoctorActivity, and GraphActivity.

## Code Snippets
### ViewModel Example
```kotlin
class PatientViewModel(private val patientDao: PatientDao) : ViewModel() {
    private val _uiState = MutableStateFlow<PatientUiState>(PatientUiState.Loading)
    val uiState: StateFlow<PatientUiState> get() = _uiState.asStateFlow()

    init {
        fetchClinicalGraph()
    }

    private fun fetchClinicalGraph() {
        viewModelScope.launch { 
            try {
                val graph = patientDao.getClinicalGraph()
                _uiState.value = PatientUiState.Success(graph)
            } catch (e: Exception) {
                _uiState.value = PatientUiState.Error(e)
            }
        }
    }
}

sealed class PatientUiState {
    data class Success(val graph: ClinicalGraph) : PatientUiState()
    object Loading : PatientUiState()
    data class Error(val error: Exception) : PatientUiState()
}
```

### RecyclerView Adapter Example
```kotlin
class NodeAdapter(private val nodes: List<ClinicalNode>, private val onNodeClick: (ClinicalNode) -> Unit) : RecyclerView.Adapter<NodeAdapter.NodeViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NodeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.node_item, parent, false)
        return NodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NodeViewHolder, position: Int) {
        holder.bind(nodes[position])
    }

    override fun getItemCount() = nodes.size

    inner class NodeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(node: ClinicalNode) {
            itemView.setOnClickListener { onNodeClick(node) }
            // Bind node data to views
        }
    }
}
```

## Conclusion
PatientActivity is a critical component of the clinical dashboard, requiring a robust implementation that leverages Room for data storage, Jetpack Compose for UI, and ViewModel with StateFlow for state management. By following this strategy, we can create a user-friendly and efficient interface for patients to view their clinical graph.