package ug.ac.ndejje.myapp.util

import android.content.Context
import androidx.activity.ComponentActivity
import com.plaid.link.Plaid
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ug.ac.ndejje.myapp.resources.AppDatabase

class PlaidManager(private val context: Context, private val database: AppDatabase) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(Config.BASE_URL)
        .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(FinTrackApiService::class.java)

    suspend fun startPlaidLink(activity: ComponentActivity, userId: Int) {
        try {
            val response = apiService.createPlaidLinkToken(mapOf("userId" to userId))
            val linkToken = response.link_token

            val configuration = LinkTokenConfiguration.Builder()
                .token(linkToken)
                .build()

            // Open Plaid Link UI using the handler
            activity.runOnUiThread {
                Plaid.create(activity.application, configuration).open(activity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
