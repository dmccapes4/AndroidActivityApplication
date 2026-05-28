package com.example.androidactivityapplication.database.entities

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import androidx.room3.Relation

@Entity(
    tableName = "clinical_nodes",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["patientId"],
            childColumns = ["patientId"]
        )
    ]
)
data class ClinicalNodeEntity(
    @PrimaryKey(autoGenerate = true)
    val nodeId: Long = 0,

    val patientId: Long,
    val nodeType: NodeTypes,
    val details: String = "None provided",
    val timestamp: Long = System.currentTimeMillis()
)

data class PatientWithClinicalNodes(
    @Embedded val patient: PatientEntity,
    @Relation(
        parentColumns = ["patientId"],
        entityColumns = ["patientId"]
    )
    val clinicalNodes: List<ClinicalNode>
)


enum class NodeTypes {
    DIAGNOSIS,
    MEDICATION,
    LAB_RESULTS,
    NOTES,
    UNKNOWN
}

sealed interface ClinicalNode {
    val id: String
    val timestamp: Long
    val type: NodeTypes
}

data class Diagnosis(
    override val id: String,
    override val timestamp: Long,
    val disease: String
) : ClinicalNode {
    override val type: NodeTypes = NodeTypes.DIAGNOSIS
}

data class Medication(
    override val id: String,
    override val timestamp: Long,
    val medicationType: String,
    val dosage: String
) : ClinicalNode {
    override val type: NodeTypes = NodeTypes.MEDICATION
}

data class LabResults(
    override val id: String,
    override val timestamp: Long,
    val labType: String,
    val results: String
) : ClinicalNode {
    override var type: NodeTypes = NodeTypes.LAB_RESULTS
}

data class Notes(
    override val id: String,
    override val timestamp: Long,
    val author: String,
    val content: String
) : ClinicalNode {
    override val type: NodeTypes = NodeTypes.NOTES
}

data class UnknownNode(
    override val id: String,
    override val timestamp: Long,
    val message: String? = "unknown"
) : ClinicalNode {
    override val type: NodeTypes = NodeTypes.UNKNOWN
}

fun ClinicalNodeEntity.toClinicalNode(): ClinicalNode {
    val idString = nodeId.toString()
    return when (nodeType) {
        NodeTypes.DIAGNOSIS -> Diagnosis(idString, timestamp, details)
        NodeTypes.MEDICATION -> {
            val parts = details.split(" ", limit = 2)
            Medication(idString, timestamp, parts.getOrElse(0) { "Unknown" }, parts.getOrElse(1) { "" })
        }
        NodeTypes.LAB_RESULTS -> {
            val parts = details.split(":", limit = 2)
            LabResults(idString, timestamp, parts.getOrElse(0) { "Unknown" }, parts.getOrElse(1) { "" })
        }
        NodeTypes.NOTES -> Notes(idString, timestamp, "System", details)
        NodeTypes.UNKNOWN -> UnknownNode(idString, timestamp, details)
    }
}



