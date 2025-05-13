package com.example.dolcetto

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    val firebaseAuth: FirebaseAuth
        get() = auth

    suspend fun login(email: String, password: String): Resource<UserData> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            fetchUserData(result.user?.uid ?: "")
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Login failed")
        }
    }

    suspend fun register(email: String, password: String): Resource<UserData> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            createNewUser(result.user!!.uid, email)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Registration failed")
        }
    }

    suspend fun fetchUserData(uid: String): Resource<UserData> {
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            if (snapshot.exists()) {
                val user = snapshot.toObject(UserData::class.java)!!
                Resource.Success(user)
            } else {
                createNewUser(uid, auth.currentUser?.email ?: "")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error fetching user")
        }
    }

    private suspend fun createNewUser(uid: String, email: String): Resource<UserData> {
        return try {
            val user = UserData(
                userId = uid,
                email = email,
                role = "user"
            )
            db.collection("users").document(uid).set(user).await()
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error("Error creating user: ${e.message}")
        }
    }

    fun logout() {
        auth.signOut()
    }
}


sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}