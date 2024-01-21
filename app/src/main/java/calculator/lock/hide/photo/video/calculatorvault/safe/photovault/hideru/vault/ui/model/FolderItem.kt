package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Keep
@Entity
data class FolderItem(
    @PrimaryKey @ColumnInfo val id: Int,
    @ColumnInfo val folderName: String,
    @ColumnInfo val type: MediaType
):Serializable