package com.example.androidactivityapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.androidactivityapplication.database.entities.ClinicalNode
import com.example.androidactivityapplication.database.entities.Diagnosis
import com.example.androidactivityapplication.database.entities.LabResults
import com.example.androidactivityapplication.database.entities.Medication
import com.example.androidactivityapplication.database.entities.NodeTypes
import com.example.androidactivityapplication.database.entities.Notes
import com.example.androidactivityapplication.database.entities.UnknownNode
import com.example.androidactivityapplication.ui.theme.AndroidActivityApplicationTheme
import com.example.androidactivityapplication.viewmodels.PatientViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidActivityApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PatientDashboard()
                }
            }
        }
    }
}

@Composable
fun PatientDashboard(
    viewModel: PatientViewModel = hiltViewModel()
) {
    val clinicalNodes = viewModel.clinicalNodes.collectAsState().value

    Column(modifier = Modifier.fillMaxSize()) {
        for (node in clinicalNodes) {
            ClinicalNodeItem(node)
        }
    }
}

@Composable
fun ClinicalNodeItem(node: ClinicalNode) {
    Card {
        Text("Node ID: ${node.id}")
        when(node) {
            is Diagnosis -> {
                Text("Diagnosis: ${node.disease}")
            }
            is Medication -> {
                Text("Medication: ${node.medicationType}")
                Text("Dosage: ${node.dosage}")
            }
            is LabResults -> {
                Text("Lab: ${node.labType}")
                Text("Result: ${node.results}")
            }
            is Notes -> {
                Text("Author: ${node.author}")
                Text("Content: ${node.content}")
            }
            is UnknownNode -> {
                Text("message: ${node.message}")
            }
        }
        Text("timestamp: ${node.timestamp}")
    }
}
