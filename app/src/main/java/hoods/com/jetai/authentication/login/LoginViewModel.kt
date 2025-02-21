package hoods.com.jetai.authentication.login

import android.content.Intent
import android.content.IntentSender
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import hoods.com.jetai.data.repository.AuthRepository
import hoods.com.jetai.data.repository.GoogleAuthClient
import hoods.com.jetai.utils.ext.collectAndHandle
import hoods.com.jetai.utils.ext.isEmailValid
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = hoods.com.jetai.Graph.authRepository,
    private val googleAuthClient: GoogleAuthClient= hoods.com.jetai.Graph.googleAuthClient
)

    :ViewModel() {

    var loginState by mutableStateOf(LoginState())
        private set

        companion object{
            const val TAG="loginViewModel"
        }

    init {
        viewModelScope.launch {
            repository.currentUser.collectLatest {
                loginState=loginState.copy(currentUser = it)
            }
        }
    }
    fun hasUserVerified():Boolean = repository.hasUser() && repository.getVerifiedUser()

    suspend fun signInWithGoogle(): IntentSender?= googleAuthClient.signIn()

    fun loginEvent(loginEvents: LoginEvents){
        when(loginEvents){
            is LoginEvents.Login->{login()}
            is LoginEvents.OnEmailChange->{
                loginState=loginState.copy(email = loginEvents.email)
            }
            is LoginEvents.OnResendVerifications->{resendVerification()}
            is LoginEvents.OnPasswordChange->{
                loginState=loginState.copy(password = loginEvents.pass)
            }
            is LoginEvents.SignInWithGoogle->{
                viewModelScope.launch {
                    googleAuthClient.signInWithIntent(loginEvents.intent).collectAndHandle (
                        onError={   loginState=loginState.copy(loginErrorMsg = it?.localizedMessage)},
                        onLoading = {
                            loginState=loginState.copy(isLoading = true)
                        }
                    ){
                    hasNotVerifiedThrowError()
                        loginState=loginState.copy(isSuccessLogin = true, isLoading = false)
                    }
                }
            }
            is LoginEvents.LogOut->{
                repository.signOut()
            }
        }

    }

    private fun validateLoginForm()= loginState.email.isNotBlank() && loginState.email.isNotBlank()

    private fun resendVerification(){
        try {
            repository.sendVerificationEmail(onSuccess = {

                loginState=loginState.copy(showResendButton = false)
            }, onError = {
                loginState=loginState.copy(loginErrorMsg = it?.localizedMessage)

            })

        } catch (e:Exception){
            loginState=loginState.copy(isLoading=false, loginErrorMsg = e?.localizedMessage)
            e.printStackTrace()
        }
    }

    private fun login()= viewModelScope.launch {
        loginState=loginState.copy(loginErrorMsg = null)
        try {

            if(!validateLoginForm()) throw IllegalArgumentException("Email or password must not be empty")
            if(!isEmailValid(loginState.email))  throw IllegalArgumentException("Invalid email")
            loginState=loginState.copy(isLoading=true)
            repository.login(loginState.email, loginState.password).collectAndHandle(
                onLoading = { loginState=loginState.copy(isLoading=true)},
                onError={
                    loginState=loginState.copy(isLoading=false, isSuccessLogin = false, loginErrorMsg = it?.localizedMessage)

                }
            ){
                hasNotVerifiedThrowError()
                loginState=loginState.copy(isLoading=false, isSuccessLogin = true, loginErrorMsg =  null)
            }

        }catch (e:Exception){
            loginState=loginState.copy(loginErrorMsg = e?.localizedMessage)
        }
        finally {
            loginState=loginState.copy(isLoading=false)
        }
    }


    fun hasNotVerifiedThrowError(){
        if(!repository.getVerifiedUser()){
            loginState=loginState.copy(showResendButton = true)
            throw IllegalArgumentException("We've sent a verification link to your email. Please check your inbox and click the link to activate your account. ")
        }
    }
}

data class LoginState(
    val email:String="",
    val password:String="",
    val isLoading:Boolean=false,
    val isSuccessLogin:Boolean=false,
    val isValidEmailAddress:Boolean=false,
    val loginErrorMsg:String?=null,
    val currentUser: FirebaseUser?=null,
    val isUserVerified:Boolean?=null,
    val showResendButton:Boolean?=null

)


sealed class LoginEvents{
    data class OnEmailChange(val email: String):LoginEvents()
    data class OnPasswordChange(val pass: String):LoginEvents()

    data object OnResendVerifications :LoginEvents()

    data object Login : LoginEvents()
    data class SignInWithGoogle(val intent:Intent) : LoginEvents()

    data object LogOut : LoginEvents()

}