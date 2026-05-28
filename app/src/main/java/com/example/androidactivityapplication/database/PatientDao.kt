package com.example.androidactivityapplication.database

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.example.androidactivityapplication.database.entities.PatientEntity
import com.example.androidactivityapplication.database.entities.PatientWithClinicalNodes
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: PatientEntity)

    @Query("SELECT * FROM patients")
    fun getAllPatients(): Flow<List<PatientEntity>>

    @Transaction
    @Query("SELECT * FROM patients WHERE patientId = :patientId")
    fun getPatientWithNodes(patientId: Long): Flow<PatientWithClinicalNodes?>
}
