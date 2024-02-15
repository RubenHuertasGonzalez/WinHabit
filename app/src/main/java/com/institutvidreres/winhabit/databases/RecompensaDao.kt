package com.institutvidreres.winhabit.databases

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.institutvidreres.winhabit.ui.recompensas.Recompensa

@Dao
interface RecompensaDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addRecompensa(recompensa: Recompensa)

    @Query("SELECT * FROM RecompensaData WHERE nombre = 'Personaje'")
    fun getPersonajes(): LiveData<List<Recompensa>>
}