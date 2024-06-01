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


@Database(entities = [FruitVegetable::class, ItemsList::class, ListFruitsCrossRef::class], version = 8, exportSchema = false)
public abstract class FruitListRoomDatabase: RoomDatabase() {
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
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.fruitVegDao(), database.itemsListDao(), database.listFruitCrossRefDao())
                        populateDatabaseFromCSV(context, database.fruitVegDao())
                    }
                }
            }
        }
        suspend fun populateDatabase(fruitVegDao : FruitVegetableDao, itemsListDao: ItemsListDao, crossRefDao: ListFruitCrossRefDao){

            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.

            //TODO: EVITARE DI AGGUINGERE OGNI VOLTA

            itemsListDao.deleteAllLists()
        }

        //TODO: RENDERLO PRIVATO E POPOLARE SOLO ALLA CREAZIONE
        public suspend fun populateDatabaseFromCSV(context: Context, fruitVegDao: FruitVegetableDao) {
            fruitVegDao.deleteAllFruitVeg()

            try {
                val inputStream = context.assets.open("dataset-frutta.CSV")
                val reader = CSVReaderBuilder(InputStreamReader(inputStream))
                    .withSkipLines(1) // Salta la prima riga (intestazione)
                    .withCSVParser(com.opencsv.CSVParserBuilder().withSeparator(';').build()) // Specifica il separatore
                    .build()

                var nextLine: Array<String>?

                while (reader.readNext().also { nextLine = it } != null) {
                    Log.d("ggg",nextLine!![0])

                    val entity = FruitVegetable(
                        fruitVegId = nextLine!![0],
                        energyJoule = nextLine!![1].toDouble(),
                        energyCal = nextLine!![2].toDouble(),
                        proteins = nextLine!![3].toDouble(),
                        carbohydrates = nextLine!![4].toDouble(),
                        lipids = nextLine!![5].toDouble(),
                        fibre = nextLine!![6].toDouble()
                    )
                    fruitVegDao.insertFruitVeg(entity)
                }
                reader.close()
            } catch (e: Exception) {
                Log.e("Database", "Error reading CSV", e)
            }
        }

        //TODO: VERIFICARE SE SERVE
        fun repopulateDatabase(scope: CoroutineScope) {
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.fruitVegDao(), database.itemsListDao(), database.listFruitCrossRefDao())
                }
            }
        }

    }
}

