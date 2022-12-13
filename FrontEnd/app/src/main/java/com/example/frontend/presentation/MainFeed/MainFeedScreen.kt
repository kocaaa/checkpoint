package com.example.frontend.presentation.MainFeed

import Constants
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.frontend.domain.model.Photo
import com.example.frontend.domain.model.Post
import com.example.frontend.presentation.destinations.*
import com.example.frontend.presentation.posts.components.PostStringState
import com.example.frontend.ui.theme.*
import com.google.accompanist.pager.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.Base64
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.O)
@Destination
@Composable
fun MainFeedScreen(
    navigator: DestinationsNavigator,
    viewModel : MainFeedViewModel = hiltViewModel()
) {

    var sort = remember {
        mutableStateOf(Constants.sort)
    }

    viewModel.proveriConstants()
    var expanded = remember { mutableStateOf(false) }

    val state = viewModel.state.value
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        ProfileMTopBar(navigator,viewModel)
        if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (state.error != "") {
            Text("An error occured while loading posts!");
        } else {
            if(viewModel.getPosts().isNotEmpty()) {
                Row(Modifier.fillMaxWidth()) {
//                    Text(
//                        text = viewModel.nazivSorta(),
//                        modifier = Modifier.clickable{
//                            expanded.value = true
//                        }
//                    )

                    OutlinedTextField(
                        value = viewModel.nazivSorta().trim(),
                        onValueChange = {},
                        //Modifier.width(200.dp),
                        Modifier.fillMaxWidth(),
                        readOnly = true,
                        label = {
                            Text(
                                text = "Sort",
                            )
                        },
                        trailingIcon = {
                            Icon(
                                if(expanded.value == false) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                contentDescription = null,
                                Modifier.clickable  {
                                    Log.d("DROP", "CLICK");
                                    expanded.value = true
                                }
                            )
                        }
                    );

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier
                            .wrapContentWidth()
                            .height(300.dp),
                    ) {
                        listOf(
                            "Date asc",
                            "Date dsc",
                            "Likes asc",
                            "Likes dsc",
                            "Comments asc",
                            "Comments dsc"
                        ).forEachIndexed { index, item ->
                            DropdownMenuItem(onClick = {
                                Constants.sort = index
                                expanded.value = false
                                viewModel.allPosts.value = viewModel.getPosts()
                            }
                            ) {
                                Text(item)
                            }
                        }
                    }
                }
            }
            AllMPosts(viewModel.allPosts.value, navigator, viewModel)
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AllMPosts(
    posts : List<Post>?,
    navigator: DestinationsNavigator,
    viewModel : MainFeedViewModel,
)
{
    if(posts == null || posts.size == 0){
        Text(
            text = "No posts found!",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }
    else{
        LazyColumn{
            items(posts){
                    post -> PostMCard(post, navigator, viewModel)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun PostMCard(
    post : Post,
    navigator : DestinationsNavigator,
    viewModel : MainFeedViewModel,
)
{
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
                                        .height(32.dp)
                                        .width(32.dp)
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


@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalPagerApi
@Composable
fun ImagePagerSliderPostCard(
    post: Post,
    photos: List<Photo>
){
    val pagerState = rememberPagerState(
        pageCount = photos.size,
        initialPage = 0
    )
    Column(
        modifier = Modifier
            .fillMaxWidth(),
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LikeOrUnlikePostButton(
    post : Post,
    viewModel: MainFeedViewModel,
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

@Composable
fun ProfileMTopBar(
    navigator : DestinationsNavigator,
    viewModel: MainFeedViewModel
)
{
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Button(
                    onClick = {
                        navigator.navigate(MainFeedScreenDestination())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MyColorTopBar),
                    contentPadding = PaddingValues(start = 8.dp, end = 15.dp)
                ) {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "",
                        tint = Color.White
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Feed",
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Button(
                    onClick = {
                        navigator.navigate(MainLocationScreenDestination())
                    },
                    contentPadding = PaddingValues(start = 5.dp, end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    border = BorderStroke(1.2.dp, MyColorTopBar)
                ) {
                    Icon(
                        Icons.Default.Explore,
                        contentDescription = "",
                        tint = MyColorTopBar
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Explore",
                        color = MyColorTopBar,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    navigator.navigate(
                        NovPostScreenDestination()
                    )
                }) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = "",
                        tint = MyColorTopBar
                    )
                }

                IconButton(onClick = {
                    navigator.navigate(
                        ProfileScreenDestination(viewModel.loginUserId, "")
                    );

                }) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "",
                        tint = MyColorTopBar
                    )
                }
            }
        }

    }

}
