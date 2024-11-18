package com.ombati.guidecaneapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.ombati.guidecaneapp.data.model.AppUser
import com.ombati.guidecaneapp.data.model.GuideCaneUser

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
            loadUserData()
        }
    }

    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signup(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty")
            return
        }
        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener
                    val appUser = AppUser(appUserId = userId)
                    firestore.collection("AppUser").document(userId)
                        .set(appUser)
                        .addOnSuccessListener {
                            _authState.value = AuthState.Authenticated
                        }
                        .addOnFailureListener { e ->
                            _authState.value = AuthState.Error(e.message ?: "Failed to create user document")
                        }
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Something went wrong")
                }
            }
    }

    fun signout() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }

    fun addGuideCaneUser(smartCaneId: String) {
        val userId = auth.currentUser?.uid ?: return
        val newGuideCaneUser = GuideCaneUser(
            id = smartCaneId,
        )

        firestore.collection("GuideCaneUser").document(smartCaneId)
            .set(newGuideCaneUser)
            .addOnSuccessListener {
                firestore.collection("AppUser").document(userId)
                    .update("guideCaneUsers", FieldValue.arrayUnion(smartCaneId))
                    .addOnSuccessListener {
                    }
                    .addOnFailureListener { e ->
                        _authState.value = AuthState.Error(e.message ?: "Failed to update AppUser with GuideCaneUser")
                    }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Failed to create GuideCaneUser")
            }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        firestore.collection("AppUser").document(userId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val appUser = document.toObject(AppUser::class.java)
                } else {
                    _authState.value = AuthState.Error("No user data found")
                }
            }
            .addOnFailureListener { e ->
                _authState.value = AuthState.Error(e.message ?: "Failed to load user data")
            }
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
