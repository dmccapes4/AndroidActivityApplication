package com.example.androidactivityapplication.database

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.example.androidactivityapplication.database.entities.ClinicalNodeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicalNodeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNode(node: ClinicalNodeEntity)

    @Query("SELECT * FROM clinical_nodes")
    suspend fun getAllNodes(): List<ClinicalNodeEntity>

    @Query("SELECT * FROM clinical_nodes WHERE patientId = :patientId")
    fun getNodesForPatient(patientId: Long): Flow<List<ClinicalNodeEntity>>
}
