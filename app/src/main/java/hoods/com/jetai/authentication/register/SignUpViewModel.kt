package hoods.com.jetai.authentication.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import hoods.com.jetai.data.repository.AuthRepository
import hoods.com.jetai.utils.Response
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authRepository: AuthRepository= hoods.com.jetai.Graph.authRepository
):ViewModel() {

    var signUpState by mutableStateOf(SignUpState())
        private set

    fun signUpEvent(signUpEvents: SignUpEvents){
        when(signUpEvents){
            is SignUpEvents.onEmailChange->{
                signUpState=signUpState.copy(email = signUpEvents.email)
            }
            is SignUpEvents.onFirstNameChange->{
                signUpState=signUpState.copy(firstName = signUpEvents.firstName)
            }
            is SignUpEvents.onLastNameChange->{
                signUpState=signUpState.copy(lastName = signUpEvents.lastName)
            }
            is SignUpEvents.onPasswordChange->{
                signUpState=signUpState.copy(password = signUpEvents.pass)
            }
            is SignUpEvents.onConfirmPasswordChange->{
                signUpState=signUpState.copy(confirmPassword = signUpEvents.confirmPassword)
            }
            is SignUpEvents.onAgreeTerms->{ signUpState=signUpState.copy(agreeTerms = signUpEvents.agreeTerms)}
            is SignUpEvents.SignUp->{
                //create user
                createUser()
            }
            is SignUpEvents.OnIsEmailVerificationChange->{
                signUpState=signUpState.copy(isVerificationEmailSent = false)
            }
        }

    }

    private fun validateSignUpForm()= signUpState.run {
        firstName.isNotEmpty() && lastName.isNotEmpty()
                && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && agreeTerms
    }

    private fun createUser()=viewModelScope.launch {
        try {

            val isNotSamePassword:Boolean= signUpState.password != signUpState.confirmPassword
            if(!validateSignUpForm())throw IllegalArgumentException("Fields can not be empty ")
            if(isNotSamePassword)throw IllegalArgumentException("Passwords don't match")
            signUpState=signUpState.copy(
                isLoading = true,
                loginErrorMsg = null
            )
            authRepository.createUser(signUpState.email,signUpState.password).collectLatest{

                signUpState = when (it) {
                    is Response.Loading -> {
                        signUpState.copy(isLoading = true)
                    }

                    is Response.Success -> {
                        sendVerificationEmail()
                        signUpState.copy(isSuccessLogin = true, isLoading = false)
                    }

                    is Response.Error -> {
                        signUpState = signUpState.copy(isSuccessLogin = false, isLoading = false)
                        throw IllegalArgumentException(it.throwable)
                    }

                }
            }


        } catch (e:Exception){
            signUpState=signUpState.copy(loginErrorMsg = e.localizedMessage)

        } finally {
            signUpState=signUpState.copy(isLoading = false)
        }




    }

    fun sendVerificationEmail() = authRepository.sendVerificationEmail(
        onSuccess = {signUpState=signUpState.copy(isVerificationEmailSent = true)},
        onError = { throw it ?: Throwable("Unknown Error") }
    )

}

data class SignUpState(
    var firstName:String="",
    var lastName:String="",
    var email:String="",
    var password:String="",
    var confirmPassword:String="",
    var agreeTerms:Boolean=false,
    var isLoading:Boolean=false,
    var isSuccessLogin:Boolean=false,
    var isVerificationEmailSent:Boolean=false,
    var loginErrorMsg:String?=null
)

sealed class SignUpEvents{
    data class onEmailChange(val email: String):SignUpEvents()
    data class onFirstNameChange(val firstName: String):SignUpEvents()
    data class onLastNameChange(val lastName: String):SignUpEvents()
    data class onPasswordChange(val pass: String):SignUpEvents()
    data class onConfirmPasswordChange(val confirmPassword: String):SignUpEvents()

    data class onAgreeTerms(val agreeTerms: Boolean):SignUpEvents()

    data object SignUp : SignUpEvents()
    data object OnIsEmailVerificationChange : SignUpEvents()
}