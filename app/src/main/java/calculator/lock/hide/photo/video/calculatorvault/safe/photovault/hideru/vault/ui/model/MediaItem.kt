package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Keep
@Entity
data class MediaItem(
    @PrimaryKey @ColumnInfo val id: Long,
    @ColumnInfo var path: String,
    @ColumnInfo var type: MediaType,
    @ColumnInfo var resolutionWidth: Int,
    @ColumnInfo var resolutionHeight: Int,
    @ColumnInfo var size: Long,
    @ColumnInfo var folderName: String,
    @ColumnInfo var folderNameId: Int,
    @ColumnInfo var hidePath: String = "",
    @ColumnInfo var isTrash: Int = 0,
    @ColumnInfo var deleteDate: String = ""

):Serializable{
    @Ignore val resolution: Pair<Int, Int> = Pair(resolutionWidth, resolutionHeight)
}