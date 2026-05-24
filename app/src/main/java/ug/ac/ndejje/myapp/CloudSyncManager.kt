package ug.ac.ndejje.myapp

import android.content.Context
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CloudSyncManager(private val context: Context) {

    suspend fun uploadBackup(account: GoogleSignInAccount, fileUri: Uri) = withContext(Dispatchers.IO) {
        // TODO: Implement using Drive API v3
    }

    suspend fun downloadLatestBackup(account: GoogleSignInAccount, targetFile: File) = withContext(Dispatchers.IO) {
        // TODO: Implement using Drive API v3
        false
    }
}
