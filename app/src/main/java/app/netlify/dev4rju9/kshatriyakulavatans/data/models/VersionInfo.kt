package app.netlify.dev4rju9.kshatriyakulavatans.data.models

data class VersionInfo (
    val currentCode: Int,
    val currentName: String,
    val latestCode: Int,
    val latestName: String,
    val releaseNotes: String,
    val apkUrl: String
)