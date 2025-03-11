package com.unipi.george.chordshub.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey val username: String,
    val fullname: String,
    val email: String


)

