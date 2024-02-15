package com.institutvidreres.winhabit.repositori

import android.content.Context
import androidx.lifecycle.LiveData
import com.institutvidreres.winhabit.databases.RecompensasDatabase
import com.institutvidreres.winhabit.ui.recompensas.Recompensa
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RoomRepository {
    companion object {

        var recompensasDatabase: RecompensasDatabase? = null
        var recompensa: LiveData<List<Recompensa>>? = null

        fun initializeDB(context: Context): RecompensasDatabase {
            return RecompensasDatabase.getDatabase(context)
        }

        //INSERT recompensa
        fun insertRecompensa(context: Context, recompensa: Recompensa) {

            recompensasDatabase = initializeDB(context)

            CoroutineScope(Dispatchers.IO).launch {
                recompensasDatabase!!.recompensaDao().addRecompensa(recompensa)
            }
        }

        // Obtener los personajes de recompensas
        fun getPersonajes(context: Context): LiveData<List<Recompensa>>? {

            recompensasDatabase = initializeDB(context)

            recompensa = recompensasDatabase!!.recompensaDao().getPersonajes()

            return recompensa
        }
    }
}