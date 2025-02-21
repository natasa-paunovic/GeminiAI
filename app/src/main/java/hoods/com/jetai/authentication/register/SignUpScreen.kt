package hoods.com.jetai.authentication.register

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import hoods.com.jetai.authentication.components.HeaderText
import hoods.com.jetai.authentication.components.LoadingView
import hoods.com.jetai.authentication.components.LoginTextField

val defaultPadding= 16.dp
val itemSpacing=8.dp

@Composable
fun SignUpScreen(
    onLoginClick:()->Unit,
    onNavigateToLoginScreen:(Boolean)->Unit,
    onBackButtonClick:()->Unit,
    modifier: Modifier,
    viewModel: SignUpViewModel= androidx.lifecycle.viewmodel.compose.viewModel()
){
    val signUpState=viewModel.signUpState
    val context= LocalContext.current

    val focusManager = LocalFocusManager.current

    val focusRequesterFirstName = remember { FocusRequester() }
    val focusRequesterLastName = remember { FocusRequester() }
    val focusRequesterEmail = remember { FocusRequester() }
    val focusRequesterPass = remember { FocusRequester() }
    val focusRequesterConfirmPass = remember { FocusRequester() }

    //LaunchedEffect je funkcija iz Jetpack Compose biblioteke koja omogućava pokretanje korutine unutar Composable funkcije.
    LaunchedEffect(signUpState.isVerificationEmailSent) {
        if(signUpState.isVerificationEmailSent){
            onNavigateToLoginScreen(true)
            viewModel.signUpEvent(SignUpEvents.OnIsEmailVerificationChange)
        }

    }

    BackHandler {
        onBackButtonClick()
    }

    Column(modifier = modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(defaultPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    )

    {
        AnimatedVisibility(signUpState.loginErrorMsg !=null) {
            Text(text=signUpState.loginErrorMsg ?: "unknown error",
                color = MaterialTheme.colorScheme.error)

        }

        HeaderText(text = "Sign Up",
                    modifier= Modifier
                        .padding(vertical = defaultPadding)
                        .align(alignment = Alignment.Start)
                    )
        
        LoginTextField(value = signUpState.firstName,
            onValueChange = {viewModel.signUpEvent(SignUpEvents.onFirstNameChange(it.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            } ))},
            labelText = "First Name",
            onNext = {
                focusRequesterLastName.requestFocus() // Fokus na sledeće polje
            },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterFirstName))
        
        Spacer(modifier = Modifier.height(defaultPadding))

        LoginTextField(value = signUpState.lastName,
            onValueChange = {viewModel.signUpEvent(SignUpEvents.onLastNameChange(it.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            } ))},
            labelText = "Last Name",
            onNext = {
                focusRequesterEmail.requestFocus() // Fokus na sledeće polje
            },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterLastName))

        Spacer(modifier = Modifier.height(defaultPadding))

        LoginTextField(value = signUpState.email,
            onValueChange = {viewModel.signUpEvent(SignUpEvents.onEmailChange(it))},
            labelText = "Email",
            onNext = {
                focusRequesterPass.requestFocus() // Fokus na sledeće polje
            },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterEmail),
            keyboardType = KeyboardType.Email)

        Spacer(modifier = Modifier.height(defaultPadding))

        LoginTextField(value = signUpState.password,
            onValueChange = {viewModel.signUpEvent(SignUpEvents.onPasswordChange(it))},
            labelText = "Password",
            visualTransformation = PasswordVisualTransformation(),
            onNext = {
                focusRequesterConfirmPass.requestFocus() // Fokus na sledeće polje
            },
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterPass),
            keyboardType = KeyboardType.Password)

        Spacer(modifier = Modifier.height(defaultPadding))

        LoginTextField(value = signUpState.confirmPassword,
            onValueChange = {viewModel.signUpEvent(SignUpEvents.onConfirmPasswordChange(it))},
            labelText = "Confirm Password",
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().focusRequester(focusRequesterConfirmPass),
            keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done, // Postavljanje "Done" na tastaturi
            onDone = {
                hideKeyboard(context, focusManager)

                // Zatvaranje tastature
            })

        Spacer(modifier = Modifier.height(defaultPadding))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){

            val policyText:String="Policy"
            val privacyText:String="Privacy"
            val annotatedString= buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)){
                    append("I Agree with")
                }
                append(" ")
                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary

                    )){
                    pushStringAnnotation(tag = privacyText,privacyText)
                    append(privacyText)
                }

                withStyle(SpanStyle(color = Color.White)){
                    append(" and ")
                }


                withStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary

                    )){
                    pushStringAnnotation(tag = policyText,policyText)
                    append(policyText)
                }
            }
            Checkbox(signUpState.agreeTerms,{
                viewModel.signUpEvent(SignUpEvents.onAgreeTerms(it))
            })
            ClickableText(text = annotatedString) {offset->
                annotatedString.getStringAnnotations(offset,offset).forEach{
                    when(it.tag){
                        privacyText->{ Toast.makeText(context,"Privacy Text Clicked",Toast.LENGTH_SHORT).show()}
                        policyText->{Toast.makeText(context,"Policy Text Clicked",Toast.LENGTH_SHORT).show()}
                    }
                }
                
            }

        }

        Spacer(modifier = Modifier.height(defaultPadding + 8.dp))
        Button(onClick = { viewModel.signUpEvent(SignUpEvents.SignUp)}, modifier=Modifier.fillMaxWidth())
        {
            Text(text = "Sign Up")
        }
        Spacer(modifier = Modifier.height(defaultPadding))

        val signInText:String="Sign In "
        val annotatedString= buildAnnotatedString {
            withStyle(SpanStyle(color = Color.White)){
                append("Already have an account?")
            }
            append(" ")
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary

                )){
                pushStringAnnotation(tag = signInText,signInText)
                append(signInText)
            }
            


           
        }
        ClickableText(text = annotatedString) {offset->
            annotatedString.getStringAnnotations(offset,offset).forEach{
                when(it.tag){
                    signInText->{
                        onLoginClick()
                    }
                }
            }

        }



    }
    
    LoadingView(isLoading = signUpState.isLoading)



}

fun hideKeyboard(context: Context, focusManager: FocusManager) {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(null, 0)
    focusManager.clearFocus() // Oslobađa fokus sa polja za unos
}