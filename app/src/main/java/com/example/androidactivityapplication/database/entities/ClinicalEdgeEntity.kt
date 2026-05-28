package com.example.androidactivityapplication.database.entities

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey

@Entity(
    tableName = "clinical_edges",
    foreignKeys = [
        ForeignKey(
            entity = ClinicalNode::class,
            parentColumns = ["clinical_nodes"],
            childColumns = ["sourceNode"]
        )
    ]
)
data class ClinicalEdgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sourceNode: Long = 0,
    val destinationNode: Long = 0,
    val connascenceType: ConnascenceTypes
)

enum class ConnascenceTypes {
    STRUCTURAL,
    TEMPORAL,
    CONCEPTUAL,
    CO_OCCURENCE,
    CO_VARIANCE
}