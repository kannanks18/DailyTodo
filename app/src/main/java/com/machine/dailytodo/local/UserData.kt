package com.machine.dailytodo.local
import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
@Keep
@Parcelize
@Entity(tableName = "user_table")
data class UserData(
    @PrimaryKey(autoGenerate = true)
    val id: Int=0,
    val userName: String="",
    val userMobile: String=""
) : Parcelable
