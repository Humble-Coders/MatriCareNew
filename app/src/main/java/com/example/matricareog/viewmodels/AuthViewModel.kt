package com.example.matricareog.viewmodels



import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.matricareog.AuthResult
import com.example.matricareog.User
import com.example.matricareog.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val userRepository = UserRepository()

    private val _authState = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val authState: StateFlow<AuthResult> = _authState.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Form validation states
    private val _isTermsAccepted = MutableStateFlow(false)
    val isTermsAccepted: StateFlow<Boolean> = _isTermsAccepted.asStateFlow()



    private fun checkAuthStatus() {
        if (userRepository.isUserLoggedIn()) {
            viewModelScope.launch {
                _authState.value = userRepository.getCurrentUser()
                when (val result = _authState.value) {
                    is AuthResult.Success -> _currentUser.value = result.user
                    else -> {}
                }
            }
        } else {
            _authState.value = AuthResult.Error("Not authenticated")
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String, fullName: String) {
        val validationError = validateSignUpInputs(email, password, confirmPassword, fullName)
        if (validationError != null) {
            _authState.value = AuthResult.Error(validationError)
            return
        }

        if (!_isTermsAccepted.value) {
            _authState.value = AuthResult.Error("Please accept Terms of Service and Privacy Policy")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            Log.d("UserViewModel", "$email, $password, $fullName")
            val result = userRepository.signUp(email, password, fullName)
            _authState.value = result

            if (result is AuthResult.Success) {
                _currentUser.value = result.user
            }
        }
    }

    fun login(email: String, password: String) {
        val validationError = validateLoginInputs(email, password)
        if (validationError != null) {
            _authState.value = AuthResult.Error(validationError)
            return
        }

        viewModelScope.launch {
            _authState.value = AuthResult.Loading
            val result = userRepository.login(email, password)
            _authState.value = result

            if (result is AuthResult.Success) {
                _currentUser.value = result.user
            }
        }
    }

    fun logout() {
        userRepository.logout()
        _currentUser.value = null
        _authState.value = AuthResult.Error("Not authenticated")
    }

    fun clearAuthState() {
        _authState.value = AuthResult.Idle
    }

    fun setTermsAccepted(accepted: Boolean) {
        _isTermsAccepted.value = accepted
    }

    private fun validateSignUpInputs(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String
    ): String? {
        return when {
            fullName.isBlank() -> "Please enter your full name"
            email.isBlank() -> "Please enter your email"
            !isValidEmail(email) -> "Please enter a valid email address"
            password.isBlank() -> "Please create a password"
            password.length < 6 -> "Password must be at least 6 characters"
            confirmPassword.isBlank() -> "Please confirm your password"
            password != confirmPassword -> "Passwords do not match"
            else -> null
        }
    }

    private fun validateLoginInputs(email: String, password: String): String? {
        return when {
            email.isBlank() -> "Please enter your email"
            !isValidEmail(email) -> "Please enter a valid email address"
            password.isBlank() -> "Please enter your password"
            else -> null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
