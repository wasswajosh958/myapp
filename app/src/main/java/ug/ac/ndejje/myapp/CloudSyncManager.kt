package ug.ac.ndejje.myapp

import android.content.Context
import android.net.Uri
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.InputStreamContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.util.*

class CloudSyncManager(private val context: Context) {

    private fun getDriveService(account: GoogleSignInAccount): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, Collections.singleton(DriveScopes.DRIVE_FILE)
        )
        credential.selectedAccount = account.account
        return Drive.Builder(
            com.google.api.client.extensions.android.http.AndroidHttp.newCompatibleTransport(),
            com.google.api.client.json.gson.GsonFactory.getDefaultInstance(),
            credential
        )
            .setApplicationName("FinTrack")
            .build()
    }

    suspend fun uploadBackup(account: GoogleSignInAccount, fileUri: Uri) = withContext(Dispatchers.IO) {
        val service = getDriveService(account)
        val fileMetadata = File().apply {
            name = "fintrack_backup_${System.currentTimeMillis()}.json"
            parents = listOf("root")
        }
        val mediaContent = InputStreamContent(
            "application/json",
            context.contentResolver.openInputStream(fileUri)
        )
        service.files().create(fileMetadata, mediaContent).execute()
    }

    suspend fun downloadLatestBackup(account: GoogleSignInAccount, targetFile: java.io.File) = withContext(Dispatchers.IO) {
        val service = getDriveService(account)
        val result = service.files().list()
            .setQ("name contains 'fintrack_backup'")
            .setOrderBy("createdTime desc")
            .setPageSize(1)
            .execute()

        val fileId = result.files?.firstOrNull()?.id ?: return@withContext false
        val outputStream = FileOutputStream(targetFile)
        service.files().get(fileId).executeMediaAndDownloadTo(outputStream)
        outputStream.close()
        true
    }
}
