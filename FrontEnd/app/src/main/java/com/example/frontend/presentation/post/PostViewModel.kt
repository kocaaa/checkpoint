package com.example.frontend.presentation.post

import Constants
import Constants.Companion.POST_ID
import android.app.Application
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.frontend.common.Resource
import com.example.frontend.domain.DataStoreManager
import com.example.frontend.domain.model.Comment
import com.example.frontend.domain.use_case.get_post.GetPostUseCase
import com.example.frontend.domain.use_case.get_user.GetMyProfilePictureUseCase
import com.example.frontend.domain.use_case.post_comments.AddCommentUseCase
import com.example.frontend.domain.use_case.post_comments.DeleteCommentUseCase
import com.example.frontend.domain.use_case.post_comments.GetFirstCommentsUseCase
import com.example.frontend.domain.use_case.post_likes.LikeOrUnlikePostUseCase
import com.example.frontend.presentation.map.MapEvent
import com.example.frontend.presentation.map.MapStyle
import com.example.frontend.presentation.map.components.MapState
import com.example.frontend.presentation.post.components.AddCommentState
import com.example.frontend.presentation.post.components.DeleteCommentState
import com.example.frontend.presentation.post.components.PostCommentsState
import com.example.frontend.presentation.post.components.PostState
import com.example.frontend.presentation.posts.components.PostStringState
import com.example.frontend.presentation.posts.components.PostsState
import com.example.frontend.presentation.profile.components.ProfilePictureState
import com.google.android.gms.maps.model.MapStyleOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val getPostUseCase : GetPostUseCase,
    private val getFirstCommentsUseCase: GetFirstCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val likeOrUnlikePostUseCase: LikeOrUnlikePostUseCase,
    private val deleteCommentUseCase: DeleteCommentUseCase,
    private val getMyProfilePictureUseCase: GetMyProfilePictureUseCase,
    private val savedStateHandle: SavedStateHandle,
    application: Application
) : ViewModel() {

    private val _state = mutableStateOf(PostState())
    val state : State<PostState> = _state
    val context = application.baseContext

    private val _stateGetComments = mutableStateOf(PostCommentsState())
    val stateGetComments : State<PostCommentsState> = _stateGetComments

    private val _stateAddComment = mutableStateOf(AddCommentState())
    val stateAddComment : State<AddCommentState> = _stateAddComment
    var commentText: String = ""
    var parentCommentId = mutableStateOf(0L)
    var replyToUsername = mutableStateOf("")

    private val _stateLikeOrUnlike = mutableStateOf(PostStringState())
    val stateLikeOrUnlike : State<PostStringState> = _stateLikeOrUnlike;

    private val _stateDeleteComment = mutableStateOf(DeleteCommentState())
    val stateDeleteComment : State<DeleteCommentState> = _stateDeleteComment

    var commentCount = mutableStateOf(0)

    private val _stateGetProfilePicture = mutableStateOf(ProfilePictureState())
    val stateGetProfilePicture : State<ProfilePictureState> = _stateGetProfilePicture
    var currentPicture = ""

    private val _stateMap = mutableStateOf(MapState())
    val stateMap : State<MapState> = _stateMap
    var showLocationBool = mutableStateOf(false)


    var access_token = "";
    var refresh_token = "";

    init {
        GlobalScope.launch(Dispatchers.Main){
            access_token =  DataStoreManager.getStringValue(context, "access_token").trim();
            refresh_token = DataStoreManager.getStringValue(context, "refresh_token").trim();

            Log.d("POST VIEW", "*${refresh_token}*");
            getPost()
            getMyProfilePicture()
        }
    }

    fun getPost(){
        var thisPostId = 0L;
        savedStateHandle.get<Long>(POST_ID)?.let { postId ->
            thisPostId = postId;
        }

        getPostUseCase("Bearer "+access_token, thisPostId).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _state.value = PostState(post = result.data)
                    commentCount.value = result.data!!.numberOfComments
                    println(result.data)
                    getFirstCommentsByPostId(result.data!!.postId)
                }
                is Resource.Error -> {
                    _state.value = PostState(error = result.message ?:
                    "An unexpected error occured")

                    if(result.message?.contains("403") == true){
                        GlobalScope.launch(Dispatchers.Main){
                            DataStoreManager.deleteAllPreferences(context);
                        }
                    }
                }
                is Resource.Loading -> {
                    _state.value = PostState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getFirstCommentsByPostId(postId: Long)
    {
        var parentCommentId = 0L;
        var replyToUsername = "";

        GlobalScope.launch(Dispatchers.Main){

            access_token =  DataStoreManager.getStringValue(context, "access_token");
            refresh_token = DataStoreManager.getStringValue(context, "refresh_token");

            getFirstCommentsUseCase("Bearer "+access_token, postId).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _stateGetComments.value = PostCommentsState(comments = result.data)
                        println(result.data)
                    }
                    is Resource.Error -> {
                        _stateGetComments.value = PostCommentsState(error = result.message ?:
                        "An unexpected error occured")
                    }
                    is Resource.Loading -> {
                        _stateGetComments.value = PostCommentsState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getMyProfilePicture(){
        getMyProfilePictureUseCase("Bearer "+access_token).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _stateGetProfilePicture.value = ProfilePictureState(profilePicture = result.data ?: "")
                    currentPicture = result.data!!
                }
                is Resource.Error -> {
                    _stateGetProfilePicture.value = ProfilePictureState(error = result.message ?:
                    "An unexpected error occured")
                    if(result.message?.contains("403") == true){
                        GlobalScope.launch(Dispatchers.Main){
                            DataStoreManager.deleteAllPreferences(context);
                        }
                    }
                }
                is Resource.Loading -> {
                    _stateGetProfilePicture.value = ProfilePictureState(isLoading = true)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun addComment(postId: Long, parentCommId: Long, commentText: String)
    {
        GlobalScope.launch(Dispatchers.Main){

            addCommentUseCase("Bearer "+access_token, commentText.trim(), postId, parentCommId).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _stateAddComment.value = AddCommentState(message = result.data ?: "")

                        parentCommentId.value = 0L;
                        commentCount.value += 1
                        getFirstCommentsByPostId(postId)
                        //Constants.refreshComments = 1L
                        Constants.singlePostPageChanged = true
                    }
                    is Resource.Error -> {
                        _stateAddComment.value = AddCommentState(error = result.message ?:
                        "An unexpected error occured")
                        println("GRESKA U CUVANJU KOMENTARA " + result.message)
                    }
                    is Resource.Loading -> {
                        _stateAddComment.value = AddCommentState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun likeOrUnlikePostById(postId: Long)
    {
        likeOrUnlikePostUseCase("Bearer "+access_token, postId).onEach { result ->
            when(result){
                is Resource.Success -> {
                    _stateLikeOrUnlike.value = PostStringState(message = result.data ?: "")
                    //Constants.postLikeChangedSinglePostPage = true
                    Constants.singlePostPageChanged = true
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

    fun deleteCommentById(commentId: Long, postId: Long, order: Int, size: Int)
    {
        GlobalScope.launch(Dispatchers.Main){

            deleteCommentUseCase("Bearer "+access_token, commentId).onEach { result ->
                when(result){
                    is Resource.Success -> {
                        _stateDeleteComment.value = DeleteCommentState(message = result.data ?: "")

                        getFirstCommentsByPostId(postId)
                        if (order == 1)
                            commentCount.value = commentCount.value - 1 - size
                        else
                            commentCount.value = commentCount.value - 1
                        //Constants.refreshComments = 1L
                        Constants.singlePostPageChanged = true
                    }
                    is Resource.Error -> {
                        _stateDeleteComment.value = DeleteCommentState(error = result.message ?:
                        "An unexpected error occured")
                        println("GRESKA U BRISANJU KOMENTARA " + result.message)
                    }
                    is Resource.Loading -> {
                        _stateDeleteComment.value = DeleteCommentState(isLoading = true)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    fun onEvent(event : MapEvent)
    {
        when(event){
            is MapEvent.ToggleLightMap -> {
                _stateMap.value = _stateMap.value.copy(
                    properties = _stateMap.value.properties.copy(
                        mapStyleOptions = if(_stateMap.value.isLightMap) {
                            null
                        } else MapStyleOptions(MapStyle.json),
                    ),
                    isLightMap = !_stateMap.value.isLightMap
                )
            }
            is MapEvent.OnMapLongClick -> {

            }
            is MapEvent.OnInfoWindowLongClick -> {

            }
        }
    }

}