package com.example.frontend.presentation.MainFeed

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.common.Resource
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.model.Post
import com.example.frontend.domain.use_case.get_posts.GetPostsIFollowUseCase
import com.example.frontend.domain.use_case.post_likes.LikeOrUnlikePostUseCase
import com.example.frontend.presentation.MainFeed.components.MainFeedState
import com.example.frontend.presentation.posts.components.PostStringState
import com.example.frontend.presentation.posts.components.PostsState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainFeedViewModel @Inject constructor (
    private val getPostsIFollowUseCase: GetPostsIFollowUseCase,
    private val likeOrUnlikePostUseCase: LikeOrUnlikePostUseCase,
    application: Application
): ViewModel() {
    private val _stateDelete = mutableStateOf(PostStringState())
    val stateDelete : State<PostStringState> = _stateDelete;
    var loginUserId = 0L;
    private val _stateLikeOrUnlike = mutableStateOf(PostStringState())
    val stateLikeOrUnlike : State<PostStringState> = _stateLikeOrUnlike;
    var access_token = "";
    var refresh_token = "";
    private val _state = mutableStateOf(MainFeedState())
    val state : State<MainFeedState> = _state
    val context = application.baseContext

    var allPosts = mutableStateOf<List<Post>?>(null);
    init {
        Constants.sort = 1;
        getPostsIFollow()
    }

    fun getPostsIFollow() {
        GlobalScope.launch(Dispatchers.Main) {
            loginUserId = DataStoreManager.getLongValue(context, "userId");
            access_token =  DataStoreManager.getStringValue(context, "access_token").trim();
            getPostsIFollowUseCase("Bearer "+access_token).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _state.value = MainFeedState(posts = result.data!!)
                        allPosts.value = getPosts();
                    }
                    is Resource.Error -> {
                        _state.value = MainFeedState(error = result.message!!)
                    }
                    is Resource.Loading -> {
                        _state.value = MainFeedState( isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }
    fun getPosts():List<Post>{
        if (Constants.sort == 1){
            return _state.value.posts!!
        }
        else if (Constants.sort == 0){
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

    fun proveriConstants()
    {
        /*if(Constants.refreshComments != 0L || Constants.postLikeChangedSinglePostPage){
            Constants.refreshComments = 0L
            Constants.postLikeChangedSinglePostPage = false
            getPostsIFollow()
        }*/
        if(Constants.singlePostPageChanged) {
            Constants.singlePostPageChanged = false
            getPostsIFollow()
        }
    }
}