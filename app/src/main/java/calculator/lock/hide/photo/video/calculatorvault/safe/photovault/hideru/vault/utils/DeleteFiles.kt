package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import java.net.URLConnection
import java.util.function.Function
import java.util.stream.Collectors

val REQUEST_PERM_DELETE = 1221

private fun getFilePathToMediaID(songPath: String, context: Context): Long {
    var id: Long = 0
    val cr = context.contentResolver
    val uri = MediaStore.Files.getContentUri("external")
    val selection = MediaStore.Audio.Media.DATA
    val selectionArgs = arrayOf(songPath)
    val projection = arrayOf(MediaStore.Audio.Media._ID)
    val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
    val cursor = cr.query(
        uri, projection,
        "$selection=?", selectionArgs, null
    )
    if (cursor != null) {
        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            id = cursor.getString(idIndex).toLong()
        }
    }
    return id
}

private fun getFileType(file: String): String {
    val mimeType = URLConnection.guessContentTypeFromName(file)
    if (mimeType != null) {
        if (mimeType.startsWith("video")) {
            return "video"
        } else if (mimeType.startsWith("image")) {
            return "image"
        }
    }
    return ""
}

@RequiresApi(api = Build.VERSION_CODES.R)
fun deleteFiles(
    files: List<MediaItem>?,
    requestCode: Int,
    activity: AppCompatActivity,
    fillInIntent: Intent?
) {
    if (files.isNullOrEmpty()) {
        return
    }
    val uris = files.stream().map<Uri>(Function<MediaItem, Uri> { file: MediaItem ->
        val mediaID = getFilePathToMediaID(file.path, activity)
        val fileType = getFileType(file.path)
        if (fileType.equals("video", ignoreCase = true)) {
            return@Function ContentUris.withAppendedId(
                MediaStore.Video.Media.getContentUri("external"),
                mediaID
            )
        } else {
            return@Function ContentUris.withAppendedId(
                MediaStore.Images.Media.getContentUri("external"),
                mediaID
            )
        }
    }).collect(Collectors.toList<Uri>())


    val pi = MediaStore.createDeleteRequest(activity.contentResolver, uris)
    try {
        val intentSender = pi.intentSender
        activity.startIntentSenderForResult(intentSender, requestCode, fillInIntent, 0, 0, 0)
    } catch (e: SendIntentException) {
        e.printStackTrace()
    }
}
