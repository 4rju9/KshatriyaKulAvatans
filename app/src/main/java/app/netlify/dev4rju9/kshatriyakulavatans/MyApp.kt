package app.netlify.dev4rju9.kshatriyakulavatans

import android.app.Application
import android.os.Environment
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp
import java.io.File

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)

        val file = File(applicationContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "Kshatriya Kul Avatans.apk")
        if (file.exists()) file.delete()

    }
}