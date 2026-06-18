package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {
    @Query("SELECT * FROM wallet_state WHERE id = 1")
    fun getWalletFlow(): Flow<DbWalletState?>

    @Query("SELECT * FROM wallet_state WHERE id = 1")
    suspend fun getWallet(): DbWalletState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveWallet(wallet: DbWalletState)

    @Query("UPDATE wallet_state SET balanceEln = :balance WHERE id = 1")
    suspend fun updateBalance(balance: Long)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): Flow<List<DbTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: DbTransaction)

    @Query("SELECT * FROM transactions WHERE txId = :txId")
    suspend fun getTransactionById(txId: String): DbTransaction?

    @Query("UPDATE transactions SET status = :status, blockIndex = :blockIndex WHERE txId = :txId")
    suspend fun confirmTransaction(txId: String, status: String, blockIndex: Int)
}

@Dao
interface BlockDao {
    @Query("SELECT * FROM blocks ORDER BY blockIndex DESC")
    fun getAllBlocksFlow(): Flow<List<DbBlock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBlock(block: DbBlock)

    @Query("SELECT * FROM blocks ORDER BY blockIndex DESC LIMIT 1")
    suspend fun getLatestBlock(): DbBlock?
}

@Dao
interface PeerDao {
    @Query("SELECT * FROM peers ORDER BY lastSeen DESC")
    fun getAllPeersFlow(): Flow<List<DbPeer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeer(peer: DbPeer)

    @Query("DELETE FROM peers WHERE id = :id")
    suspend fun deletePeer(id: String)
}
