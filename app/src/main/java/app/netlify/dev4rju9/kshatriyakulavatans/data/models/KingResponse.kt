package app.netlify.dev4rju9.kshatriyakulavatans.data.models

import com.google.gson.annotations.SerializedName

data class KingResponse(
    @SerializedName("status_code") val statusCode: Int,
    val body: List<King>
)

data class King(
    val name: String,
    val image: String,
    @SerializedName("main_content") val mainContent: String,
    val sections: List<Section>
)

data class Section(
    val title: String,
    val text: String
)