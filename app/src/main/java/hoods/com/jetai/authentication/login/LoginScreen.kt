package hoods.com.jetai.authentication.login

import android.app.Activity.RESULT_OK
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import hoods.com.jetai.authentication.components.AlternativeLoginOptions
import hoods.com.jetai.authentication.components.HeaderText
import hoods.com.jetai.authentication.components.LoadingView
import hoods.com.jetai.authentication.components.LoginTextField
import hoods.com.jetai.authentication.register.defaultPadding
import kotlinx.coroutines.launch

@Composable
fun LoginScreen (
    onSignupClick:()->Unit,
    onNavigateToHomeScreen:()->Unit,
    onForgotPasswordClick:()->Unit,
    modifier: Modifier,
    isVerificationEmailSent:Boolean,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
){

    val loginState=viewModel.loginState
    val context= LocalContext.current

    val scope= rememberCoroutineScope()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPass = remember { FocusRequester() }

    val launcher= rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                scope.launch {
                    viewModel.loginEvent(
                        LoginEvents.SignInWithGoogle(result.data ?: return@launch)
                    )
                }

            }
        })

    LaunchedEffect(isVerificationEmailSent) {
        if(isVerificationEmailSent){
            snackbarHostState.showSnackbar("Verification Email sent to ${loginState.currentUser}")

        }

    }

    LaunchedEffect(viewModel.hasUserVerified()) {
        Log.i("LoginScreen", "LoginScreen: ${viewModel.hasUserVerified()}")
        if(viewModel.hasUserVerified()){
           onNavigateToHomeScreen()

        }

    }

    Scaffold(snackbarHost = { SnackbarHost(hostState=snackbarHostState) {


    }

    }){
            innerPadding->
        Box(modifier = modifier
            .padding(defaultPadding)
            .padding(innerPadding),
            contentAlignment = Alignment.Center
        ){

            Column{

                HeaderText(text = "Login",
                    modifier= Modifier
                        .padding(vertical = defaultPadding)
                        .align(alignment = Alignment.Start)
                )

                AnimatedVisibility(loginState.loginErrorMsg !=null) {
                    Text(text=loginState.loginErrorMsg ?: "unknown error",
                        color = MaterialTheme.colorScheme.error)

                }

                AnimatedVisibility(loginState.showResendButton!=null) {
                    TextButton(
                        onClick = {viewModel.loginEvent(LoginEvents.OnResendVerifications)}
                    ){
                        Text(text = "Resend Verification")
                    }

                }

                LoginTextField(value = loginState.email,
                    onValueChange = {viewModel.loginEvent(LoginEvents.OnEmailChange(it))},
                    labelText = "Username",
                    leadingIcon = Icons.Default.Person,
                    onNext = {
                        focusRequesterPass.requestFocus() // Fokus na sledeće polje
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterEmail))

                Spacer(modifier = Modifier.height(defaultPadding))

                LoginTextField(value = loginState.password,
                    onValueChange = {viewModel.loginEvent(LoginEvents.OnPasswordChange(it))},
                    labelText = "Password",
                    leadingIcon = Icons.Default.Lock,
                    onNext = {
                        // Fokus na sledeće polje
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardType = KeyboardType.Password,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequesterPass))

                Spacer(modifier = Modifier.height(defaultPadding))

                TextButton(onClick =  onForgotPasswordClick,
                    modifier= Modifier
                        .align(alignment= Alignment.End)
                )
                {
                    Text(text = "Forgot Password?")

                }

                Spacer(modifier = Modifier.height(defaultPadding))

                Button(onClick =  {viewModel.loginEvent(LoginEvents.Login)},
                    modifier= Modifier.fillMaxWidth()
                )
                {
                    Text(text = "Login")

                }

                Spacer(modifier = Modifier.height(defaultPadding))

                AlternativeLoginOptions(onIconClick = { scope.launch {
                    val googleIntentSender= viewModel.signInWithGoogle()
                    launcher.launch(IntentSenderRequest.Builder(
                        googleIntentSender?: return@launch).build()
                    )
                }
                                                      },
                    onSignUpClick = { onSignupClick() },
                    modifier
                        .fillMaxWidth()
                        .wrapContentSize(align = Alignment.BottomCenter)
                )
            }



        }
        LoadingView(isLoading = loginState.isLoading)
    }



    val focusManager = LocalFocusManager.current


}