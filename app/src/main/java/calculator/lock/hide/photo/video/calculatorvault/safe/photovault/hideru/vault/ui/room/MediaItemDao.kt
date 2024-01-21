package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType

@Dao
interface MediaItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(mediaItem: MediaItem)

    @Delete
    fun delete(mediaItem: MediaItem)

    @Update
    fun update(mediaItem: MediaItem)

    @Query("Select * from MediaItem WHERE type=:mediaType AND isTrash=0 ORDER BY id DESC")
    fun getAllData(mediaType: MediaType): LiveData<List<MediaItem>>

    @Query("Select * from MediaItem WHERE isTrash=:isTrash")
    fun getAllTrashData(isTrash: Int): LiveData<List<MediaItem>>

    @Query("Select * from MediaItem WHERE isTrash=:isTrash")
    fun getTrashData(isTrash: Int): List<MediaItem>

    @Query("Select * from MediaItem WHERE type=:mediaType AND folderName =:albumName AND isTrash=0")
    fun fetchMediaItemByAlbum(albumName: String, mediaType: MediaType): LiveData<List<MediaItem>>

    @Query("Select COUNT(id) from MediaItem WHERE isTrash=0 AND type=:medaType")
    fun getMediaCount(medaType: MediaType): LiveData<Int>

    @Query("Select COUNT(id) from MediaItem WHERE isTrash=1")
    fun getTrashCount(): LiveData<Int>

}