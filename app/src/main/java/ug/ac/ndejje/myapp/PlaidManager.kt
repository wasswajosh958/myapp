package ug.ac.ndejje.myapp

import android.content.Context
import androidx.activity.ComponentActivity
import com.plaid.link.OpenPlaidLink
import com.plaid.link.configuration.LinkTokenConfiguration
import com.plaid.link.result.LinkSuccess

class PlaidManager(private val context: Context) {

    fun openLink(activity: ComponentActivity, linkToken: String, onSuccess: (String) -> Unit) {
        val configuration = LinkTokenConfiguration.Builder()
            .token(linkToken)
            .build()

        OpenPlaidLink.open(activity, configuration) { result ->
            if (result.linkSuccess != null) {
                onSuccess(result.linkSuccess!!.publicToken)
            }
        }
    }
}
