package ug.ac.ndejje.myapp.util

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

// Data classes for API Communication
data class LinkTokenResponse(val link_token: String)
data class ExchangeTokenRequest(val public_token: String, val userId: Int)
data class ExchangeTokenResponse(val access_token: String, val item_id: String)
data class SyncDataRequest(val userId: Int, val dataJson: String)
data class SyncDataResponse(val status: String, val message: String)

interface FinTrackApiService {

    @POST("api/plaid/create_link_token")
    suspend fun createPlaidLinkToken(@Body request: Map<String, Int>): LinkTokenResponse

    @POST("api/plaid/exchange_public_token")
    suspend fun exchangePlaidPublicToken(@Body request: ExchangeTokenRequest): ExchangeTokenResponse

    @POST("api/sync/upload")
    suspend fun uploadCloudBackup(@Body request: SyncDataRequest): SyncDataResponse

    @GET("api/sync/download/{userId}")
    suspend fun downloadCloudBackup(@Path("userId") userId: Int): SyncDataRequest
}
