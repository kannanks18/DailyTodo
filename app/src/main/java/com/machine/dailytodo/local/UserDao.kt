package com.machine.dailytodo.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userData: UserData)


    @Query("SELECT * FROM user_table")
    fun getData(): Flow<UserData>

}