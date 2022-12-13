package com.example.frontend.presentation.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.frontend.domain.model.Post
import com.example.frontend.presentation.destinations.*
import com.example.frontend.presentation.location.LocationCard
import com.example.frontend.presentation.map.MapWindow
import com.example.frontend.presentation.posts.*
import com.example.frontend.presentation.posts.components.PostStringState
import com.example.frontend.presentation.profile.components.UserPostsState
import com.example.frontend.presentation.profile.components.ProfileDataState
import com.example.frontend.presentation.profile.components.ProfilePictureState
import com.example.frontend.ui.theme.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Destination
@Composable
fun ProfileScreen(
    userId : Long,
    username : String,
    navigator : DestinationsNavigator,
    viewModel: ProfileViewModel = hiltViewModel()
)
{
    val context = LocalContext.current
    val state = viewModel.state.value
    val pictureState = viewModel.pictureState.value
    val postsState = viewModel.postsState.value

    viewModel.proveriConstants()

    val isRefreshing by viewModel.isRefreshing.collectAsState()

    if(state.error.contains("403") || pictureState.error.contains("403") || postsState.error.contains("403")){
        if(state.error.contains("403")) Log.d("STATE", "ERROR 403");
        if(pictureState.error.contains("403")) Log.d("PICTURE STATE", "ERROR 403");
        if(postsState.error.contains("403")) Log.d("POSTS STATE", "ERROR 403");

        navigator.navigate(LoginScreenDestination){
            popUpTo(MainLocationScreenDestination.route){
                inclusive = true;
            }
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = {
            viewModel.getProfileData();
        }
    ){

        if(state.isLoading || pictureState.isLoading || postsState.isLoading){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                CircularProgressIndicator();
            }
        }
        else{
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                TopBar(
                    name =
                    if(viewModel.savedUserId == viewModel.loginUserId)
                        viewModel.username
                    else
                        username
                    ,
                    modifier = Modifier.padding(20.dp),
                    navigator = navigator,
                    viewModel = viewModel
                )

                ProfileSection(navigator, state, pictureState, viewModel.savedUserId);
                Spacer(modifier = Modifier.height(25.dp))

                //ako nije moj profil
                if(viewModel.savedUserId != viewModel.loginUserId){
                    ButtonSection(viewModel, modifier = Modifier.fillMaxWidth());
                    Spacer(modifier = Modifier.height(20.dp))
                }

                if(viewModel.savedUserId != 0L){
                    UserPostsSection(postsState, viewModel.savedUserId, viewModel, navigator);
                }
            }
        }

    }
}

@Composable
fun TopBar(
    name : String,
    modifier: Modifier = Modifier,
    navigator : DestinationsNavigator,
    viewModel: ProfileViewModel
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ){
        Row(
            horizontalArrangement = Arrangement.Start
        ){
            IconButton(onClick = {
                navigator.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MyColorTopBar,
                    modifier = modifier.size(24.dp),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = name,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ){
            if(viewModel.savedUserId == viewModel.loginUserId)
            {
                IconButton(onClick = {
                    navigator.navigate(
                        ProfileSettingsScreenDestination()
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Back",
                        tint = MyColorTopBar,
                        modifier = modifier.size(24.dp)
                    )
                }
                IconButton(onClick = {
                    viewModel.logoutUser(navigator);
                }) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = "",
                        tint = MyColorTopBar
                    )
                }
            }
            else{
                IconButton(onClick = {
                    /* */
                }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Back",
                        tint = Color.Transparent,
                        modifier = modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfileSection(
    navigator : DestinationsNavigator,
    state : ProfileDataState,
    pictureState : ProfilePictureState,
    userId : Long,
    modifier: Modifier = Modifier,
)
{
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {

            val painter = rememberImagePainter(
                data = "https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png",
                builder = {}
            )
            if(pictureState.error != ""){
                Image(
                    painter = painter,
                    contentDescription = "Profile image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)
                        .clip(CircleShape),
                )
            }
            else{
                val photo = pictureState.profilePicture;
                val decoder = Base64.getDecoder()
                val photoBytes = decoder.decode(photo)
                if(photoBytes.size>1){
                    val mapa: Bitmap = BitmapFactory.decodeByteArray(photoBytes,0,photoBytes.size)
                    print(mapa.byteCount)
                    if(mapa!=null){
                        Image(
                            bitmap = mapa.asImageBitmap(),
                            modifier = Modifier
                                .height(100.dp)
                                .width(100.dp)
                                .clip(CircleShape),
                            contentDescription ="Profile image" ,
                            contentScale = ContentScale.Crop
                        )
                    }
                }
                else{
                    //def picture
                    Image(
                        painter = painter,
                        contentDescription = "Profile image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .height(100.dp)
                            .width(100.dp)
                            .clip(CircleShape),
                    )
                }
            }
            
            //Spacer(modifier = Modifier.width(8.dp))

            StatSection(navigator, state, userId, modifier.weight(7f))

        }
    }
}


@Composable
fun StatSection(
    navigator : DestinationsNavigator,
    state : ProfileDataState,
    userId: Long,
    modifier: Modifier = Modifier
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ){
        if(state.error.isNotBlank()){
            Text("Error!");
        }

        if(state.profileData != null){
            ProfileStat(numberText = state.profileData.postCount.toString(), text = "Posts",
            onClick = {})
            ProfileStat(numberText = state.profileData.followersCount.toString(), text = "Followers",
            onClick = {
                navigator.navigate(
                    UserListScreenDestination("followers", userId)
                )
            })
            ProfileStat(numberText = state.profileData.followingCount.toString(), text = "Following",
            onClick = {
                navigator.navigate(
                    UserListScreenDestination("following", userId)
                )
            })
        }
    }
}

@Composable
fun ProfileStat(
    numberText : String,
    text : String,
    modifier: Modifier = Modifier,
    onClick : () -> Unit = {}
)
{
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable{
                onClick()
            }
    )    {
        Text(
            text = numberText,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(text = text)
    }
}

@Composable
fun ButtonSection(
    viewModel : ProfileViewModel,
    modifier: Modifier = Modifier
){
    val minWidth = 120.dp
    val height = 35.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)

    ){

        Button(
            onClick = {
                viewModel.followUnfollowUser()
            },
            colors = androidx.compose.material.ButtonDefaults.buttonColors(
                backgroundColor = if (viewModel.state.value.profileData!!.amFollowing) Color.White else MyColorTopBar
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            if (!viewModel.state.value.profileData!!.amFollowing) {
                Icon(
                    Icons.Default.Add,
                    contentDescription ="",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            else{
                Icon(
                    Icons.Default.Remove,
                    contentDescription ="",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = if (viewModel.state.value.profileData!!.amFollowing) "Unfollow" else "Follow",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (viewModel.state.value.profileData!!.amFollowing) MyColorTopBar else Color.White
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserPostsSection(
    postsState : UserPostsState,
    userId: Long,
    viewModel : ProfileViewModel,
    navigator : DestinationsNavigator
)
{
    var list by remember{ mutableStateOf(true) }
    var map by remember{ mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ){
        Button(onClick = {
            list = true;
            map = false;
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = if(list) MyColorTopBar else Color.LightGray,
                contentColor = Color.White
            ),
            //modifier = Modifier.width(120.dp).height(45.dp),
            modifier = Modifier.width(110.dp).height(45.dp),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "POSTS",
                letterSpacing = 1.2.sp
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Button(onClick = {
            list = false;
            map = true;
        },
            colors = ButtonDefaults.buttonColors(
                containerColor = if(map) MyColorTopBar else Color.LightGray,
                contentColor = Color.White
            ),
            //modifier = Modifier.width(120.dp).height(45.dp),
            modifier = Modifier.width(110.dp).height(45.dp),
            shape = RoundedCornerShape(6.dp)
        ) {
            Text(
                text = "MAP",
                letterSpacing = 1.2.sp
            )
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
    ){
        if(postsState.userPosts!=null){
            if(list){
                PostsSection(userId = userId, postsState.userPosts, viewModel, navigator);
            }
            if(map){
                MapSection(userId = userId, postsState.userPosts,navigator);
            }
        }
        else{
            Text("ERROR");
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostsSection(
    userId : Long,
    posts: List<Post>,
    viewModel : ProfileViewModel,
    navigator : DestinationsNavigator
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        if(posts.size < 1)
        {
            Text("No posts yet");
        }
        else{


            var searchPosts by remember{ mutableStateOf(posts) }
            var searchText by remember{ mutableStateOf("") }

            val trailingIconView = @Composable {
                IconButton(onClick = {
                    searchText = ""
                    searchPosts = posts;
                }) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription ="",
                        tint = Color.Black
                    )
                }
            }

            TextField(
                value = searchText,
                trailingIcon = if(searchText.isNotBlank()) trailingIconView else null,
                onValueChange = {
                    searchText = it
                    if(searchText!="")
                        searchPosts = posts.filter { p ->
                            p.location.name.lowercase().contains(searchText.trim().lowercase());
                        }
                    else
                        searchPosts = posts;
                },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null)},
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "Search posts by location name",
                        fontSize = 15.sp
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = Color.LightGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                //shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))
            LazyColumn{
                items(searchPosts){
                        post -> PostCard(post = post, navigator = navigator, viewModel)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun PostCard(
    post : Post,
    navigator : DestinationsNavigator,
    viewModel : ProfileViewModel
){

    Card(
        modifier = Modifier.padding(vertical = 8.dp),
        elevation = 1.dp,
        backgroundColor = MaterialTheme.colorScheme.surface,
        onClick = {
            navigator.navigate(PostScreenDestination(post.postId))
        }
    ){
        Row {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        val photo = post.image
                        val decoder = Base64.getDecoder()
                        val photoBytes = decoder.decode(photo)
                        if(photoBytes.size>1){
                            val mapa: Bitmap = BitmapFactory.decodeByteArray(photoBytes,0,photoBytes.size)
                            print(mapa.byteCount)
                            if(mapa!=null){
                                Image(
                                    bitmap = mapa.asImageBitmap(),
                                    contentDescription = "Profile image",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .height(35.dp)
                                        .width(35.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalArrangement = Arrangement.Center
                        ) {

                            Text(
                                text = post.appUserUsername,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = post.location.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    if(viewModel.loginUserId == post.appUserId) {
                        DeletePostButton(post = post, viewModel = viewModel)
                    }
                }

                Row(){
                    if(post.photos.size > 0)
                        ImagePagerSliderPostCard(post, post.photos)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = post.date,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 10.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = if (post.description.length < 90) post.description else "${post.description.substring(0, 85)} ...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        LikeOrUnlikePostButton(
                            post = post,
                            viewModel = viewModel,
                            stateLikeOrUnlike = viewModel.stateLikeOrUnlike.value
                        )
                        Text(
                            text = "${post.numberOfLikes} likes",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Row {
                        Text(
                            text = "${post.numberOfComments} comments",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun MapSection(
    userId : Long,
    posts: List<Post>,
    navigator : DestinationsNavigator
)
{
    Column(
        modifier = Modifier
            .padding(
                horizontal = 35.dp,
                vertical = 40.dp
            )
            .border(
                width = Dp.Hairline,
                color = Color.Transparent,
                shape = RoundedCornerShape(5.dp)
            )
    ) {
        MapWindow(userId, posts, navigator);
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DeletePostButton(
    post: Post,
    viewModel: ProfileViewModel
) {
    IconButton(
        onClick = {
            viewModel.deletePostById(post.postId, post.location.id)
        },
        modifier = Modifier.size(30.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete post",
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LikeOrUnlikePostButton(
    post : Post,
    viewModel: ProfileViewModel,
    stateLikeOrUnlike: PostStringState
) {
    IconButton(onClick = {
        viewModel.likeOrUnlikePostById(post.postId)
        post.isLiked = !post.isLiked
        if (post.isLiked)
            post.numberOfLikes += 1;
        else
            post.numberOfLikes -= 1;
    }) {
        Icon(
            if(post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if(post.isLiked) "Unlike" else "Like",
            tint = if(post.isLiked) Color.Red else Color.DarkGray
        )
    }
    if(stateLikeOrUnlike.isLoading){
    }
    else if(stateLikeOrUnlike.error!=""){
        Text("An error occured while like/unlike post!");
    }
}