package com.example.frontend.presentation.post

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.util.lerp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.example.frontend.domain.model.Comment
import com.example.frontend.domain.model.Photo
import com.example.frontend.presentation.destinations.LoginScreenDestination
import com.example.frontend.presentation.destinations.MainLocationScreenDestination
import com.example.frontend.presentation.destinations.ProfileScreenDestination
import com.google.accompanist.pager.*
import com.google.accompanist.pager.ExperimentalPagerApi
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import com.example.frontend.presentation.map.MapEvent
import com.example.frontend.presentation.posts.LikeOrUnlikePostButton
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.*
import kotlin.math.absoluteValue

import android.graphics.Camera
import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.frontend.domain.model.Post
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.CameraUpdateFactory.zoomTo
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*


@RequiresApi(Build.VERSION_CODES.O)
@Destination
@Composable
fun PostScreen(
    postId : Long,
    navigator : DestinationsNavigator,
    viewModel: PostViewModel = hiltViewModel()
)
{
    val state = viewModel.state.value
    val stateGetComments = viewModel.stateGetComments.value

    if(state.error.contains("403")){
        navigator.navigate(LoginScreenDestination){
            popUpTo(MainLocationScreenDestination.route){
                inclusive = true;
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {

        IconButton(onClick = {
            navigator.popBackStack()
        }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "",
                tint = Color.DarkGray
            )
        }

        if(state.isLoading || stateGetComments.isLoading){
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
        else if (state.error != "" || stateGetComments.error != "") {
            Text("An error occured while loading this post!");
        }
        else if(state.post!=null){
            PostDetails(post = state.post, comments = stateGetComments.comments, navigator = navigator, viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PostDetails(
    post: Post,
    comments: List<Comment>?,
    navigator: DestinationsNavigator,
    viewModel: PostViewModel
){

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
    ) {

        CardHeader(post, navigator, viewModel)

        if (viewModel.showLocationBool.value)
            PostMap(post, navigator, viewModel)
        else {
            if(post.photos.size > 0)
                ImagePagerSlider(post, post.photos) //photo slider
        }

        PostDescriptionAndLikes(post, viewModel)

        PostComments(post, comments, navigator, viewModel)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalPagerApi
@Composable
fun CardHeader(
    post : Post,
    navigator: DestinationsNavigator,
    viewModel: PostViewModel
){

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 1.dp,
        shape = RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Row(
                modifier = Modifier
                    .clickable{
                        navigator.navigate(ProfileScreenDestination(post.appUserId, post.appUserUsername))
                    },
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
                                .height(30.dp)
                                .width(30.dp)
                                .clip(CircleShape),
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp),
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
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = post.date,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
            ClickableText(
                text = AnnotatedString(
                    text = if (viewModel.showLocationBool.value) "Show photos" else "Show location"
                ),
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                ),
                onClick = {
                    //prikazi lokaciju umesto slajdera
                    viewModel.showLocationBool.value = !viewModel.showLocationBool.value
                }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalPagerApi
@Composable
fun ImagePagerSlider(
    post: Post,
    photos: List<Photo>
){
    val pagerState = rememberPagerState(
        pageCount = photos.size,
        initialPage = 0
    )
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 1.dp,
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
            ){

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 15.dp, 0.dp, 15.dp)
                ) {
                        page->
                    Card(
                        modifier = Modifier
                            .graphicsLayer {
                                val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                                lerp(
                                    start = 0.85f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                ).also { scale ->
                                    scaleX = scale
                                    scaleY = scale
                                }

                                alpha = lerp(
                                    start = 0.5f,
                                    stop = 1f,
                                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                                )
                            }
                            .fillMaxWidth()
                    ){

                        val photo = photos[page]
                        val decoder = Base64.getDecoder()
                        val photoBytes = decoder.decode(photo.photo.data)
                        if(photoBytes.size>1) {
                            val mapa: Bitmap =
                                BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
                            print(mapa.byteCount)
                            if (mapa != null) {
                                Image(
                                    bitmap = mapa.asImageBitmap(),
                                    contentDescription = "",
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                indicatorWidth = 6.dp,
                indicatorHeight = 6.dp,
                activeColor = Color.Blue,
                inactiveColor = Color.LightGray
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalPagerApi
@Composable
fun PostMap(
    post : Post,
    navigator: DestinationsNavigator,
    viewModel: PostViewModel
){
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 1.dp,
        shape = RectangleShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(256.dp),
            ){

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(0.dp, 15.dp, 0.dp, 15.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        MapView(post, viewModel)
                    }
                }
            }
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MapView(
    post: Post,
    viewModel: PostViewModel
)
{
    val scaffoldState = rememberScaffoldState()
    val uiSettings = remember{
        MapUiSettings(zoomControlsEnabled = false)
    }

    val postLocation = LatLng(post.location.lat, post.location.lng)
    val camPosState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(postLocation, 1f);
    }
    val builder = LatLngBounds.Builder()

    val localDensity = LocalDensity.current
    var mapWidth by remember{
        mutableStateOf(0)
    }

    var mapHeight by remember{
        mutableStateOf(0)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.onEvent(MapEvent.ToggleLightMap)
            }) {
                Icon(imageVector = if(viewModel.stateMap.value.isLightMap){
                    Icons.Default.ToggleOff
                } else Icons.Default.ToggleOn,
                    contentDescription = "Toggle fallout map"
                )
            }
        }
    ) {
        GoogleMap (
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coords ->
                    mapWidth = with(localDensity){coords.size.width}
                    mapHeight = with(localDensity){coords.size.height}
                },
            properties = viewModel.stateMap.value.properties,
            uiSettings = uiSettings,
            onMapLoaded = {

            },
            onMapLongClick = {
                Log.d("Long click", "Map long click");
                Log.d("LATLNG", it.toString());
            },
            cameraPositionState = camPosState
        ){

            val postLocation = LatLng(post.location.lat, post.location.lng)
//            builder.include(postLocation)


            val markerState : MarkerState = rememberMarkerState()
            markerState.position = postLocation

            Marker(
                state = markerState,
                title = post.location.name,
                onInfoWindowClick = {},
                onInfoWindowLongClick = {},
                icon = BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE
                )
            )

//            updateMapCamera(camPosState, builder, mapWidth, mapHeight, postLocation)
        }
    }
}

fun updateMapCamera(
    cameraPositionState: CameraPositionState,
    builder : LatLngBounds.Builder,
    width: Int,
    height : Int,
    postLocation: LatLng
){
    val padding = (width * 0.20).toInt();
    cameraPositionState.move(
        update = CameraUpdateFactory.newLatLngBounds(builder.build(), width, height, padding)
    )

}


@Composable
fun PostDescriptionAndLikes(
    post : Post,
    viewModel: PostViewModel
){
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 1.dp,
        shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 15.dp)
        ) {
            Text(
                text = post.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    var likedBool by remember{ mutableStateOf(post.isLiked) }
                    var likeCount by remember{ mutableStateOf(post.numberOfLikes) }

                    IconButton(onClick = {
                        viewModel.likeOrUnlikePostById(post.postId)
                        likedBool = !likedBool
                        if (likedBool)
                            likeCount += 1;
                        else
                            likeCount -= 1;
                    }) {
                        Icon(
                            if (likedBool) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (likedBool) "Unlike" else "Like",
                            tint = if (likedBool) Color.Red else Color.DarkGray,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Text(
                        text = "${likeCount} likes",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Row {
                    Text(
                        text = "${viewModel.commentCount.value} comments",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PostComments(
    post : Post,
    comments: List<Comment>?,
    navigator: DestinationsNavigator,
    viewModel: PostViewModel
){

    AddFirstCommentCard(post, viewModel)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(0.dp, 500.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (!(comments.isNullOrEmpty()))
            Column {
                comments.forEach{ comment ->
                    CommentCard(post, comment, navigator, viewModel)
                }
            }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddFirstCommentCard(
    post : Post,
    viewModel: PostViewModel
) {

    Card(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        elevation = 1.dp,
        shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var newCommentText by remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current

            BasicTextField(
                modifier = Modifier
                    .weight(3f)
                    .border(
                        BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(6.dp)
                    /*.background(
                        color = if (viewModel.replyToUsername.value == "") Color.White else Color.LightGray
                    )*/,
                value = newCommentText,
                onValueChange = { newCommentText = it },
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.padding(2.dp)
                    ) {
                        if (newCommentText.isEmpty()) {
                            Text(
                                text = "Add a comment...",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = LocalTextStyle.current.copy(color = Color.DarkGray, fontSize = 14.sp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                    if (newCommentText.trim().isNotBlank()) {
                        viewModel.addComment(post.postId, 0L, newCommentText)
                        newCommentText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                ),
                modifier = Modifier
                    .height(30.dp)
            ) {
                Text(
                    text = "Add",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Blue
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddSecondCommentCard(
    post : Post,
    viewModel: PostViewModel
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 60.dp, end = 10.dp, top = 5.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val photo = viewModel.currentPicture
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
                            .height(28.dp)
                            .width(28.dp)
                            .clip(CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            var newSecondCommentText by remember { mutableStateOf("") }
            val focusManager = LocalFocusManager.current

            BasicTextField(
                modifier = Modifier
                    .weight(3f)
                    .border(
                        BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(2.dp)
                /*.background(
                    color = if (viewModel.replyToUsername.value == "") Color.White else Color.LightGray
                )*/,
                value = newSecondCommentText,
                onValueChange = { newSecondCommentText = it },
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.padding(3.dp)
                    ) {
                        if (newSecondCommentText.isEmpty()) {
                            Text(
                                text = "Replying to ${viewModel.replyToUsername.value}",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                        innerTextField()
                    }
                },
                textStyle = LocalTextStyle.current.copy(color = Color.DarkGray, fontSize = 12.sp)
            )

            Spacer(modifier = Modifier.width(10.dp))
            Button(
                onClick = {
                    if (newSecondCommentText.trim().isNotBlank()) {
                        viewModel.addComment(post.postId, viewModel.parentCommentId.value, newSecondCommentText)
                        newSecondCommentText = ""
                        viewModel.replyToUsername.value = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.White
                ),
                modifier = Modifier
                    .height(30.dp)
                    .width(60.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "Add",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    color = Color.Blue
                )
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CommentCard(
    post : Post,
    comment: Comment,
    navigator: DestinationsNavigator,
    viewModel: PostViewModel
){

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val photo = comment.image
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
                                .height(30.dp)
                                .width(30.dp)
                                .clip(CircleShape)
                        )
                    }
                }
                Column (
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 0.dp),
                ) {
                    Text(
                        text = comment.authorUsername,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = comment.text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(3.dp))


                    ClickableText(
                        text = AnnotatedString(
                            text = "Reply"
                        ),
                        style = TextStyle(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        ),
                        onClick = {
                            viewModel.replyToUsername.value = comment.authorUsername
                            viewModel.parentCommentId.value = comment.id
                        }
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (comment.canDelete) {
                    IconButton(onClick = {
                        viewModel.deleteCommentById(comment.id, post.postId, 1, comment.subCommentList.size)
                    }, modifier = Modifier
                        .size(20.dp)
                        .padding(end = 5.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "",
                            tint = Color.DarkGray
                        )
                    }
                }
            }
        }
    }

    //Divider(color = Color.LightGray, modifier = Modifier.fillMaxWidth().width(1.dp))

    comment.subCommentList.forEach {
        subComment -> SubCommentCard(post = post, comment = comment, subComment = subComment, navigator = navigator, viewModel = viewModel)
    }

    if (viewModel.parentCommentId.value == comment.id) {
        AddSecondCommentCard(post = post, viewModel = viewModel)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SubCommentCard(
    post : Post,
    comment: Comment,
    subComment: Comment,
    navigator: DestinationsNavigator,
    viewModel: PostViewModel
){
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RectangleShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 60.dp, end = 10.dp, top = 0.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val photo = subComment.image
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
                                .height(28.dp)
                                .width(28.dp)
                                .clip(CircleShape)
                        )
                    }
                }
                Column (
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 0.dp, bottom = 0.dp),
                ) {
                    Text(
                        text = subComment.authorUsername,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subComment.text,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(3.dp))

                    ClickableText(
                        text = AnnotatedString(
                            text = "Reply"
                        ),
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        ),
                        onClick = {
                            viewModel.parentCommentId.value = comment.id
                            viewModel.replyToUsername.value = subComment.authorUsername
                        }
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (subComment.canDelete) {
                    IconButton(onClick = {
                        viewModel.deleteCommentById(subComment.id, post.postId, 2, 0)
                    }, modifier = Modifier
                        .size(19.dp)
                        .padding(end = 5.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "",
                            tint = Color.DarkGray
                        )
                    }
                }
            }
        }
    }

    //Divider(color = Color.LightGray, modifier = Modifier.fillMaxWidth().width(1.dp))
}