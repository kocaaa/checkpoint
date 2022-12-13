package com.example.frontend.presentation.newpost

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.lazy.items
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.frontend.domain.model.Photo
import com.example.frontend.presentation.destinations.LoginScreenDestination
import com.example.frontend.presentation.destinations.MainLocationScreenDestination
import com.example.frontend.presentation.destinations.NovPostMapScreenDestination
import com.example.frontend.presentation.newpost.components.SlikaState
import com.example.frontend.ui.theme.MyColorTopBar
import com.google.accompanist.pager.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*
import kotlin.math.absoluteValue

@RequiresApi(Build.VERSION_CODES.O)
@Destination
@Composable
fun NovPostScreen(
    navigator: DestinationsNavigator,
    viewModel : NovPostViewModel = hiltViewModel()
){
    val state = viewModel.state.value

    val context = LocalContext.current

    if(state.error.contains("403")){
        navigator.navigate(LoginScreenDestination){
            popUpTo(MainLocationScreenDestination.route){
                inclusive = true;
            }
        }
    }


    viewModel.proveriConstants()

    var selected = remember {
        mutableStateOf(0)
    }


    var setLocationStep by remember{ mutableStateOf(true) }
    var choosePhotosStep by remember{ mutableStateOf(false) }


    Column(
        Modifier
            .padding(20.dp)
            .fillMaxSize()
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = {
                    if(setLocationStep)
                        navigator.popBackStack()
                    else if(choosePhotosStep){
                        setLocationStep = true;
                        choosePhotosStep = false;
                    }
                }
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "",
                        tint = Color.DarkGray)
                }

                Spacer(Modifier.width(10.dp));

                Text(
                    text = "New post",
                    fontSize = 20.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.End
            ){
                if(setLocationStep){
                    IconButton(
                        onClick = {
                            if((viewModel.markerPOI.value != null || viewModel.markerLatLng.value != null) && viewModel.imeLokacije.value != ""){
                                setLocationStep = false;
                                choosePhotosStep = true;

//                                    if (markerLatLng.value != null) {
//                                        viewModel.saveLocation(imeLokacije.value, markerLatLng.value!!, navigator)
//                                    }
//                                    else {
//                                        viewModel.saveLocation(
//                                            markerPOI.value!!.name,
//                                            markerPOI.value!!.latLng,
//                                            navigator)
//                                    }
                            }
                            else{
                                //alert da ne moze da ne izabere lokaciju a da ide dalje
                                Toast.makeText(
                                    context,
                                    if(viewModel.imeLokacije.value != "") "You need to choose location!" else "You need to add location name!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "",
                            tint = MyColorTopBar)
                    }
                }
                else if(choosePhotosStep){
                    Button(
                        onClick = {
                            Log.d("PHOTOS", viewModel.givePhotos().toString());
                            Log.d("DESCRIPTION", viewModel.description.value);
                            if (viewModel.markerLatLng.value != null){
                                Log.d("LOCATION", viewModel.markerLatLng.value.toString());
                            }
                            else{
                                Log.d("LOCATION", viewModel.markerPOI.value?.latLng.toString());
                            }
                            Log.d("LOCATION NAME", viewModel.imeLokacije.value);



                            if(viewModel.givePhotos().isNotEmpty() && viewModel.imeLokacije.value!=""){
//                                    viewModel.savePost(
//                                        navigator,
//                                        viewModel.description.value,
//                                        viewModel.location.value.id
//                                    )

                                if (viewModel.markerLatLng.value != null) {
                                    viewModel.saveLocation(viewModel.imeLokacije.value, viewModel.markerLatLng.value!!, navigator)
                                }
                                else {
                                    viewModel.saveLocation(
                                        viewModel.markerPOI.value!!.name,
                                        viewModel.markerPOI.value!!.latLng,
                                        navigator)
                                }
                            }
                            else{
                                //alert da ne moze da ne izabere objavi bez slika
                                Toast.makeText(
                                    context,
                                    if(viewModel.givePhotos().isEmpty()) "You need to add photos!" else if(viewModel.imeLokacije.value == "") "You need to choose location" else "Error",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    ) {
                        Text("Post")
                    }
                }
            }
        }
        Spacer(Modifier.height(5.dp));

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ){
            if(choosePhotosStep){
                ChoosePhotos(viewModel = viewModel, navigator = navigator, context = context)
            }
            if(setLocationStep){
                SetLocation(viewModel = viewModel, navigator = navigator, context = context, changeLocationName = {
                    viewModel.imeLokacije.value = it
                }, changeMarkerLatLng = {
                    viewModel.markerLatLng.value = it
                }, changeMarkerPOI = {
                    viewModel.markerPOI.value = it
                })
            }
        }
    }
}

@Composable
fun slika(
    navigator: DestinationsNavigator,
    photo:SlikaState,
    viewModel : NovPostViewModel
){
    val configuration = LocalConfiguration.current;
    val screenWidth = configuration.screenWidthDp.dp;

    Row(
        Modifier
            .border(width = 2.dp, color = Color.DarkGray, shape = RoundedCornerShape(20.dp))
            .wrapContentHeight()
            .width(screenWidth - 40.dp)
            .padding(20.dp)

    ){
        Column() {
            Image(bitmap = photo.slika.asImageBitmap(),"",
                Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 0.dp, 0.dp, 5.dp)
            )
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
                IconButton(onClick = { viewModel.deletePhoto(photo.slika) },
                    Modifier
                        .height(50.dp)
                        .border(width = 0.dp, color = Color.Gray, shape = RoundedCornerShape(50.dp))
                        .background(color = Color.LightGray, shape = RoundedCornerShape(50.dp))
                        .padding(10.dp, 10.dp, 10.dp, 5.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChoosePhotos(
    viewModel : NovPostViewModel,
    navigator : DestinationsNavigator,
    context : Context
) {
    val context = LocalContext.current
    val myImage: Bitmap = BitmapFactory.decodeResource(Resources.getSystem(), android.R.mipmap.sym_def_app_icon)
    val result = remember {
        mutableStateOf<Bitmap>(myImage)
    }

    val maxChars = 100;
    TextField(
        value = viewModel.description.value,
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        onValueChange = {
            if(it.length <= maxChars){
                viewModel.description.value = it
            }
            else{
                Toast.makeText(
                    context,
                    "Character limit reached!",
                    Toast.LENGTH_LONG
                ).show()
            } },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        label = {
            Text("Enter description") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        maxLines = 5
    )

    val chooseImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){
        if(it != null)
        {
            if(Build.VERSION.SDK_INT < 29){
                result.value = MediaStore.Images.Media.getBitmap(context.contentResolver,it)

                viewModel.parsePhoto(result.value)
            }
            else {
                val source = ImageDecoder.createSource(context.contentResolver,it as Uri)
                result.value = ImageDecoder.decodeBitmap(source)
                viewModel.parsePhoto(result.value)
            }
        }
    }

    Spacer(Modifier.height(20.dp));

    Row(
        Modifier.clickable{
            chooseImage.launch("image/*");
        }
    ){
        Text("Add picture", fontSize = 20.sp)
        Icon(
            imageVector = Icons.Default.AddAPhoto,
            contentDescription = "Add photo",
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(25.dp),
        )
    }

    Spacer(Modifier.height(15.dp))

    //ovde da bude slider izabranih slika, ako ih ima
//    LazyRow(
//        horizontalArrangement = Arrangement.spacedBy(10.dp)
//    ) {
//        items(viewModel.givePhotos()) { item ->
//            slika(navigator, photo = item, viewModel)
//        }
//    }
    if(viewModel.givePhotos().isNotEmpty())
        ImageSlider(photos = viewModel.givePhotos(), viewModel)
}

@Composable
fun SetLocation(
    viewModel : NovPostViewModel,
    navigator : DestinationsNavigator,
    context : Context,
    changeMarkerLatLng: (LatLng?) -> Unit = {},
    changeMarkerPOI : (PointOfInterest?) -> Unit = {},
    changeLocationName : (String) -> Unit = {}
) {

    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }
//    var markerLatLng = remember {
//        mutableStateOf<LatLng?>(null)
//    }
//    var markerPOI = remember {
//        mutableStateOf<PointOfInterest?>(null)
//    }

    val camPosState = rememberCameraPositionState {
        position = CameraPosition( LatLng(50.0,25.0),5F,0F,0F)
    }
    val builder = LatLngBounds.Builder()
//    var imeLokacije = remember {
//        mutableStateOf("")
//    }
    val localDensity = LocalDensity.current
    var mapWidth by remember {
        mutableStateOf(44)
    }

    var mapHeight by remember {
        mutableStateOf(20)
    }

    Row(
        Modifier.padding(top = 10.dp)
    ){
        Icon(
            Icons.Default.LocationOn,
            contentDescription = "",
            tint = Color.DarkGray)
        Text(
            "Choose location from map"
        )
    }

    Spacer(Modifier.height(10.dp));

    Row(
        Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {

        TextField(
            value = viewModel.imeLokacije.value, onValueChange = {
                if (viewModel.markerLatLng.value != null) {
                    viewModel.imeLokacije.value = it
                    changeLocationName(it)
                }
                else{
                    Toast.makeText(
                        context,
                        "You need to choose location from map first",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            modifier = Modifier
                .fillMaxWidth(),
            label = { Text("Enter location name") },
            singleLine = true,
        )
    }

    //Spacer(Modifier.height(5.dp));

    Row(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(top = 20.dp, bottom = 15.dp)
    ){
        //mapica


        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .onGloballyPositioned { coords ->
                    mapWidth = with(localDensity) { coords.size.width }
                    mapHeight = with(localDensity) { coords.size.height }
                },
            uiSettings = uiSettings,
            onMapLoaded = {

            },
            cameraPositionState = camPosState,
            onMapClick = {
                viewModel.markerLatLng.value = it
                changeMarkerLatLng(it)
                if(viewModel.markerPOI.value != null){
                    viewModel.imeLokacije.value = ""
                    changeLocationName("")
                    viewModel.markerPOI.value = null
                    changeMarkerPOI(null)
                }
                else{
                    viewModel.imeLokacije.value = viewModel.imeLokacije.value
                    changeLocationName(viewModel.imeLokacije.value)
                }

            },
            onPOIClick = {
                viewModel.markerLatLng.value = null
                changeMarkerLatLng(null)
                viewModel.markerPOI.value = it
                changeMarkerPOI(it)
                viewModel.imeLokacije.value = it.name
                changeLocationName(it.name)
            },
        ) {
            val markerState : MarkerState = rememberMarkerState()
            if (viewModel.markerLatLng.value != null) {
                markerState.position = viewModel.markerLatLng.value!!
                Marker(state = markerState, title = viewModel.imeLokacije.value)
            }
            if (viewModel.markerPOI.value != null) {
                markerState.position = viewModel.markerPOI.value!!.latLng;
                Marker(state = markerState, title =viewModel. markerPOI.value!!.name)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalPagerApi
@Composable
fun ImageSlider(
    photos: List<SlikaState>,
    viewModel : NovPostViewModel
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
                .padding(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){

            Column(
                Modifier
                    .fillMaxWidth()
                    .height(30.dp)
            ){
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ){
                    IconButton(
                        onClick = {
                            Log.d("DELETE", "Deleting picture ${pagerState.currentPage}")
                            viewModel.deletePhoto(photos[pagerState.currentPage].slika)
                                  },
                        modifier = Modifier
                            .size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
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

//                        val decoder = Base64.getDecoder()
//                        val photoBytes = decoder.decode(photo.photo.data)
//                        if(photoBytes.size>1) {
//                            val mapa: Bitmap =
//                                BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.size)
//                            print(mapa.byteCount)
//                            if (mapa != null) {
//                                Image(
//                                    bitmap = mapa.asImageBitmap(),
//                                    contentDescription = "",
//                                    contentScale = ContentScale.Crop
//                                )
//                            }
//                        }

                        val photo = photos[page]
                        Image(
                            bitmap = photo.slika.asImageBitmap(),
                            contentDescription = "",
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 5.dp),
                activeColor = MyColorTopBar,
                inactiveColor = Color.LightGray
            )
        }
    }
}