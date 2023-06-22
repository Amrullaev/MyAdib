package com.amrullaev.myadib.database.dao

import androidx.room.*
import com.amrullaev.myadib.database.entity.WriterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WriterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(writerEntity: WriterEntity)

    @Query("select * from writerentity")
    fun getWriters():List<WriterEntity>?

    @Query("select * from writerentity where name = :name")
    fun getWriterByName(name:String): WriterEntity?

    @Delete()
    fun remove(writerEntity: WriterEntity)

    @Query("select * from writerentity")
    fun getWritersFlow(): Flow<List<WriterEntity>>

}