package app.netlify.dev4rju9.kshatriyakulavatans.di

import android.content.Context
import androidx.room.Room
import app.netlify.dev4rju9.kshatriyakulavatans.data.remote.retrofit.CloudFlare
import app.netlify.dev4rju9.kshatriyakulavatans.data.repository.Repository
import app.netlify.dev4rju9.kshatriyakulavatans.data.room.AppDatabase
import app.netlify.dev4rju9.kshatriyakulavatans.data.room.SourceDao
import app.netlify.dev4rju9.kshatriyakulavatans.data.room.UserDao
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.addsourcescreen.AddSourceViewModel
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.loginscreen.LoginViewModel
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.authenticationscreens.registrationscreen.RegisterViewModel
import app.netlify.dev4rju9.kshatriyakulavatans.ui.screens.mainscreen.MainScreenViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseFirestore() = FirebaseFirestore
        .getInstance()
        .collection("kshatriyakulavatans")
        .document("kshatriyakulavatans")

    @Provides
    @Singleton
    fun providesFirebaseDatabase() = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun providesRepository(
        @ApplicationContext
        context: Context,
        auth: FirebaseAuth,
        firestore: DocumentReference,
        storage: FirebaseStorage,
        sourceDao: SourceDao,
        userDao: UserDao,
        api: CloudFlare
    ) = Repository(context, auth, firestore, storage, sourceDao, userDao, api)

    @Provides
    @Singleton
    fun providesLoginViewModel(
        repo: Repository
    ) = LoginViewModel(repo)

    @Provides
    @Singleton
    fun providesRegisterViewModel(
        repo: Repository
    ) = RegisterViewModel(repo)

    @Provides
    @Singleton
    fun providesMainScreenViewModel(
        repo: Repository
    ) = MainScreenViewModel(repo)

    @Provides
    @Singleton
    fun provideAddSourceViewModel(
        repo: Repository
    ) = AddSourceViewModel(repo)

    @Provides
    @Singleton
    fun providesDatabase (
        @ApplicationContext
        context: Context
    ) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "app_database"
    ).fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun providesSourceDao (
        db: AppDatabase
    ) = db.sourceDao()

    @Provides
    @Singleton
    fun providesUserDao (
        db: AppDatabase
    ) = db.userDao()

}