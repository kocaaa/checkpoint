package com.example.frontend.domain.repository

import com.example.frontend.data.remote.dto.*
import com.example.frontend.domain.model.Comment
import com.example.frontend.domain.model.Location
import com.example.frontend.domain.model.RegisterUser
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Part
import retrofit2.http.Path

interface CheckpointRepository {
    suspend fun register(appUser : RegisterUser) : String

    suspend fun login(username : String, password : String) : LoginDTO

    suspend fun searchLocation(token : String, name : String) : List<LocationDTO>

    suspend fun getAll(token: String) : List<LocationDTO>

    suspend fun getPostsFromLocation(token : String, locationId : Long) : List<PostDTO>

    suspend fun getPostsByUserId(token : String, userId : Long) : List<PostDTO>

    suspend fun getMyFollowers(token: String): List<UserDetailedDTO>

    suspend fun getMyFollowing(token: String): List<UserDetailedDTO>

    suspend fun getMyFollowersCount(token: String): Int

    suspend fun getMyFollowingCount(token: String): Int

    suspend fun getMyPostsCount(token: String): Int

    suspend fun getAllFollowingByUser(token: String, userId : Long): List<UserDetailedDTO>

    suspend fun getAllFollowersPerUser(token: String, userId : Long): List<UserDetailedDTO>

    suspend fun followOrUnfollowUser(token: String, userId: Long): String

    suspend fun countAllFollowingByUser(token: String, userId : Long): Int

    suspend fun countAllFollowersPerUser(token: String, userId : Long): Int

    suspend fun getUserPostsCount(token: String, userId : Long): Int

    suspend fun getFollowingByUsername(token: String, userId: Long, username: String): List<UserDetailedDTO>

    suspend fun getFollowersByUsername(token: String, userId: Long, username: String): List<UserDetailedDTO>

    suspend fun getMyFollowersByUsername(token: String, username: String): List<UserDetailedDTO>

    suspend fun getMyFollowingByUsername(token: String, username: String): List<UserDetailedDTO>

    suspend fun getPhotoByPostIdAndOrder(token: String, postid: Long, order: Int): BinaryPhoto

    suspend fun deletePostById(token : String, postId: Long) : String

    suspend fun getUserId(token:String) : Long

    suspend fun getUserFromJWT(token : String): UserDTO

    suspend fun getPostById(token:String, postId:Long) : PostDTO

    suspend fun savePost(token: String,description:String,locationId:Long): Long

    suspend fun addImage(token: String, postId:Long, order:Int, photo:MultipartBody.Part): String

    suspend fun getNumberOfLikesByPostId(token: String, postId: Long) : Int

    suspend fun getNumberOfCommentsByPostId(token: String, postId: Long) : Int

    suspend fun likeOrUnlikePostById(token: String, postId: Long) : String

    suspend fun changeUserEmail(token: String, newEmail: String) : String

    suspend fun changeUserPassword(token: String, passwords: Array<String>) : String

    suspend fun getMyProfilePicture(token : String) : String

    suspend fun getUserProfilePicture(token : String, userId: Long) : String

    suspend fun changeProfilePicture(token : String, profile_image: MultipartBody.Part) : String

    suspend fun saveLocation(token: String, location: LocationDTO) : LocationDTO

    suspend fun authorizeUser(token: String) : LoginDTO
    
    suspend fun getFirstCommentsByPostId(token: String, postId: Long): List<Comment>

    suspend fun addComment(token : String, commentText: String, postId:Long, parentCommentId:Long) : String

    suspend fun deleteCommentById(token: String, commentId: Long): String

    suspend fun getPostsOfUsersThatIFollow(token: String): List<PostDTO>
}
