package com.example.prova_progetto.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KParameter

@Database(entities = [FruitVegetable::class, ItemsList::class, ListFruitsCrossRef::class], version = 1, exportSchema = false)
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
                    "fruitList_database"
                ).fallbackToDestructiveMigration()
                    .addCallback(FruitVegDatabaseCallback(scope))
                    .build()

                INSTANCE = instance

                instance
            }
        }

        private class FruitVegDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.fruitVegDao(), database.itemsListDao(), database.listFruitCrossRefDao())
                    }
                }
            }
        }
        suspend fun populateDatabase(fruitVegDao : FruitVegetableDao, itemsListDao: ItemsListDao, crossRefDao: ListFruitCrossRefDao){

            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.

            //TODO: EVITARE DI AGGUINGERE OGNI VOLTA

            val list1 = ItemsList(listTitle = "LaPrimaListaTest")
            val list2 = ItemsList(listTitle = "LaSecondaListaTest")
            val list3 = ItemsList(listTitle = "LaTerzaListaTest")
            val list4 = ItemsList(listTitle = "LaQuartaListaTest")

            itemsListDao.insertList(list1)
            itemsListDao.insertList(list2)
            itemsListDao.insertList(list3)
            itemsListDao.insertList(list4)

        }

    }
}

