package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItemWithMediaItems
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaType

@Dao
interface FolderItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(folderItem: FolderItem)

    @Delete
    fun delete(folderItem: FolderItem)

    @Query("Select * from FolderItem")
    fun getAllData(): LiveData<List<FolderItem>>

    @Query("Select * from FolderItem")
    fun getAllFolderData(): List<FolderItem>

    @Query("SELECT * FROM FolderItem WHERE folderName IN (:folderNames)")
    fun getFoldersByName(folderNames: List<String>): List<FolderItem>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFolderItems(vararg folderItems: FolderItem)

    @Query("SELECT * FROM FolderItem WHERE type =:mediaType")
    fun getFoldersByType(mediaType: MediaType): LiveData<List<FolderItem>>


    @Query("SELECT * FROM FolderItem WHERE folderName = :name")
    fun getFolderByName(name: String): FolderItem?

    @Transaction
    @Query(" SELECT * FROM FolderItem WHERE type =:mediaType")
    fun getFoldersWithMediaItems(mediaType: MediaType): LiveData<List<FolderItemWithMediaItems>>

}