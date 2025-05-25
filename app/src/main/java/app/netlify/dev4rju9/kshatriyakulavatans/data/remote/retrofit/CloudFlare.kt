package app.netlify.dev4rju9.kshatriyakulavatans.data.remote.retrofit

import app.netlify.dev4rju9.kshatriyakulavatans.data.models.KingResponse
import app.netlify.dev4rju9.kshatriyakulavatans.data.models.VersionResponse
import retrofit2.http.GET

interface CloudFlare {

    @GET("/kshatriyakulavatans/update")
    suspend fun getVersionInfo(): VersionResponse

    @GET("/kshatriyakulavatans/fetch/kings")
    suspend fun getKings(): KingResponse

}