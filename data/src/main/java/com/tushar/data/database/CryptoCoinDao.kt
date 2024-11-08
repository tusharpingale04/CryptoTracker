package com.tushar.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tushar.data.models.database.CryptoCoinEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CryptoCoinDao {
    @Query(
        """
    SELECT * FROM crypto_coins_table
    WHERE 
        ((:searchQuery IS NULL OR TRIM(:searchQuery) = '') OR 
         (name LIKE '%' || :searchQuery || '%') OR 
         (symbol LIKE '%' || :searchQuery || '%')) AND
        (:isActive IS NULL OR isActive = :isActive) AND
        (:isNotActive IS NULL OR isActive != :isNotActive) AND
        (:coinType IS NULL OR type = :coinType) AND
        (:isNew IS NULL OR isNew = :isNew)
    """
    )
    fun getAllCoins(
        searchQuery: String?,
        isActive: Boolean?,
        isNotActive: Boolean?,
        coinType: String?,
        isNew: Boolean?
    ): Flow<List<CryptoCoinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coins: List<CryptoCoinEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM crypto_coins_table LIMIT 1)")
    suspend fun hasCryptoEntries(): Boolean
}