package com.institutvidreres.winhabit.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.institutvidreres.winhabit.ui.recompensas.Recompensa

@Database(
        entities = [Recompensa::class],
        version = 1,
        exportSchema = false
    )

    abstract class RecompensasDatabase  : RoomDatabase(){
        abstract fun recompensaDao() : RecompensaDao

        companion object {

            @Volatile
            private var INSTANCE: RecompensasDatabase? = null

            fun getDatabase(context: Context): RecompensasDatabase {
                // if the INSTANCE is not null, then return it,
                // if it is, then create the database
                if (INSTANCE == null) {
                    synchronized(this) {
                        // Pass the database to the INSTANCE
                        INSTANCE = buildDatabase(context)
                    }
                }
                // Return database.
                return INSTANCE!!
            }

            private fun buildDatabase(context: Context): RecompensasDatabase {
                return Room.databaseBuilder(
                    context.applicationContext,
                    RecompensasDatabase::class.java,
                    "recompensa_database"
                )
                    .build()
            }
        }
    }
