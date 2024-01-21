package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.filemanager

import android.content.Context
import android.os.storage.StorageManager
import com.simplemobiletools.commons.extensions.isPathOnOTG
import com.simplemobiletools.commons.extensions.isPathOnSD
import com.simplemobiletools.commons.helpers.isNougatPlus
import java.util.Locale

val Context.config: Config get() = Config.newInstance(applicationContext)

fun Context.isPathOnRoot(path: String) = !(path.startsWith(config.internalStoragePath) || isPathOnOTG(path) || (isPathOnSD(path)))

fun Context.getAllVolumeNames(): List<String> {
    val volumeNames = mutableListOf(PRIMARY_VOLUME_NAME)
    if (isNougatPlus()) {
        val storageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        getExternalFilesDirs(null)
            .mapNotNull { storageManager.getStorageVolume(it) }
            .filterNot { it.isPrimary }
            .mapNotNull { it.uuid?.lowercase(Locale.US) }
            .forEach {
                volumeNames.add(it)
            }
    }
    return volumeNames
}
