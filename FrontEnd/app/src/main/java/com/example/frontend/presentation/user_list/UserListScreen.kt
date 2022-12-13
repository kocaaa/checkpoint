package com.example.frontend.presentation.user_list

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.frontend.data.remote.dto.UserDetailedDTO
import com.example.frontend.domain.model.User
import com.example.frontend.presentation.destinations.LoginScreenDestination
import com.example.frontend.presentation.destinations.MainLocationScreenDestination
import com.example.frontend.presentation.destinations.ProfileScreenDestination
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Destination
@Composable
fun UserListScreen(
    userTypeList: String,
    userId : Long,
    navigator : DestinationsNavigator,
    viewModel: UserListViewModel = hiltViewModel()
)
{
    val state = viewModel.state.value
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    var searchText by remember{ mutableStateOf("") }

    viewModel.proveriConstants();

    if(state.error.contains("403")){
        navigator.navigate(LoginScreenDestination){
            popUpTo(MainLocationScreenDestination.route){
                inclusive = true;
            }
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isRefreshing),
        onRefresh = {
            if(searchText == "") viewModel.getAllUsers();
            else viewModel.searchUsers(searchText);
        }
    ){

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 27.dp, vertical = 10.dp)
        ){
            UserListTopBar(modifier = Modifier.padding(vertical = 15.dp), viewModel.typeOfUsers, navigator);
            Spacer(modifier = Modifier.height(5.dp))

            UserListSearchBar(searchText, onChange = {
                searchText = it
                if(searchText == "") viewModel.getAllUsers();
                else viewModel.searchUsers(searchText);
            });
            Spacer(modifier = Modifier.height(5.dp))

            if(state.isLoading){
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            else if(state.users != null){
                UserList(userList = state.users, navigator = navigator)
            }

        }

    }

}

@Composable
fun UserListTopBar(
    modifier: Modifier = Modifier,
    userListType : String,
    navigator : DestinationsNavigator
)
{
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
                    tint = Color.Black,
                    modifier = modifier.size(24.dp),
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = userListType,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.SemiBold
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
        ){
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

@Composable
fun UserListSearchBar(
    searchText : String,
    onChange: (String) -> Unit = {}
){
    var searchTextChange by remember {
        mutableStateOf(searchText)
    }

    val trailingIconView = @Composable {
        IconButton(onClick = {
            onChange("") //isprazni search text
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
            onChange(it)
        },
        leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null)},
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = {
            Text("Search users")
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        //shape = RoundedCornerShape(20.dp)
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserList(
    userList : List<UserDetailedDTO>?,
    navigator: DestinationsNavigator
){
    if(userList == null || userList.isEmpty()){
        Text(
            text = "No users found!",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }
    else{
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = 10.dp,
                vertical = 10.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally

        ){
            items(userList){
                user -> OneUser(user = user, navigator = navigator)
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OneUser(
    user : UserDetailedDTO,
    navigator: DestinationsNavigator
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Log.d(
                    "ID",
                    user.id.toString()
                )
                navigator.navigate(
                    ProfileScreenDestination(user.id, user.username)
                )
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ){

        val photo = user.image
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
                        .height(25.dp)
                        .width(25.dp)
                        .clip(CircleShape),
                )
            }
        }

        Text(
            text = user.username,
            textAlign = TextAlign.Center
        )

        Text(
            text = user.email,
            textAlign = TextAlign.Center
        )

    }
}