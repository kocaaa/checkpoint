package com.example.frontend.data.remote

import com.example.frontend.data.remote.dto.*
import com.example.frontend.domain.model.Comment
import com.example.frontend.domain.model.Location
import com.example.frontend.domain.model.RegisterUser
import okhttp3.MultipartBody
import retrofit2.http.*

interface CheckpointApi {
    @POST("api/register")
    suspend fun register(
        @Body appUser:RegisterUser
    ): String

    @FormUrlEncoded
    @POST("api/login")
    suspend fun login(
        @Field("username") username:String,
        @Field("password") password:String
    ) : LoginDTO

    @Headers("Content-Type: application/json")
    @GET("location/all")
    suspend fun getAll(
        @Header("Authorization") token : String
    ): List<LocationDTO>

    @Headers("Content-Type: application/json")
    @GET("location/keyword/{name}")
    suspend fun searchLocation(
        @Header("Authorization") token : String,
        @Path("name") name:String
    ): List<LocationDTO>

    @Headers("Content-Type: application/json")
    @GET("post/location/{locationId}")
    suspend fun getPostsFromLocation(
        @Header("Authorization") token : String,
        @Path("locationId") locationId:Long
    ): List<PostDTO>

    @Headers("Content-Type: application/json")
    @GET("post/user/{userId}")
    suspend fun getPostsByUserId(
        @Header("Authorization") token : String,
        @Path("userId") userId:Long
    ): List<PostDTO>

    @Headers("Content-Type: application/json")
    @GET("post/following")
    suspend fun getPostsOfUsersThatIFollow(
        @Header("Authorization") token : String,
    ): List<PostDTO>

    @Headers("Content-Type: application/json")
    @GET("follow_list/my/following")
    suspend fun getMyFollowing(
        @Header("Authorization") token : String
    ) : List<UserDetailedDTO>

    @Headers("Content-Type: application/json")
    @GET("follow_list/my/followers")
    suspend fun getMyFollowers(
        @Header("Authorization") token : String
    ) : List<UserDetailedDTO>

    @Headers("Content-Type: application/json")
    @GET("follow_list/my/following/count")
    suspend fun getMyFollowingCount(
        @Header("Authorization") token : String
    ) : Int

    @Headers("Content-Type: application/json")
    @GET("follow_list/my/followers/count")
    suspend fun  getMyFollowersCount(
        @Header("Authorization") token : String
    ) : Int

    @Headers("Content-Type: application/json")
    @GET("post/my/count")
    suspend fun getMyPostsCount(
        @Header("Authorization") token : String
    ) : Int

    @Headers("Content-Type: application/json")
    @DELETE("post/delete/{id}")
    suspend fun detelePostById(
        @Header("Authorization") token: String,
        @Path("id") postId: Long
    ): String

    ///////////////////////////

    @Headers("Content-Type: application/json")
    @GET("follow_list/{userId}/following")
    suspend fun getAllFollowingByUser(
        @Header("Authorization") token : String,
        @Path("userId") userId:Long
    ) : List<UserDetailedDTO>

    @Headers("Content-Type: application/json")
    @GET("follow_list/{userId}/followers")
    suspend fun getAllFollowersPerUser(
        @Header("Authorization") token : String,
        @Path("userId") userId:Long
    ) : List<UserDetailedDTO>

    @Headers("Content-Type: application/json")
    @POST("follow_list/follow_or_unfollow/{userId}")
    suspend fun followOrUnfollowUser(
        @Header("Authorization") token : String,
        @Path("userId") userId:Long
    ) : String

    @Headers("Content-Type: application/json")
    @GET("follow_list/{userId}/following/count")
    suspend fun countAllFollowingByUser(
        @Header("Authorization") token : String,
        @Path("userId") userId:Long
    ) : Int

    @Headers("Content-Type: application/json")
    @GET("follow_list/{userId}/followers/count")
    suspend fun countAllFollowersPerUser(
        @Header("Authorization") token : String,
        @Path("userId") userId:Long
    ) : Int

    @Headers("Content-Type: application/json")
    @GET("post/user/{userId}/count")
    suspend fun getUserPostsCount(
        @Header("Authorization") token : String,
        @Path("userId") userId:Long
    ) : Int

    @Headers("Content-Type: application/json")
    @GET("follow_list/{userId}/following/keyword/{username}")
    suspend fun getFollowingByUsername(
        @Header("Authorization") token : String,
        @Path("userId") userId:Long,
        @Path("username") username : String
    ) : List<UserDetailedDTO>

    @Headers("Content-Type: application/json")
    @GET("follow_list/{userId}/followers/keyword/{username}")
    suspend fun getFollowersByUsername(
        @Header("Authorization") token : String,
        @Path("userId") userId:Long,
        @Path("username") username : String
    ) : List<UserDetailedDTO>

    @Headers("Content-Type: application/json")
    @GET("follow_list/my/following/keyword/{username}")
    suspend fun getMyFollowingByUsername(
        @Header("Authorization") token : String,
        @Path("username") username : String
    ) : List<UserDetailedDTO>

    @Headers("Content-Type: application/json")
    @GET("follow_list/my/followers/keyword/{username}")
    suspend fun getMyFollowersByUsername(
        @Header("Authorization") token : String,
        @Path("username") username : String
    ) : List<UserDetailedDTO>

    @Headers("Content-Type: application/octet-stream")
    @GET("photos/photoByPostIdAndOrder/{postId}/{order}")
    suspend fun GetPhotoByPostIdAndOrder(
        @Header("Authorization") token : String,
        @Path("postId") postId: Long,
        @Path("order") order: Int
    ) : BinaryPhoto

    @Headers("Content-Type: application/json")
    @GET("api/getUserId")
    suspend fun getUserId(
        @Header("Authorization") token : String,
    ) : Long

    @Headers("Content-Type: application/json")
    @GET("post_likes/count/{postId}")
    suspend fun getNumberOfLikesByPostId(
        @Header("Authorization") token : String,
        @Path("postId") postId:Long
    ) : Int

    @Headers("Content-Type: application/json")
    @GET("post/{id}")
    suspend fun getPostById(
        @Header("Authorization") token : String,
        @Path("id") postId : Long
    ) : PostDTO


    @POST("post/save/location/{locationId}")
    suspend fun savePost(
        @Header("Authorization") token : String,
        @Body description: String,
        @Path("locationId") locationId: Long
    ) : Long

    @Multipart
    @POST("photos/add/{postId}/{order}")
    suspend fun addImage(
        @Header("Authorization") token : String,
        @Path("postId") postId:Long,
        @Path("order") order:Int,
        @Part photo: MultipartBody.Part
    ): String
    @GET("post/{id}/comments/count")
    suspend fun getNumberOfCommentsByPostId(
        @Header("Authorization") token : String,
        @Path("id") postId:Long
    ) : Int

    @Headers("Content-Type: application/json")
    @POST("post_likes/save/{postId}")
    suspend fun likeOrUnlikePostById(
        @Header("Authorization") token : String,
        @Path("postId") postId:Long
    ) : String

    @Headers("Content-Type: application/json")
    @PUT("api/user/info")
    suspend fun changeUserEmail(
        @Header("Authorization") token : String,
        @Body newEmail: String
    ) : String

    @Headers("Content-Type: application/json")
    @PUT("api/user/password")
    suspend fun changeUserPassword(
        @Header("Authorization") token : String,
        @Body passwords: Array<String>
    ) : String

    //@Headers("Content-Type: application/octet-stream")
    @Headers("Content-Type: application/json")
    @GET("api/getMyProfilePicture")
    suspend fun getMyProfilePicture(
        @Header("Authorization") token : String
    ) : String

    @Multipart
    @PUT("api/changeProfilePicture")
    suspend fun changeProfilePicture(
        @Header("Authorization") token : String,
        @Part profile_image: MultipartBody.Part
    ) : String

    @Headers("Content-Type: application/json")
    @GET("api/user")
    suspend fun getUserFromJWT(
        @Header("Authorization") token : String
    ): UserDTO

    @Headers("Content-Type: application/json")
    @GET("api/getProfilePictureByUserId/{userId}")
    suspend fun getUserProfilePicture(
        @Header("Authorization") token : String,
        @Path("userId") userId: Long
    ) : String

    @Headers("Content-Type: application/json")
    @POST("location/save")
    suspend fun saveLocation(
        @Header("Authorization") token : String,
        @Body location:LocationDTO
    ) : LocationDTO

    @Headers("Content-Type: application/json")
    @GET("api/token/refresh")
    suspend fun authorizeUser(
        @Header("Authorization") token : String
    ) : LoginDTO

    @Headers("Content-Type: application/json")
    @GET("comments/first/{postId}")
    suspend fun getFirstCommentsByPostId(
        @Header("Authorization") token: String,
        @Path("postId") postId: Long
    ): List<Comment>

    @Headers("Content-Type: application/json")
    @POST("comments/{postId}/{parentCommentId}/add")
    suspend fun addComment(
        @Header("Authorization") token : String,
        @Body commentText: String,
        @Path("postId") postId:Long,
        @Path("parentCommentId") parentCommentId:Long
    ) : String

    @Headers("Content-Type: application/json")
    @DELETE("comments/delete/{id}")
    suspend fun deleteCommentById(
        @Header("Authorization") token: String,
        @Path("id") commentId: Long
    ): String
}