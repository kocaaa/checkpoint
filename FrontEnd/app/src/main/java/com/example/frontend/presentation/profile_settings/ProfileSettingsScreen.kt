package com.example.frontend.presentation.profile_settings

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoorSliding
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.frontend.presentation.destinations.LoginScreenDestination
import com.example.frontend.presentation.destinations.MainLocationScreenDestination
import com.example.frontend.presentation.destinations.ProfileScreenDestination
import com.example.frontend.presentation.destinations.UserListScreenDestination
import com.example.frontend.presentation.profile.ProfileScreen
import com.example.frontend.presentation.profile_settings.components.ChangeProfilePictureState
import com.example.frontend.presentation.InputType
import com.example.frontend.presentation.TextInput
import com.example.frontend.presentation.profile_settings.components.ProfilePictureState
import com.example.frontend.presentation.profile_settings.components.ProfileSettingsUserState
import com.example.frontend.ui.theme.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*


@RequiresApi(Build.VERSION_CODES.O)
@Destination
@Composable
fun ProfileSettingsScreen(
    navigator : DestinationsNavigator,
    viewModel: ProfileSettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state = viewModel.state.value
    val stateEmailChange = viewModel.stateEmailChange.value
    val stateGetMyProfilePicture = viewModel.stateGetMyProfilePicture.value
    val stateChangeProfilePicture = viewModel.stateChangeProfilePicture.value
    val statePasswordChange = viewModel.statePasswordChange.value

    if(state.error.contains("403") || stateEmailChange.error.contains("403") || stateGetMyProfilePicture.error.contains("403")
        || stateChangeProfilePicture.error.contains("403") || statePasswordChange.error.contains("403")){
        navigator.navigate(LoginScreenDestination){
            popUpTo(MainLocationScreenDestination.route){
                inclusive = true;
            }
        }
    }

    var emailInput = remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current
    var emailFocusRequester = FocusRequester()
    var oldPasswordFocusRequester = FocusRequester()
    var newPassword1FocusRequester = FocusRequester()
    var newPassword2FocusRequester = FocusRequester()

    val myImage: Bitmap = BitmapFactory.decodeResource(Resources.getSystem(), android.R.mipmap.sym_def_app_icon)
    val result = remember {
        mutableStateOf<Bitmap>(myImage)
    }

    val choseImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){
        if(it != null)
        {
            if(Build.VERSION.SDK_INT < 29){
                result.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
                //viewModel.imgbitmap = result.value
                viewModel.parsePhoto(result.value)
                viewModel.changeProfilePicture(navigator)
            }
            else {
                val source = ImageDecoder.createSource(context.contentResolver, it as Uri)
                result.value = ImageDecoder.decodeBitmap(source)
                //viewModel.imgbitmap = result.value
                viewModel.parsePhoto(result.value)
                viewModel.changeProfilePicture(navigator)
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 28.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Row(
                horizontalArrangement = Arrangement.Start
            ){
                IconButton(onClick = {
                    navigator.popBackStack()
                }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "",
                        tint = MyColorTopBar
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.End
            ){
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

        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        if(state.isLoading || stateGetMyProfilePicture.isLoading){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        else if(state.error != ""){
            Text("An error occured while loading user info!");
        }
        else{
            ProfilePicture(viewModel, stateGetMyProfilePicture, choseImage, result)
            UsernameAndEmail(navigator, state, viewModel, focusManager)
            Passwords(viewModel, oldPasswordFocusRequester, newPassword1FocusRequester, newPassword2FocusRequester, focusManager)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePicture(
    viewModel: ProfileSettingsViewModel,
    stateGetMyProfilePicture: ProfilePictureState,
    choseImage: ManagedActivityResultLauncher<String, Uri?>,
    result: MutableState<Bitmap>,
    ) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 50.dp, end = 20.dp, top = 65.dp, bottom = 0.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        var picture = stateGetMyProfilePicture.picture
        val decoder = Base64.getDecoder()
        val photoInBytes = decoder.decode(picture)

        if(photoInBytes.size > 1) {
            val mapa: Bitmap = BitmapFactory.decodeByteArray(photoInBytes,0, photoInBytes.size)
            if(mapa != null) {
                if (!viewModel.changePictureEnabled)
                    result.value = mapa;
                //println("*************usao")
                    Image(
                    //bitmap = if(viewModel.changePictureEnabled) result.value.asImageBitmap() else mapa.asImageBitmap(),
                    //bitmap = if(viewModel.flagPictureFirstShow) mapa.asImageBitmap() else result.value.asImageBitmap(),
                        bitmap = result.value.asImageBitmap(),
                    modifier = Modifier
                        .height(120.dp)
                        .width(120.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterVertically),
                    contentDescription = "Profile image",
                    contentScale = ContentScale.Crop
                )

                Column(

                ) {
                    IconButton(onClick = {
                        choseImage.launch("image/*")
                    },
                        modifier = Modifier
                            .border(0.dp, MyColorTopBar, RectangleShape)
                            .size(30.dp)
                    ) {
                        Icon(
                            Icons.Filled.ModeEdit,
                            contentDescription = "",
                            tint = MyColorTopBar,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UsernameAndEmail(
    navigator: DestinationsNavigator,
    state : ProfileSettingsUserState,
    viewModel: ProfileSettingsViewModel,
    focusManager: FocusManager
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 30.dp)
    ) {

        Text(
            text = "${state.user?.username}",
            fontFamily = FontFamily.Monospace,
            color = Color.DarkGray,
            fontSize = 25.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        TextInput(
            inputType = InputType.Mail,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }),
            valuePar = viewModel.currentEmail,
            onChange = {viewModel.currentEmail = it}
        );
        if(viewModel.stateEmailChange.value.error!=""){
            Text(viewModel.stateEmailChange.value.error)
        }
        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            viewModel.changeEmail(navigator)
        },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MyColorTopBar),
            modifier = Modifier
                .height(38.dp)
                .width(120.dp)
        ) {
            Text(
                text = "Update email",
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun Passwords(
    viewModel: ProfileSettingsViewModel,
    oldPasswordFocusRequester: FocusRequester,
    newPassword1FocusRequester: FocusRequester,
    newPassword2FocusRequester: FocusRequester,
    focusManager: FocusManager
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextInput(
            inputType = InputType.OldPassword,
            focusRequester = oldPasswordFocusRequester,
            keyboardActions = KeyboardActions(
                onNext = {
                    newPassword1FocusRequester.requestFocus()
                }),
            valuePar = viewModel.currentOldPassInputValue,
            onChange = {viewModel.currentOldPassInputValue = it}
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextInput(
            inputType = InputType.NewPassword,
            focusRequester = newPassword1FocusRequester,
            keyboardActions = KeyboardActions(
                onDone = {
                    newPassword2FocusRequester.requestFocus()
                }),
            valuePar = viewModel.currentNewPass1InputValue,
            onChange = {viewModel.currentNewPass1InputValue = it}
        )

        Spacer(modifier = Modifier.height(10.dp))

        TextInput(
            inputType = InputType.NewPasswordConfirm,
            focusRequester = newPassword2FocusRequester,
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                }),
            valuePar = viewModel.currentNewPass2InputValue,
            onChange = {viewModel.currentNewPass2InputValue = it}
        )

        if(viewModel.statePasswordChange.value.error != ""){
            Text(viewModel.statePasswordChange.value.error)
        }
        Spacer(modifier = Modifier.height(15.dp))

        Button(onClick = {
            viewModel.changePassword()
        },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = MyColorTopBar),
            modifier = Modifier
                .height(40.dp)
                .width(135.dp)
        ) {
            Text(
                text = "Update password",
                fontSize = 12.sp,
                color = Color.White
            )
        }
    }
}

