package app.netlify.dev4rju9.kshatriyakulavatans.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import app.netlify.dev4rju9.kshatriyakulavatans.data.models.User
import app.netlify.dev4rju9.kshatriyakulavatans.data.models.VersionInfo
import app.netlify.dev4rju9.kshatriyakulavatans.data.remote.retrofit.CloudFlare
import app.netlify.dev4rju9.kshatriyakulavatans.data.room.SourceDao
import app.netlify.dev4rju9.kshatriyakulavatans.data.room.UserDao
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.addsourcescreen.AddSourceUiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class Repository (
    context: Context,
    val auth: FirebaseAuth,
    val firestore: DocumentReference,
    val storage: FirebaseStorage,
    val db: SourceDao,
    val userDao: UserDao,
    val api: CloudFlare
) {

    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun onSignUp(
        name: String,
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit = {},
        onError: (message: String) -> Unit = {}
    ) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    user?.sendEmailVerification()?.addOnCompleteListener { emailTask ->
                        if (emailTask.isSuccessful) {
                            val uid = auth.currentUser?.uid
                            if (uid != null) {
                                saveUserInFirestore(
                                    name = name,
                                    username = username,
                                    email = email,
                                    password = password,
                                    uid = uid,
                                    onSuccess = {
                                        auth.signOut()
                                        onSuccess()
                                    },
                                    onFailure = { message ->
                                        onError(message)
                                    }
                                )
                            } else {
                                onError("User ID is null.")
                            }
                        } else {
                            onError(emailTask.exception?.localizedMessage ?: "Failed to send verification email.")
                        }
                    }
                } else {
                    onError(task.exception?.localizedMessage ?: "Sign up failed.")
                }
            }
    }

    private fun saveUserInFirestore(
        name: String,
        username: String,
        email: String,
        password: String,
        uid: String,
        onSuccess: () -> Unit = {},
        onFailure: (message: String) -> Unit = {}
    ) {
        val user = User(
            name = name,
            username = username,
            email = email,
            admin = false
        )

        firestore.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("x4rju9", "User successfully saved in Firestore.")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.e("x4rju9", "Failed to save user: ${exception.message}", exception)
                onFailure(exception.localizedMessage ?: "Failed to save user data.")
            }
    }

    fun loginUser(email: String, password: String, onSuccess: (user: User) -> Unit, onError: (Exception) -> Unit) {
        try {
            auth.signInWithEmailAndPassword(email.trim(), password)
                .addOnCompleteListener {

                    if (it.isSuccessful) {

                        val isVerified = auth.currentUser?.isEmailVerified ?: false

                        if (!isVerified) {
                            auth.currentUser?.sendEmailVerification()
                            onError(Exception("Email not verified. Please check you mail and verify."))
                            auth.signOut()
                            return@addOnCompleteListener
                        } else {
                            getUserFromFirebase(
                                auth.currentUser?.uid!!,
                                onSuccess = { onSuccess(it) },
                                onError = { message ->
                                    onError(Exception(message))
                                }
                            )
                        }
                    } else {
                        onError(it.exception?: Exception("Some unknown error occurred!"))
                    }
                }
        } catch (e: Exception) {
            onError(e)
        }
    }

    fun getUserFromFirebase(userId: String, onSuccess: (user: User) -> Unit, onError: (message: String) -> Unit) {
        firestore.collection("users")
            .document(userId)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val document = it.result
                    if (document.exists()) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            onSuccess(user)
                        } else {
                            onError("User data is null.")
                        }
                    } else {
                        onError("User document does not exist.")
                        }
                } else {
                    onError(it.exception?.localizedMessage ?: "Failed to fetch user data.")
                }
            }
    }

    fun saveSourceInFirebase(
        source: AddSourceUiState,
        onSuccess: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        firestore.collection("sources")
            .document("${source.researchedBy}x${source.timestamp}")
            .set(source)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it) }
    }

    suspend fun uploadImageToFirebase(title: String, uri: Uri): String = withContext(Dispatchers.IO) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("sources/${title.replace(" ", "_")}_${UUID.randomUUID()}.jpg")
        imageRef.putFile(uri).await()
        imageRef.downloadUrl.await().toString()
    }

    fun getAllSources(): Flow<List<AddSourceUiState>> {
        return firestore.collection("sources")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { it.toObject(AddSourceUiState::class.java) }
            }
    }

    fun getSources(): Flow<List<AddSourceUiState>> {
        return db.getAllSources()
    }

    suspend fun refreshSources() {
        getAllSources()
            .collect { fetchedSources ->
                Log.d("x4rju9", "refreshSources: Firebase, size: ${fetchedSources.size}")
                if (fetchedSources.isNotEmpty()) {
                    Log.d("x4rju9", "Deleted All Sources")
                    fetchedSources.forEach { source ->
                        db.insertSource(source)
                        Log.d("x4rju9", "Inserted into Room: ${source.title}")
                    }
                }
            }
    }

    suspend fun fetchVersionInfo(context: Context): Result<VersionInfo> = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = api.getVersionInfo()

            val currentPackage = context.packageManager.getPackageInfo(context.packageName, 0)
            val currentCode = currentPackage.versionCode
            val currentName = currentPackage.versionName?: ""

            val notes = response.releaseNotes.joinToString("\n")

            Result.success(
                VersionInfo(
                    currentCode = currentCode,
                    currentName = currentName,
                    latestCode = response.latestVersionCode,
                    latestName = response.latestVersionName,
                    releaseNotes = notes,
                    apkUrl = response.apkUrl
                )
            )
        } catch (e: Exception) {
            Log.e("VersionRepo", e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    suspend fun deleteSourceAndImages(source: AddSourceUiState) {
        deleteImagesFromStorage(source.imageUris)
        deleteSourceFromFirestore(source)
        db.deleteSource(source)
    }

    private fun deleteImagesFromStorage(imageUris: List<String>) {
        for (uri in imageUris) {

            val storageRef = storage.getReferenceFromUrl(uri)
            storageRef.delete()
                .addOnSuccessListener {
                    Log.d("SourceRepository", "Image deleted from Firebase Storage: $uri")
                }
                .addOnFailureListener { exception ->
                    Log.e("SourceRepository", "Failed to delete image from Firebase Storage: $uri", exception)
                }
        }
    }

    private fun deleteSourceFromFirestore(source: AddSourceUiState) {
        val sourceDocRef = firestore.collection("sources")
            .document("${source.researchedBy}x${source.timestamp}")

        sourceDocRef.delete()
            .addOnSuccessListener {
                Log.d("SourceRepository", "Source deleted from Firestore")
            }
            .addOnFailureListener { exception ->
                Log.e("SourceRepository", "Failed to delete source from Firestore", exception)
            }
    }

    suspend fun signOut() {
        sharedPreferences.edit().clear().apply()
        db.deleteAllSources()
        auth.signOut()
    }

    fun forgotPassword (email: String, onSuccess: () -> Unit) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
    }

    fun getCurrentUserData(): User {
        return User(
            name = sharedPreferences.getString("name", "unknown")?: "unknown",
            username = sharedPreferences.getString("username", "unknown")?: "unknown",
            email = sharedPreferences.getString("email", "unknown")?: "unknown",
            admin = sharedPreferences.getBoolean("isAdmin", false)
        )
    }

    fun updateUserProfile(
        name: String,
        username: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onError(Exception("User not authenticated"))
            return
        }

        val updates = mapOf(
            "name" to name,
            "username" to username
        )

        firestore.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                onSuccess()
                updateUserInRoom(name, username)
            }
            .addOnFailureListener { onError(it) }
    }

    fun updateUserInRoom(name: String, username: String) {
        // Save the user details into shared preferences or room
        try {
            sharedPreferences.edit().apply {
                putString("name", name)
                putString("username", username)
                apply()
            }
        } catch (e: Exception) { e.printStackTrace() }
        Log.d("x4rju9", "saveUserInRoom: ${sharedPreferences.all}")
    }

    fun getUsers (): Flow<List<User>> {
        return userDao.getAllUsers()
    }

    suspend fun fetchAndCacheUsers() {
        val snapshot = firestore.collection("users").get().await()
        val users = snapshot.documents.mapNotNull {
            it.toObject(User::class.java)?.let { user ->
                User(user.name, user.username, user.email, user.admin)
            }
        }
        userDao.clearAll()
        userDao.insertUsers(users)
    }

    suspend fun toggleAdminStatus(email: String, isAdmin: Boolean, onError: (Exception) -> Unit) {
        try {
            val userSnapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (!userSnapshot.isEmpty) {
                val userDocument = userSnapshot.documents[0]
                userDocument.reference.update("admin", isAdmin).await()
                fetchAndCacheUsers()
            } else {
                onError(Exception("User not found with email: $email"))
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

}