package com.example.androidactivityapplication.database

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.example.androidactivityapplication.database.entities.ClinicalEdge
import kotlinx.coroutines.flow.Flow

@Dao
interface ClinicalEdgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEdge(edge: ClinicalEdge)

    @Query("SELECT * FROM clinical_edges")
    fun getAllEdges(): Flow<List<ClinicalEdge>>
}
