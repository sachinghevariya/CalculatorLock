package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Function to copy media file from the system to app files directory
suspend fun copyMediaFileToAppFilesDir(context: Context, mediaItem: MediaItem) {
    withContext(Dispatchers.IO) {
        val sourceFile = File(mediaItem.path)
        val destinationDir = File(context.filesDir, "hideImages")
        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }
        val destinationFile = File(destinationDir, sourceFile.name)

        try {
            val inputStream = FileInputStream(sourceFile)
            val outputStream = FileOutputStream(destinationFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

            // File copied successfully
        } catch (e: IOException) {
            // Handle any errors that may occur during file copying
            e.printStackTrace()
        }
    }
}

// Function to move media file from the system to app files directory (deletes the original file)
suspend fun moveMediaFileToAppFilesDir(
    context: Context,
    mediaItem: MediaItem,
    callback: (hidePath: String) -> Unit
) {
    withContext(Dispatchers.IO) {
        val sourceFile = File(mediaItem.path)
        val destinationDir = File(context.filesDir, "hideImages")
        if (!destinationDir.exists()) {
            destinationDir.mkdirs()
        }
        val destinationFile = File(destinationDir, sourceFile.name)
        try {
            /* val inputStream = FileInputStream(sourceFile)
             val outputStream = FileOutputStream(destinationFile)
             val buffer = ByteArray(1024)
             var bytesRead: Int

             while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                 outputStream.write(buffer, 0, bytesRead)
             }

             inputStream.close()
             outputStream.close()

             // Wait for deleteFileWithCursor to complete before proceeding
             val rowsDeleted = suspendCoroutine<Int> { continuation ->
                 deleteFileWithCursor(context, mediaItem) { rowsDeleted ->
                     callback.invoke(destinationFile.absolutePath)
                     Log.e("TAG", "moveMediaFileToAppFilesDir: $rowsDeleted" )
                     continuation.resume(rowsDeleted)
                 }
             }*/

            FileInputStream(sourceFile).use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int

                    while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
            }

            // Wait for deleteFileWithCursor to complete before proceeding
            val rowsDeleted = suspendCoroutine<Int> { continuation ->
                deleteFileWithCursor(context, mediaItem) { rowsDeleted ->
                    callback.invoke(destinationFile.absolutePath)
                    continuation.resume(rowsDeleted)
                }
            }

        } catch (e: IOException) {
            // Handle any errors that may occur during file moving
            e.printStackTrace()
        }
    }
}


// Function to delete a file using a cursor
fun deleteFileWithCursor(context: Context, mediaItem: MediaItem, callback: (Int) -> Unit) {

    val fileUri =
        FileProvider.getUriForFile(context, context.packageName + ".provider", File(mediaItem.path))


    val contentResolver: ContentResolver = context.contentResolver

    // Define the selection and selectionArgs to specify the file to delete
    val selection = "${MediaStore.MediaColumns._ID} = ?"
    val selectionArgs = arrayOf(fileUri.lastPathSegment)

    // Delete the file using the ContentResolver
    val rowsDeleted = contentResolver.delete(
        fileUri,
        selection,
        selectionArgs
    )
    MediaScannerConnection.scanFile(
        context,
        arrayOf(File(mediaItem.path).absolutePath),
        null
    ) { path, uri ->
    }

//    context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, fileUri))

    callback(rowsDeleted)

//    if (rowsDeleted > 0) {
//        MediaScannerConnection.scanFile(
//            context,
//            arrayOf(fileUri.path),
//            null
//        ) { path, uri ->
//            Log.e("TAG", "deleteFileWithCursor:MediaScannerConnection ")
//            // Handle the completion of media scanning if needed
//        }
//    } else {
//    }

}

/*fun deleteFileWithCursor(context: Context, mediaItem: MediaItem) {
    val fileUri = FileProvider.getUriForFile(context, context.packageName + ".provider", File(mediaItem.path))

    Log.e("TAG", "deleteFileWithCursor: $fileUri")

    val fileToDelete = File(mediaItem.path)

    try {
        if (fileToDelete.exists()) {
            if (fileToDelete.delete()) {
                Log.e("TAG", "File deleted successfully: ${fileToDelete.path}")

                // Notify the MediaScanner to remove the file from the gallery
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(fileUri.path),
                    null
                ) { path, uri ->
                    Log.e("TAG", "deleteFileWithCursor: MediaScannerConnection")
                    // Handle the completion of media scanning if needed
                }
            } else {
                Log.e("TAG", "Failed to delete the file: ${fileToDelete.path}")
            }
        } else {
            Log.e("TAG", "File does not exist: ${fileToDelete.path}")
        }
    }catch (e:Exception){
        e.printStackTrace()
    }

}*/

// Function to move media file from hidePath to path using cursor and MediaScanner
suspend fun moveMediaFileUsingCursor(
    context: Context,
    mediaItem: MediaItem,
    callback: (hidePath: String) -> Unit
) {
    withContext(Dispatchers.IO) {
        val sourceFile = File(mediaItem.hidePath)
        val destinationDir = File(mediaItem.path).parentFile

        if (!sourceFile.exists()) {
            callback.invoke(mediaItem.path)
            return@withContext
        }
        try {
            if (!destinationDir.exists()) {
                destinationDir.mkdirs()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val destinationFile = File(destinationDir, sourceFile.name)

        /*try {
            val inputStream = FileInputStream(sourceFile)
            val outputStream = FileOutputStream(destinationFile)
            val buffer = ByteArray(1024)
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                outputStream.write(buffer, 0, bytesRead)
            }

            inputStream.close()
            outputStream.close()

            // Delete the original file
            sourceFile.delete()

            // Use MediaScanner to notify the system about the change
            MediaScannerConnection.scanFile(
                context,
                arrayOf(destinationFile.absolutePath),
                null
            ) { path, uri ->
                // Handle the completion of media scanning if needed
            }

            callback.invoke(destinationFile.absolutePath)
        } catch (e: IOException) {
            // Handle any errors that may occur during file moving
            e.printStackTrace()
        }*/


        try {
            // Use FileChannel for efficient file copying
            FileInputStream(sourceFile).use { inputStream ->
                FileOutputStream(destinationFile).use { outputStream ->
                    val inputChannel = inputStream.channel
                    val outputChannel = outputStream.channel

                    inputChannel.transferTo(0, inputChannel.size(), outputChannel)

                    // Delete the original file
                    sourceFile.delete()

                    // Use MediaScanner to notify the system about the change
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(destinationFile.absolutePath),
                        null
                    ) { path, uri ->
                        // Handle the completion of media scanning if needed
                    }

                    callback.invoke(destinationFile.absolutePath)
                }
            }
        } catch (e: IOException) {
            // Handle any errors that may occur during file moving
            e.printStackTrace()
        }
    }
}

fun createDirectoriesForFilePath(filePath: String): String {
    val file = File(filePath)
    val parentDirectory = file.parentFile

    if (parentDirectory != null && !parentDirectory.exists()) {
        // Create parent directories if they do not exist
        parentDirectory.mkdirs()
    }
    return parentDirectory.absolutePath
}


// Function to fetch media items from storage using coroutines
suspend fun fetchVideoMediaFolderItems(context: Context): HashMap<String, ArrayList<MediaItem>> =
    withContext(Dispatchers.IO) {
        val mediaMap = HashMap<String, ArrayList<MediaItem>>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.SIZE
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val queryUri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val mediaTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH) ?: 0
            val heightColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT) ?: 0
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE) ?: 0

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(dataColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val size = cursor.getLong(sizeColumn)
                val folderPath = File(path).parent
                var folderName = ""
                var folderNameId = 0
                val type = if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    folderName = "My Photos"
                    folderNameId = 0
                    MediaType.IMAGE
                } else {
                    folderName = "My Videos"
                    folderNameId = 1
                    val type = MediaType.VIDEO
                    val mediaItem =
                        MediaItem(id, path, type, width, height, size, folderName, folderNameId)
                    if (mediaMap.containsKey(folderPath)) {
                        mediaMap[folderPath]?.add(mediaItem)
                    } else {
                        val mediaList = ArrayList<MediaItem>()
                        mediaList.add(mediaItem)
                        mediaMap[folderPath] = mediaList
                    }
                }


            }
        }

        return@withContext mediaMap
    }


// Function to fetch media items from storage using coroutines
suspend fun fetchMediaFolderItems(context: Context): HashMap<String, ArrayList<MediaItem>> =
    withContext(Dispatchers.IO) {
        val mediaMap = HashMap<String, ArrayList<MediaItem>>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.SIZE
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val queryUri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->

            if (cursor.moveToFirst()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                val mediaTypeColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
                val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
                val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val path = cursor.getString(dataColumn)
                    val mediaType = cursor.getInt(mediaTypeColumn)
                    val width = cursor.getInt(widthColumn)
                    val height = cursor.getInt(heightColumn)
                    val size = cursor.getLong(sizeColumn)
                    val folderPath = File(path).parent
                    var folderName = ""
                    var folderNameId = 0
                    val type = if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                        folderName = "My Photos"
                        folderNameId = 0
                        val type = MediaType.IMAGE
                        val mediaItem =
                            MediaItem(id, path, type, width, height, size, folderName, folderNameId)
                        Log.e("TAG", "fetchMediaFolderItems: $path")

                        if (mediaMap.containsKey(folderPath)) {
                            mediaMap[folderPath]?.add(mediaItem)
                        } else {
                            val mediaList = ArrayList<MediaItem>()
                            mediaList.add(mediaItem)
                            mediaMap[folderPath] = mediaList
                        }
                    } else {
                        folderName = "My Videos"
                        folderNameId = 1
                        MediaType.VIDEO
                    }

                }
            }

        }

        return@withContext mediaMap
    }

// Function to fetch media items from storage using coroutines
suspend fun fetchMediaItems(context: Context): List<MediaItem> = withContext(Dispatchers.IO) {
    val mediaItems = mutableListOf<MediaItem>()

    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns.WIDTH,
        MediaStore.Files.FileColumns.HEIGHT,
        MediaStore.Files.FileColumns.SIZE
    )

    val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"

    val selectionArgs = arrayOf(
        MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
        MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
    )

    val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

    val queryUri = MediaStore.Files.getContentUri("external")

    context.contentResolver.query(
        queryUri,
        projection,
        selection,
        selectionArgs,
        sortOrder
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
        val mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
        val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
        val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
        val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)


        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val path = cursor.getString(dataColumn)
            val mediaType = cursor.getInt(mediaTypeColumn)
            val width = cursor.getInt(widthColumn)
            val height = cursor.getInt(heightColumn)
            val size = cursor.getLong(sizeColumn)
            var folderName = ""
            var folderNameId = 0
            val type = if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                folderName = "My Photos"
                folderNameId = 0
                MediaType.IMAGE
            } else {
                folderName = "My Videos"
                folderNameId = 1
                MediaType.VIDEO
            }

            mediaItems.add(MediaItem(id, path, type, width, height, size, folderName, folderNameId))
        }
    }

    return@withContext mediaItems
}

suspend fun createMediaItemsFromFilePaths(
    context: Context,
    filePaths: List<String>,
    isVideo: Boolean,
    isAudio: Boolean
): List<MediaItem> =
    withContext(Dispatchers.IO) {
        val mediaItems = mutableListOf<MediaItem>()
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.SIZE
        )


        val selection =
            "${MediaStore.Files.FileColumns.DATA} IN (${filePaths.joinToString(", ") { '?'.toString() }})"
        val selectionArgs = filePaths.toTypedArray()
        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val queryUri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val mediaTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(dataColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val size = cursor.getLong(sizeColumn)
                var folderName = ""
                var folderNameId = 0

                val type = if (isAudio) {
                    folderName = "My Audios"
                    folderNameId = 2
                    MediaType.AUDIO
                } else if (!isVideo) {
                    folderName = "My Photos"
                    folderNameId = 0
                    MediaType.IMAGE
                } else {
                    folderName = "My Videos"
                    folderNameId = 1
                    MediaType.VIDEO
                }

                mediaItems.add(
                    MediaItem(
                        id = id,
                        path = path,
                        type = type,
                        resolutionWidth = width,
                        resolutionHeight = height,
                        size = size,
                        folderName = folderName,
                        folderNameId = folderNameId
                    )
                )
            }
        }

        return@withContext mediaItems
    }

fun shareMultipleMediaItem(
    context: Context,
    mediaItems: ArrayList<MediaItem>,
    callback: () -> Unit
) {
    val files = ArrayList<Uri>()
    val shareIntent = Intent()
    shareIntent.action = "android.intent.action.SEND_MULTIPLE"
    shareIntent.putExtra("android.intent.extra.SUBJECT", "")
    if (mediaItems.size > 0) {
        if (mediaItems[0].type == MediaType.IMAGE) {
            shareIntent.type = "image/*"
        } else {
            shareIntent.type = "video/*"
        }
    }

    mediaItems.forEach {
        val fileUri = FileProvider.getUriForFile(
            context,
            context.packageName + ".provider",
            File(it.hidePath)
        )
        files.add(fileUri)
    }
    shareIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", files)
    context.startActivity(shareIntent)
    callback.invoke()
}

suspend fun fetchMediaItemById(context: Context, mediaItemId: Long): MediaItem? =
    withContext(Dispatchers.IO) {
        var mediaItem: MediaItem? = null

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.SIZE
        )

        val selection = "${MediaStore.Files.FileColumns._ID} = ?"

        val selectionArgs = arrayOf(mediaItemId.toString())

        val queryUri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val mediaTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            if (cursor.moveToFirst()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(dataColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val size = cursor.getLong(sizeColumn)
                var folderName = ""
                var folderNameId = 0
                val type = if (mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
                    folderName = "My Photos"
                    folderNameId = 0
                    MediaType.IMAGE
                } else {
                    folderName = "My Videos"
                    folderNameId = 1
                    MediaType.VIDEO
                }

                mediaItem = MediaItem(id, path, type, width, height, size, folderName, folderNameId)
            }
        }

        return@withContext mediaItem
    }


//-------------------------------------------Audio--------------------------------------------------
suspend fun fetchAudioMediaItems(context: Context): HashMap<String, ArrayList<MediaItem>> =
    withContext(Dispatchers.IO) {
        val mediaMap = HashMap<String, ArrayList<MediaItem>>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.SIZE
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?, ?)"

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val queryUri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val mediaTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(dataColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val size = cursor.getLong(sizeColumn)
                val folderPath = File(path).parent
                var folderName = ""
                var folderNameId = 0

                folderName = "My Audios"
                folderNameId = 2
                val type = MediaType.AUDIO
                val mediaItem =
                    MediaItem(id, path, type, width, height, size, folderName, folderNameId)
                Log.e("TAG", "fetchAudioMediaItems: $path")

                if (mediaMap.containsKey(folderPath)) {
                    mediaMap[folderPath]?.add(mediaItem)
                } else {
                    val mediaList = ArrayList<MediaItem>()
                    mediaList.add(mediaItem)
                    mediaMap[folderPath] = mediaList
                }

            }
        }

        return@withContext mediaMap
    }


//-------------------------------------------Document--------------------------------------------------
/*
suspend fun fetchDocumentMediaItems(context: Context): HashMap<String, ArrayList<MediaItem>> =
    withContext(Dispatchers.IO) {
        val mediaMap = HashMap<String, ArrayList<MediaItem>>()

        val documentExtensions = setOf(
            "pdf", "doc", "docx", "txt", "ppt", "pptx", "xls", "xlsx",
            "csv", "odp", "ods", "odt", "ott", "rtf"
        )
        val rootDir = Environment.getExternalStorageDirectory()
        findDocuments(rootDir, documentExtensions, mediaMap)

        return@withContext mediaMap
    }

private fun findDocuments(
    directory: File,
    documentExtensions: Set<String>,
    mediaMap: HashMap<String, ArrayList<MediaItem>>
) {
    val files = directory.listFiles() ?: return

    for (file in files) {
        if (file.isDirectory) {
            // Recursively search subdirectories
            findDocuments(file, documentExtensions, mediaMap)
        } else {
            val extension = file.extension.toLowerCase(Locale.ROOT)
            if (documentExtensions.contains(extension)) {
                // This file has a document extension
                val folderPath = file.parent
                val folderName = "My Documents"
                val folderNameId = 3 // Assign an appropriate ID for the folder
                val type = MediaType.DOC // Replace with the appropriate media type

                Log.e("TAG", "fetchDocumentMediaItems: ${file.absolutePath}")
                val mediaItem = MediaItem(
                    id = 0, // Assign a unique ID
                    path = file.absolutePath,
                    type = type,
                    resolutionWidth = 0,
                    resolutionHeight = 0,
                    size = file.length(),
                    folderName = folderName,
                    folderNameId = folderNameId
                )

                if (mediaMap.containsKey(folderPath)) {
                    mediaMap[folderPath]?.add(mediaItem)
                } else {
                    val mediaList = ArrayList<MediaItem>()
                    mediaList.add(mediaItem)
                    mediaMap[folderPath] = mediaList
                }
            }
        }
    }
}
*/

suspend fun fetchDocumentMediaItems(context: Context): HashMap<String, ArrayList<MediaItem>> =
    withContext(Dispatchers.IO) {
        val mediaMap = HashMap<String, ArrayList<MediaItem>>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.WIDTH,
            MediaStore.Files.FileColumns.HEIGHT,
            MediaStore.Files.FileColumns.SIZE
        )


        val selection =
            "${MediaStore.Files.FileColumns.MIME_TYPE} IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"

        val selectionArgs = arrayOf(
            "application/pdf",
            "text/csv",
            "application/vnd.oasis.opendocument.presentation",
            "application/vnd.oasis.opendocument.spreadsheet",
            "application/vnd.oasis.opendocument.text",
            "application/vnd.sun.xml.writer.template",
            "application/rtf",
            "application/x-7z-compressed",
            "application/x-abiword",
            "application/vnd.android.package-archive",
            "application/x-freearc",
            "application/vnd.amazon.ebook",
            "application/x-bzip",
            "application/x-bzip2",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/x-gzip",
            "application/json",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "application/vnd.ms-works",
            "application/excel",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/zip",
            "application/vnd.oasis.opendocument.text-template",
            "text/plain",
            "application/csv",
            "text/rtf"
        )


        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val queryUri = MediaStore.Files.getContentUri("external")

        context.contentResolver.query(
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val mediaTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val widthColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.WIDTH)
            val heightColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.HEIGHT)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(dataColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val width = cursor.getInt(widthColumn)
                val height = cursor.getInt(heightColumn)
                val size = cursor.getLong(sizeColumn)
                val folderPath = File(path).parent
                var folderName = ""
                var folderNameId = 0

                folderName = "My Documents"
                folderNameId = 3
                val type = MediaType.DOC
                val mediaItem =
                    MediaItem(id, path, type, width, height, size, folderName, folderNameId)
                if (mediaMap.containsKey(folderPath)) {
                    mediaMap[folderPath]?.add(mediaItem)
                } else {
                    val mediaList = ArrayList<MediaItem>()
                    mediaList.add(mediaItem)
                    mediaMap[folderPath] = mediaList
                }
            }
        }

        return@withContext mediaMap
    }


