package com.example.androidactivityapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room3.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.androidactivityapplication.database.ClinicalDatabase
import com.example.androidactivityapplication.database.entities.ClinicalNode
import com.example.androidactivityapplication.database.entities.ClinicalNodeEntity
import com.example.androidactivityapplication.database.entities.NodeTypes
import com.example.androidactivityapplication.database.entities.toClinicalNode
import javax.inject.Inject

@HiltViewModel
class PatientViewModel @Inject constructor(
    private val database: ClinicalDatabase
) : ViewModel() {
    private val _clinicalNodes = MutableStateFlow<List<ClinicalNode>>(emptyList())
    val clinicalNodes: StateFlow<List<ClinicalNode>> get() = _clinicalNodes

    init {
        viewModelScope.launch {
            loadClinicalGraph()
        }
    }

    private suspend fun loadClinicalGraph() {
        database.withTransaction {
            // Fetch the patient's clinical graph from Room
            // Assuming getAllNodes returns List<ClinicalNodeEntity>
            val nodeEntities = database.clinicalNodeDao().getAllNodes()
            _clinicalNodes.value = nodeEntities.map { it.toClinicalNode() }
        }
    }

    fun insertDummyData(patientId: Long = 1L) {
        viewModelScope.launch {
            database.withTransaction {
                val dao = database.clinicalNodeDao()

                val dummyNodes = listOf(
                    ClinicalNodeEntity(
                        patientId = patientId,
                        nodeType = NodeTypes.DIAGNOSIS,
                        details = "Type 2 Diabetes",
                        timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
                    ),
                    ClinicalNodeEntity(
                        patientId = patientId,
                        nodeType = NodeTypes.MEDICATION,
                        details = "Metformin 500mg",
                        timestamp = System.currentTimeMillis() - 43200000
                    ),
                    ClinicalNodeEntity(
                        patientId = patientId,
                        nodeType = NodeTypes.LAB_RESULTS,
                        details = "HbA1c: 7.2%",
                        timestamp = System.currentTimeMillis() - 3600000
                    ),
                    ClinicalNodeEntity(
                        patientId = patientId,
                        nodeType = NodeTypes.NOTES,
                        details = "Patient reports increased thirst",
                        timestamp = System.currentTimeMillis()
                    ),
                    ClinicalNodeEntity(
                        patientId = patientId,
                        nodeType = NodeTypes.UNKNOWN,
                        details = "Miscellaneous note",
                        timestamp = System.currentTimeMillis() - 7200000
                    )
                )

                for (node in dummyNodes) {
                    dao.insertNode(node)
                }
            }

            // Refresh the UI
            loadClinicalGraph()
        }
    }
}
