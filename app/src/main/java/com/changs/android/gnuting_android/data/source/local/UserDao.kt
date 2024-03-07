package com.changs.android.gnuting_android.data.source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.changs.android.gnuting_android.data.model.MyInfoResult
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM my_info")
    fun getMyInfo(): Flow<MyInfoResult>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMyInfo(user: MyInfoResult)

    @Delete
    fun deleteMyInfo(user: MyInfoResult)
}