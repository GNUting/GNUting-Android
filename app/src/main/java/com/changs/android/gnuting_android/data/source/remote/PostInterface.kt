package com.changs.android.gnuting_android.data.source.remote

import com.changs.android.gnuting_android.data.model.InUser
import com.changs.android.gnuting_android.data.model.PostDetailResponse
import com.changs.android.gnuting_android.data.model.PostResponse
import com.changs.android.gnuting_android.data.model.PostSearchResponse
import com.changs.android.gnuting_android.data.model.ReportRequest
import com.changs.android.gnuting_android.data.model.SaveRequest
import com.changs.android.gnuting_android.data.model.DefaultResponse
import com.changs.android.gnuting_android.data.model.RefreshTokenRequest
import com.changs.android.gnuting_android.data.model.ReIssueAccessTokenResponse
import com.changs.android.gnuting_android.data.model.UserSearchResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PostInterface {

    @GET("api/v1/board")
    suspend fun getPostList(@Query("page") page: Int = 1): Response<PostResponse>

    @GET("api/v1/board/myboard")
    suspend fun getMyPostList(@Query("page") page: Int = 1): Response<PostResponse>

    @GET("api/v1/board/{id}")
    suspend fun getPostDetail(@Path("id") id: Int): Response<PostDetailResponse>

    @GET("/api/v1/board/user/search")
    suspend fun getUserSearch(@Query("nickname") nickname: String): Response<UserSearchResponse>

    @POST("/api/v1/board/save")
    suspend fun postSave(@Body saveRequest: SaveRequest): Response<DefaultResponse>

    @PATCH("api/v1/board/{id}")
    suspend fun patchPostDetail(@Path("id") id: Int, @Body saveRequest: SaveRequest): Response<DefaultResponse>

    @DELETE("api/v1/board/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<DefaultResponse>

    @POST("api/v1/board/apply/{id}")
    suspend fun postApply(@Path("id") id: Int, @Body inUser: List<InUser>): Response<DefaultResponse>

    @GET("api/v1/board/search")
    suspend fun getSearchPost(@Query("keyword") keyword: String, @Query("page") page: Int = 1): Response<PostSearchResponse>

    @POST("/api/v1/boardReport")
    suspend fun postBoardReport(@Body reportRequest: ReportRequest): Response<DefaultResponse>

    @POST("/api/v1/reIssueAccessToken")
    suspend fun postReIssueAccessToken(@Body request: RefreshTokenRequest): Response<ReIssueAccessTokenResponse>

}