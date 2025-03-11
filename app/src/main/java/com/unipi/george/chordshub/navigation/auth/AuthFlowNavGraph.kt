package com.unipi.george.chordshub.navigation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unipi.george.chordshub.navigation.Screen
import com.unipi.george.chordshub.screens.auth.LoginScreen
import com.unipi.george.chordshub.screens.auth.SignUpScreen
import com.unipi.george.chordshub.repository.AuthRepository
import com.unipi.george.chordshub.screens.auth.ForgotPasswordScreen

@Composable
fun AuthFlowNavGraph(
    navController: NavHostController,
    isUserLoggedInState: MutableState<Boolean>
) {
    val fullNameState = AuthRepository.fullNameState
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController) {
                isUserLoggedInState.value = true
                fullNameState.value = AuthRepository.getFullName()
            }
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(authRepository = AuthRepository, onBack = { navController.popBackStack() })
        }
    }
}
