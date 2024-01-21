package calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.FolderItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.MediaItem
import calculator.lock.hide.photo.video.calculatorvault.safe.photovault.hideru.vault.ui.model.NoteItem

@Database(
    entities = [MediaItem::class, FolderItem::class, NoteItem::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getMedaItemDao(): MediaItemDao
    abstract fun getFolderItemDao(): FolderItemDao
    abstract fun getNotesItemDao(): NoteItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calculator_db"
                ).allowMainThreadQueries().addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }


        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `noteItem` (" +
                            "`noteTitle` TEXT NOT NULL, `createdDate` INTEGER NOT NULL, `id` INTEGER NOT NULL, `noteDescription` TEXT NOT NULL," +
                            "`type` TEXT NOT NULL, `isTrash` INTEGER NOT NULL, " +
                            "`deleteDate` TEXT NOT NULL," +
                            "PRIMARY KEY(`id`))"
                )
            }
        }

    }


}