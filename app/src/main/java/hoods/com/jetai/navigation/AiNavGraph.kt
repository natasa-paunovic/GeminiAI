package hoods.com.jetai.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import hoods.com.jetai.authentication.forgot_password.ForgotPasswordScreen
import hoods.com.jetai.authentication.login.LoginScreen
import hoods.com.jetai.authentication.login.LoginViewModel
import hoods.com.jetai.authentication.register.SignUpScreen
import hoods.com.jetai.chatroom.ChatRoomScreen
import hoods.com.jetai.chatroom.ChatRoomViewModel
import hoods.com.jetai.chatroom.EMPTY_TITLE
import hoods.com.jetai.messages.MessageScreen
import hoods.com.jetai.messages.MessageViewModel
import hoods.com.jetai.messages.MessageViewModelFactory
import hoods.com.jetai.photo_reasoning.PhotoReasoningScreen

@Composable
fun AiNavGraph(modifier: Modifier,
               navHostController: NavHostController,
               navigationActions: AiNavigationActions,
               viewModel: LoginViewModel,
               chatRoomViewModel: ChatRoomViewModel,
               startDestination:String) {

    NavHost(navController = navHostController, startDestination = startDestination ) {
        authGraph(modifier,navHostController,navigationActions,viewModel)

        homeGraph(modifier, navHostController, navigationActions,chatRoomViewModel )
        
    }
}

fun NavGraphBuilder.authGraph(modifier: Modifier,
                              navHostController: NavHostController,
                              navigationActions: AiNavigationActions,
                              viewModel: LoginViewModel){

    navigation(startDestination = Route.LoginScreen().routeWithArgs, route = Route.NESTED_AUTH_ROUTE){
        composable(Route.LoginScreen().routeWithArgs,
                    arguments = listOf(navArgument(name = Route.isEmailSentArg){

                    })
        ){
            entry->
            LoginScreen(
                onSignupClick = { navigationActions.navigateToSignUpScreen() },
                onNavigateToHomeScreen = {
                    navigationActions.navigateToHomeGraph()
                                         },
                onForgotPasswordClick = { navigationActions.navigateToForgotPasswordScreen() },
                modifier = modifier,
                isVerificationEmailSent = entry.arguments?.getString(Route.isEmailSentArg).toBoolean()
            )
        }
        composable(Route.SignupScreen().route){
            SignUpScreen(
                onLoginClick = { navigationActions.navigateToLoginScreenWithArgs(false) },
                onNavigateToLoginScreen = {navigationActions.navigateToLoginScreenWithArgs(it)},
                onBackButtonClick = { navigationActions.navigateToLoginScreenWithArgs(false) },
                modifier = modifier
            )
        }

        composable(Route.ForgotPasswordScreen().route){
            ForgotPasswordScreen{navHostController.navigateUp()}

        }
    }
}

fun NavGraphBuilder.homeGraph(modifier: Modifier,
                              navHostController: NavHostController,
                              navigationActions: AiNavigationActions,
                              viewModel: ChatRoomViewModel){

    val messageRoute= "${Route.MessageScreen().route}/{chatId}/{chatTitle}"

    navigation(startDestination = Tabs.Chats.title, route = Route.NESTED_HOME_ROUTE){
        composable(route = Tabs.Chats.title) {
            ChatRoomScreen(
                modifier,
                viewModel
            ) { id, chatTitle ->

                val safeId = id.ifEmpty { "default_chat_id" } // Replace with a valid fallback ID


                navHostController.navigate("${Route.MessageScreen().route}/$safeId/$chatTitle") {
                    launchSingleTop = true
                    popUpTo(Route.MessageScreen().route) { saveState = true }
                    restoreState = true
                }
            }
        }





        composable(route = messageRoute,
            arguments = listOf(navArgument("chatId"){
                type = NavType.StringType

            }, navArgument("chatTitle"){
                type = NavType.StringType; defaultValue = EMPTY_TITLE
            })
        ){backStack->
            val id=backStack.arguments?.getString("chatId") ?:""
            val chatTitle=backStack.arguments?.getString("chatTitle") ?: EMPTY_TITLE
            val viewModel : MessageViewModel = androidx.lifecycle.viewmodel.compose.viewModel (factory = MessageViewModelFactory(id,chatTitle))
            MessageScreen(chatViewModel = viewModel,
                modifier = modifier)
        }

//        composable(Tabs.VisualIq.title) {
//
//        }
        
        composable(
            route = Tabs.VisualIq.title,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() }
        ){
            PhotoReasoningScreen(modifier = modifier)
        }



    }
}