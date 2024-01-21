package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Keep
@Entity
data class NoteItem(
    @PrimaryKey @ColumnInfo val id: Int,
    @ColumnInfo var noteTitle: String,
    @ColumnInfo var noteDescription: String,
    @ColumnInfo val type: MediaType,
    @ColumnInfo var isTrash: Int = 0,
    @ColumnInfo var deleteDate: String = "",
    @ColumnInfo var createdDate: Long = 0
):Serializable
