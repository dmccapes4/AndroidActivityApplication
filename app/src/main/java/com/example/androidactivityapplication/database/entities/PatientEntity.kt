package com.example.androidactivityapplication.database.entities

import androidx.room3.Embedded
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(
    tableName = "patients"
)
data class PatientEntity(
    @PrimaryKey(autoGenerate = true)

    val patientId: Long = 0,

    val name: String,
    val mrn: String,

    @Embedded
    val demographics: PatientDemographics,

    @Embedded
    val contactInfo: PatientContactInfo? = null
)

data class PatientDemographics(
    val dateOfBirth: String,
    val gender: String,
    val bloodType: String?
)

data class PatientContactInfo(
    val phone: String?,
    val emergencyContact: String?
)