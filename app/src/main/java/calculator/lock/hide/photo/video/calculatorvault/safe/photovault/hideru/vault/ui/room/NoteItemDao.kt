package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.NoteItem

@Dao
interface NoteItemDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(noteItem: NoteItem)

    @Delete
    fun delete(noteItem: NoteItem)

    @Update
    fun update(noteItem: NoteItem)

    @Query("Select * from NoteItem where isTrash = 0 ORDER BY createdDate DESC")
    fun getAllNotesData(): LiveData<List<NoteItem>>
    @Query("Select * from NoteItem where isTrash = 1")
    fun getAllTrashNotes():LiveData<List<NoteItem>>

    @Query("Select COUNT(id) from NoteItem where isTrash = 0")
    fun getNotesCount(): LiveData<Int>

    @Query("Select COUNT(id) from NoteItem where isTrash = 1")
    fun getNotesTrashCount(): LiveData<Int>
}