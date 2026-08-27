package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.CanteenRepository
import com.example.ui.screens.admin.AdminMainContainer
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.student.StudentMainContainer
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AdminViewModel
import com.example.ui.viewmodel.AuthState
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.StudentViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmartCanteenApp()
                }
            }
        }
    }
}

@Composable
fun SmartCanteenApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repository = remember {
        val database = AppDatabase.getInstance(context)
        CanteenRepository(database.canteenDao())
    }

    val authViewModel: AuthViewModel = remember { AuthViewModel(repository) }
    val studentViewModel: StudentViewModel = remember { StudentViewModel(repository) }
    val adminViewModel: AdminViewModel = remember { AdminViewModel(repository) }

    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    when (val state = authState) {
        is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AmberPrimary)
            }
        }
        is AuthState.Unauthenticated -> {
            AuthScreen(authViewModel = authViewModel)
        }
        is AuthState.Authenticated -> {
            if (state.user.role.equals("ADMIN", ignoreCase = true)) {
                AdminMainContainer(
                    currentUser = state.user,
                    authViewModel = authViewModel,
                    adminViewModel = adminViewModel
                )
            } else {
                StudentMainContainer(
                    currentUser = state.user,
                    authViewModel = authViewModel,
                    studentViewModel = studentViewModel
                )
            }
        }
    }
}

