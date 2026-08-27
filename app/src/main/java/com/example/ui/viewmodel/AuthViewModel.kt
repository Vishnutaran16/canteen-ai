package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.User
import com.example.data.repository.CanteenRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthState {
    data object Loading : AuthState
    data class Authenticated(val user: User) : AuthState
    data object Unauthenticated : AuthState
}

class AuthViewModel(private val repository: CanteenRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Load default student account on launch for instant testability
        loadDefaultStudent()
    }

    fun loadDefaultStudent() {
        viewModelScope.launch {
            val student = repository.getUserByStudentId("CS2026042")
                ?: repository.getFirstUserByRole("STUDENT")
            if (student != null) {
                _authState.value = AuthState.Authenticated(student)
            } else {
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun switchToAdmin() {
        viewModelScope.launch {
            val admin = repository.getUserByStudentId("ADMIN01")
                ?: repository.getFirstUserByRole("ADMIN")
                ?: User(
                    studentId = "ADMIN01",
                    name = "Chef Ramesh (Admin)",
                    email = "canteen.admin@college.edu",
                    phone = "+91 98765 00001",
                    role = "ADMIN",
                    walletBalance = 5000.0,
                    department = "Central Canteen Operations",
                    profileAvatar = "👨‍🍳"
                )
            _authState.value = AuthState.Authenticated(admin)
        }
    }

    fun switchToStudent() {
        loadDefaultStudent()
    }

    fun login(studentId: String, role: String = "STUDENT", onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val user = repository.getUserByStudentId(studentId.trim())
            if (user != null) {
                _authState.value = AuthState.Authenticated(user)
                onResult(true, "Welcome back, ${user.name}!")
            } else {
                onResult(false, "User not found. Try Demo Student or Register.")
            }
        }
    }

    fun registerStudent(
        name: String,
        studentId: String,
        email: String,
        phone: String,
        department: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (name.isBlank() || studentId.isBlank()) {
                onResult(false, "Please provide Name and Student ID")
                return@launch
            }
            val newUser = User(
                studentId = studentId.trim().uppercase(),
                name = name.trim(),
                email = email.trim().ifBlank { "${studentId.lowercase()}@college.edu" },
                phone = phone.trim().ifBlank { "+91 98765 43210" },
                role = "STUDENT",
                walletBalance = 250.0, // Welcome signup bonus!
                department = department.ifBlank { "Engineering & Tech" },
                profileAvatar = "👨‍🎓"
            )
            repository.registerUser(newUser)
            _authState.value = AuthState.Authenticated(newUser)
            onResult(true, "Registration successful! Welcome bonus of ₹250 added to wallet.")
        }
    }

    fun logout() {
        _authState.value = AuthState.Unauthenticated
    }
}
