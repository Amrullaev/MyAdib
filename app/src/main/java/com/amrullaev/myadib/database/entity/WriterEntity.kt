package com.amrullaev.myadib.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WriterEntity(
    @PrimaryKey
    val name:String
)
