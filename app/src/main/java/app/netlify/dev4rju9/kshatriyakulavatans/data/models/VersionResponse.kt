package app.netlify.dev4rju9.kshatriyakulavatans.data.models

import com.google.gson.annotations.SerializedName

data class VersionResponse(
    @SerializedName("latestVersionCode") val latestVersionCode: Int,
    @SerializedName("latestVersion") val latestVersionName: String,
    @SerializedName("url") val apkUrl: String,
    @SerializedName("releaseNotes") val releaseNotes: List<String>
)