package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class FolderItemWithMediaItems(
    @Embedded val folderItem: FolderItem,
    @Relation(
        parentColumn = "id",
        entityColumn = "folderNameId"
    )
    var mediaItems: List<MediaItem> = emptyList()
):Serializable
