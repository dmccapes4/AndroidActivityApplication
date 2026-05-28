package com.example.androidactivityapplication.database

import androidx.room3.RoomDatabase
import androidx.room3.TypeConverters
import androidx.room3.Database
import com.example.androidactivityapplication.database.entities.ClinicalEdge
import com.example.androidactivityapplication.database.entities.ClinicalNode
import com.example.androidactivityapplication.database.entities.PatientEntity
import com.example.androidactivityapplication.database.converters.DateConverter

@Database(
    entities = [
        PatientEntity::class,
        ClinicalNode::class,
        ClinicalEdge::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class ClinicalDatabase : RoomDatabase() {

    abstract fun patientDao(): PatientDao
    abstract fun clinicalNodeDao(): ClinicalNodeDao
    abstract fun clinicalEdgeDao(): ClinicalEdgeDao

    companion object {
        const val DATABASE_NAME = "clinical_database"
    }
}
