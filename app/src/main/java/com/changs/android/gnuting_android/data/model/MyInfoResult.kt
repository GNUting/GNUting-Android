package com.changs.android.gnuting_android.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "my_info")
data class MyInfoResult(
    @SerializedName("age")
    @ColumnInfo(name = "age")
    val age: String,
    @ColumnInfo(name = "department")
    @SerializedName("department")
    val department: String,
    @ColumnInfo(name = "gender")
    @SerializedName("gender")
    val gender: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("nickname")
    @ColumnInfo(name = "nickname")
    val nickname: String,
    @SerializedName("profileImage")
    @ColumnInfo(name = "profile_image")
    val profileImage: String? = null,
    @SerializedName("studentId")
    @ColumnInfo(name = "student_id")
    val studentId: String,
    @SerializedName("userRole")
    @ColumnInfo(name = "user_role")
    val userRole: String,
    @SerializedName("userSelfIntroduction")
    @ColumnInfo(name = "user_self_introduction")
    val userSelfIntroduction: String,
) : Parcelable {
    @PrimaryKey(autoGenerate = false)
    var idx: Long = 0L
}