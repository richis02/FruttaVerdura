package com.example.prova_progetto.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.opencsv.CSVReaderBuilder
import java.io.InputStreamReader


@Database(entities = [FruitVegetable::class, ItemsList::class, ListFruitsCrossRef::class], version = 24, exportSchema = false)
abstract class FruitListRoomDatabase: RoomDatabase() {
    abstract fun fruitVegDao() : FruitVegetableDao
    abstract fun itemsListDao() : ItemsListDao
    abstract fun listFruitCrossRefDao() : ListFruitCrossRefDao

    companion object {
        @Volatile
        private var INSTANCE: FruitListRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FruitListRoomDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FruitListRoomDatabase::class.java,
                    "items_list"
                ).fallbackToDestructiveMigration()
                    .addCallback(FruitVegDatabaseCallback(scope, context))
                    .build()

                INSTANCE = instance

                instance
            }
        }

        private class FruitVegDatabaseCallback(
            private val scope: CoroutineScope,
            private val context: Context
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabaseFromCSV(context, database.fruitVegDao())
                    }
                }
            }

            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)

                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabaseFromCSV(context, database.fruitVegDao())
                    }
                }
            }


        }

         suspend fun populateDatabaseFromCSV(context: Context, fruitVegDao: FruitVegetableDao) {

             fruitVegDao.deleteAllFruitVeg()

            try {
                val inputStream = context.assets.open("dataset-frutta.CSV")
                val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                    .withSkipLines(1) // Salta la prima riga (intestazione)
                    .withCSVParser(com.opencsv.CSVParserBuilder().withSeparator(';').build()) // Specifica il separatore
                    .build()

                var nextLine: Array<String>?

                while (reader.readNext().also { nextLine = it } != null) {

                    val entity = FruitVegetable(
                        fruitVegName = nextLine!![0],
                        energyJoule = nextLine!![1].toDouble(),
                        energyCal = nextLine!![2].toDouble(),
                        proteins = nextLine!![3].toDouble(),
                        carbohydrates = nextLine!![4].toDouble(),
                        lipids = nextLine!![5].toDouble(),
                        fibre = nextLine!![6].toDouble(),
                        img = nextLine!![7],
                        photo = nextLine!![8]
                    )
                    fruitVegDao.insertFruitVeg(entity)
                }
                reader.close()
            } catch (e: Exception) {
                Log.e("Database", "Errore lettura CSV", e)
            }
        }
    }
}

