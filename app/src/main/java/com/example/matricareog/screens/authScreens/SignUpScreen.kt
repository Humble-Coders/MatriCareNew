package com.example.matricareog.screens.authScreens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matricareog.viewmodels.AuthViewModel
import com.example.matricareog.model.AuthResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    val authState by authViewModel.authState.collectAsState()
    val isTermsAccepted by authViewModel.isTermsAccepted.collectAsState()
    val isLoading = authState is AuthResult.Loading

    // Colors
    val primaryPink = Color(0xFFE91E63)
    val lightGray = Color(0xFFF5F5F5)
    val darkGray = Color(0xFF757575)

    // Handle authentication state
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthResult.Success -> {
                onNavigateToHome()
            }
            is AuthResult.Error -> {
                if (state.message != "Not authenticated") {
                    // Error will be displayed in UI
                }
            }
            AuthResult.Loading -> {
                // Loading handled by isLoading state
            }
            AuthResult.Idle -> {
                // Do nothing, waiting for user action
            }
        }
    }

// Clear error state when component is disposed
    DisposableEffect(Unit) {
        onDispose {
            authViewModel.clearAuthState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // Logo/Title with "care" in pink
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Text(
                text = "Matri",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "care",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = primaryPink
            )
        }

        Text(
            text = "Create your account",
            fontSize = 20.sp,
            color = darkGray,
            modifier = Modifier.padding(bottom = 40.dp)
        )

        if (authState is AuthResult.Error && (authState as AuthResult.Error).message != "Not authenticated") {
            ErrorMessage(
                message = (authState as AuthResult.Error).message,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Full Name Field
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholder = {
                Text(
                    text = "Enter your full name",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person icon",
                    tint = darkGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = primaryPink,
                cursorColor = primaryPink
            ),
            singleLine = true,
            enabled = !isLoading
        )

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(
                    text = "Enter your email",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Email icon",
                    tint = darkGray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = primaryPink,
                cursorColor = primaryPink
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = !isLoading
        )

// Password validation states
        val isPasswordError = password.isNotBlank() && password.length < 6
        val isConfirmPasswordError = confirmPassword.isNotBlank() &&
                password.isNotBlank() && password != confirmPassword

// Password Field with validation
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    text = "Create password (min 6 characters)",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock icon",
                    tint = if (isPasswordError) Color(0xFFC62828) else darkGray
                )
            },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = if (isPasswordError) Color(0xFFC62828) else darkGray
                    )
                }
            },
            isError = isPasswordError,
            supportingText = if (isPasswordError) {
                { Text("Password must be at least 6 characters", color = Color(0xFFC62828)) }
            } else null,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = primaryPink,
                cursorColor = primaryPink,
                errorBorderColor = Color(0xFFC62828),
                errorLabelColor = Color(0xFFC62828)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !isLoading
        )

// Confirm Password Field with validation
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = {
                Text(
                    text = "Confirm password",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Lock icon",
                    tint = if (isConfirmPasswordError) Color(0xFFC62828) else darkGray
                )
            },
            trailingIcon = {
                IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                    Icon(
                        imageVector = if (isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isConfirmPasswordVisible) "Hide password" else "Show password",
                        tint = if (isConfirmPasswordError) Color(0xFFC62828) else darkGray
                    )
                }
            },
            isError = isConfirmPasswordError,
            supportingText = if (isConfirmPasswordError) {
                { Text("Passwords do not match", color = Color(0xFFC62828)) }
            } else null,
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.LightGray,
                focusedBorderColor = primaryPink,
                cursorColor = primaryPink,
                errorBorderColor = Color(0xFFC62828),
                errorLabelColor = Color(0xFFC62828)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !isLoading
        )
        // Terms and Privacy Row with clickable text
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isTermsAccepted,
                onCheckedChange = { authViewModel.setTermsAccepted(it) },
                colors = CheckboxDefaults.colors(
                    checkedColor = primaryPink,
                    uncheckedColor = Color.LightGray
                ),
                enabled = !isLoading
            )
            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "I agree to ",
                    fontSize = 14.sp,
                    color = darkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TextButton(
                    onClick = { /* Handle terms */ },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.wrapContentWidth(),
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Terms of Service",
                        fontSize = 14.sp,
                        color = primaryPink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = " and ",
                    fontSize = 14.sp,
                    color = darkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                TextButton(
                    onClick = { /* Handle privacy */ },
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier.wrapContentWidth(),
                    enabled = !isLoading
                ) {
                    Text(
                        text = "Privacy Policy",
                        fontSize = 14.sp,
                        color = primaryPink,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Sign Up Button
        Button(
            onClick = {
                Log.d("SignUpScreen", "$email, $password, $confirmPassword, $fullName")
                authViewModel.signUp(email, password, confirmPassword, fullName)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryPink,
                contentColor = Color.White
            ),
            enabled = isTermsAccepted && !isLoading &&
                    fullName.isNotBlank() &&
                    email.isNotBlank() &&
                    password.isNotBlank() &&
                    confirmPassword.isNotBlank()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Already have account
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Already have an account? ",
                fontSize = 14.sp,
                color = darkGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            TextButton(
                onClick = onNavigateToLogin,
                contentPadding = PaddingValues(0.dp),
                enabled = !isLoading
            ) {
                Text(
                    text = "Log In",
                    fontSize = 14.sp,
                    color = primaryPink,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bottom indicator
        Box(
            modifier = Modifier
                .width(134.dp)
                .height(5.dp)
                .background(Color.Black, RoundedCornerShape(2.5.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}