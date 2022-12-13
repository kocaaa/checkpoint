package com.example.frontend.presentation.location

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.frontend.domain.model.Location
import com.example.frontend.presentation.MainFeed.MainFeedScreen
import com.example.frontend.presentation.destinations.*
import com.example.frontend.ui.theme.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Destination
@Composable
fun MainLocationScreen(
    navigator : DestinationsNavigator,
    viewModel : LocationViewModel = hiltViewModel()
)
{
    val state = viewModel.state.value
    var searchText by remember{ mutableStateOf("") }

    viewModel.proveriConstants()

    val context = LocalContext.current
    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }
    val localDensity = LocalDensity.current

    var mapWidth by remember {
        mutableStateOf(44)
    }

    var mapHeight by remember {
        mutableStateOf(20)
    }

    val camPosState = rememberCameraPositionState{
    }

    if(state.error.contains("403")){
        navigator.navigate(LoginScreenDestination){
            popUpTo(MainLocationScreenDestination.route){
                inclusive = true;
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    )
    {
        ProfileTopBar(navigator, viewModel)

        Spacer(Modifier.height(10.dp))

        LocationSearchBar(searchText, onChange = {
            searchText = it
            if(searchText == ""){
                viewModel.getAllLocations();
            }
            else{
                viewModel.searchLocations(searchText)
            }
        })
        
        Spacer(Modifier.height(15.dp));

        if(state.isLoading){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        else{

            if(state.locations.isEmpty()){
                Text("No posts from searched location")
            }
            else{
                val oneLocation = LatLng(state.locations[0].lat, state.locations[0].lng)
                camPosState.position = CameraPosition.fromLatLngZoom(oneLocation, 1f);
            }
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp)
                    .onGloballyPositioned { coords ->
                        mapWidth = with(localDensity) { coords.size.width }
                        mapHeight = with(localDensity) { coords.size.height }
                    },
                uiSettings = uiSettings,
                onMapLoaded = {
                    },
                cameraPositionState = camPosState
            ) {
                state.locations.forEach{ location->
                    val markerState : MarkerState = rememberMarkerState()
                    markerState.position = LatLng(location.lat, location.lng)
                    Marker(
                        state = markerState,
                        title = location.name,
                        //snippet = "See all posts",
                        snippet = "Click long for all posts",
//                        onInfoWindowClick = {
//                            it.hideInfoWindow()
//                            navigator.navigate(PostsScreenDestination(location.id))
//                        }
                        onInfoWindowLongClick = {
                            it.hideInfoWindow()
                            navigator.navigate(PostsScreenDestination(location.id))
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun LocationSearchBar(
    searchText : String,
    onChange: (String) -> Unit = {}
)
{
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
            Text("Search locations")
        },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        )//,
        //shape = RoundedCornerShape(20.dp)
    )
}

@Composable
fun LocationList(
    locationList : List<Location>?,
    navigator : DestinationsNavigator
)
{

    if(locationList == null || locationList.isEmpty()){
        Text(
            text = "No locations found!",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }

    else{
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ){
            items(locationList){
                    location -> LocationCard(location = location, navigator)
            }
        }
    }
}


@Composable
fun LocationCard(
    location : Location,
    navigator : DestinationsNavigator
)
{
    val painter = rememberImagePainter(
        data = "https://www.moneycrashers.com/wp-content/uploads/2018/01/thailand-trip-travel-budget.jpg",
        builder = {

        }
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Image(painter = painter,
            contentDescription = "Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable {
                    navigator.navigate(
                        PostsScreenDestination(location.id)
                    )
                }
                .height(200.dp)
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(){
            Text(text = location.name)
        }
    }
}

@Composable
fun ProfileTopBar(
    navigator : DestinationsNavigator,
    viewModel: LocationViewModel
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
                Button(onClick = {
                    navigator.navigate(MainFeedScreenDestination())
                },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    contentPadding = PaddingValues(start = 8.dp, end = 15.dp),
                    border = BorderStroke(1.2.dp, MyColorTopBar)
                ){
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "",
                        tint = MyColorTopBar
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Feed",
                        color = MyColorTopBar,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    navigator.navigate(MainLocationScreenDestination())
                },
                    contentPadding = PaddingValues(start = 5.dp, end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MyColorTopBar),
                ){
                    Icon(
                        Icons.Default.Explore,
                        contentDescription = "",
                        tint = Color.White
                    )
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = "Explore",
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
                    viewModel.getAllLocations()
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