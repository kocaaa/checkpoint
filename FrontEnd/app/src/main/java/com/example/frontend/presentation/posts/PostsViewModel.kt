package com.example.frontend.presentation.posts

import Constants
import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.common.Resource
import Constants.Companion.LOCATION_ID
import android.util.Log
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.model.Post
import com.example.frontend.domain.use_case.delete_post.DeletePostUseCase
import com.example.frontend.domain.use_case.location_posts.GetAllPostsForLocationUseCase
import com.example.frontend.domain.use_case.location_posts.GetPhotoByPostIdAndOrderUseCase
import com.example.frontend.domain.use_case.post_likes.LikeOrUnlikePostUseCase

import com.example.frontend.presentation.posts.components.PostCardState
import com.example.frontend.presentation.posts.components.PostStringState
import com.example.frontend.presentation.posts.components.PostsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getAllPostsForLocationUseCase : GetAllPostsForLocationUseCase,
    private val getPhotoByPostIdAndOrderUseCase: GetPhotoByPostIdAndOrderUseCase,
    private val deletePostUseCase: DeletePostUseCase,
    private val likeOrUnlikePostUseCase: LikeOrUnlikePostUseCase,
    private val savedStateHandle: SavedStateHandle,
    application: Application
) : ViewModel(){

    var loginUserId = 0L;

    private val _state = mutableStateOf(PostsState())
    val state : State<PostsState> = _state
    val context = application.baseContext
    var locId = 0L;

    var allPosts = mutableStateOf<List<Post>?>(null);

    var access_token = "";
    var refresh_token = "";

    private val _stateDelete = mutableStateOf(PostStringState())
    val stateDelete : State<PostStringState> = _stateDelete;

    private val _stateLikeOrUnlike = mutableStateOf(PostStringState())
    val stateLikeOrUnlike : State<PostStringState> = _stateLikeOrUnlike;

    init {
        Constants.sort = 1;
        savedStateHandle.get<Long>(LOCATION_ID)?.let { locationId ->
            Log.d("Location id", locationId.toString())
            locId = locationId;
            getAllPostsForLocation(locationId)
        }
    }

    fun getAllPostsForLocation(locationId : Long)
    {
        GlobalScope.launch(Dispatchers.Main){

            loginUserId = DataStoreManager.getLongValue(context, "userId");

            access_token =  DataStoreManager.getStringValue(context, "access_token").trim();
            refresh_token = DataStoreManager.getStringValue(context, "refresh_token").trim();

            getAllPostsForLocationUseCase("Bearer "+access_token, locationId).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _state.value = PostsState(posts = result.data)
                        allPosts.value = getPosts()
                        println(result.data)
                    }
                    is Resource.Error -> {
                        _state.value = PostsState(error = result.message ?:
                        "An unexpected error occured")
                        if(result.message?.contains("403") == true){
                            GlobalScope.launch(Dispatchers.Main){
                                DataStoreManager.deleteAllPreferences(context);
                            }
                        }
                    }
                    is Resource.Loading -> {
                        _state.value = PostsState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getPhoto(post: Post): PostCardState {
        val _CardState = mutableStateOf(PostCardState())
        val state : State<PostCardState> = _CardState

        println("test------")
        if(!post.photos.isEmpty())
            getPhotoByPostIdAndOrderUseCase("Bearer "+access_token, post.photos[0].postId,post.photos[0].order).map { result ->
                when(result){
                    is Resource.Success -> {
                        println("--secces")
                        _CardState.value = PostCardState(picture = result.data!!.data.toByteArray())
                    }
                    is Resource.Error -> {
                        if(result.message?.contains("403") == true){
                            GlobalScope.launch(Dispatchers.Main){
                                DataStoreManager.deleteAllPreferences(context);
                            }
                        }

                        println("--error")
                        _CardState.value = PostCardState(error = result.message ?:
                        "An unexpected error occured")
                        println(result.message.toString())
                    }
                    is Resource.Loading -> {
                        println("--loading")
                        _CardState.value = PostCardState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)

        return _CardState.value;
    }


    fun deletePostById(postId: Long, locationId: Long)
    {
        deletePostUseCase("Bearer "+access_token, postId).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _stateDelete.value = PostStringState(message = result.data ?: "")
                    if (result.data!! == "Location deleted") {
                        Constants.locationDeletedUpdateMap = true;
                    }
                    getAllPostsForLocation(locationId)
                }
                is Resource.Error -> {
                    _stateDelete.value = PostStringState(error = result.message ?:
                    "An unexpected error occured")
                    if(result.message?.contains("403") == true){
                        GlobalScope.launch(Dispatchers.Main){
                            DataStoreManager.deleteAllPreferences(context);
                        }
                    }
                }
                is Resource.Loading -> {
                    _stateDelete.value = PostStringState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun likeOrUnlikePostById(postId: Long)
    {
        likeOrUnlikePostUseCase("Bearer "+access_token, postId).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _stateLikeOrUnlike.value = PostStringState(message = result.data ?: "")
                    allPosts.value = getPosts()
                }
                is Resource.Error -> {
                    _stateLikeOrUnlike.value = PostStringState(error = result.message ?:
                    "An unexpected error occured")
                    if(result.message?.contains("403") == true){
                        GlobalScope.launch(Dispatchers.Main){
                            DataStoreManager.deleteAllPreferences(context);
                        }
                    }

                }
                is Resource.Loading -> {
                    _stateLikeOrUnlike.value = PostStringState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun proveriConstants(){
        /*if(Constants.refreshComments != 0L || Constants.postLikeChangedSinglePostPage){
            Log.d("REFRESH COMM", "Refresh");
            Constants.refreshComments = 0L
            Constants.postLikeChangedSinglePostPage = false
            Log.d("Location id", locId.toString())
            getAllPostsForLocation(locId)
        }*/
        if (Constants.singlePostPageChanged) {
            Constants.singlePostPageChanged = false
            getAllPostsForLocation(locId)
        }
    }

    fun getPosts():List<Post>{
        if (Constants.sort == 0){
            return _state.value.posts!!
        }
        else if (Constants.sort == 1){
            return _state.value.posts!!.reversed()
        }
        else if (Constants.sort == 2){
            return  _state.value.posts!!.sortedBy { it.numberOfLikes }
        }
        else if (Constants.sort == 3){
            return _state.value.posts!!.sortedByDescending { it.numberOfLikes }
        }
        else if (Constants.sort == 4){
            return _state.value.posts!!.sortedBy { it.numberOfComments }
        }
        else if (Constants.sort == 5){
            return _state.value.posts!!.sortedByDescending { it.numberOfComments }
        }
         return emptyList()
    }
    fun nazivSorta():String{
        if (Constants.sort == 0){
            return "Date asc"
        }
        else if (Constants.sort == 1){
            return "Date dsc"
        }
        else if (Constants.sort == 2){
            return  "Likes asc"
        }
        else if (Constants.sort == 3){
            return "Likes dsc"
        }
        else if (Constants.sort == 4){
            return "Comments asc"
        }
        else if (Constants.sort == 5){
            return "Comments dsc"
        }
        return ""
    }
}