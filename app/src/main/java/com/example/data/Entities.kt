package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet_state")
data class DbWalletState(
    @PrimaryKey val id: Int = 1,
    val mnemonic: String,
    val privateKeyHex: String,
    val publicKeyHex: String,
    val address: String,
    val balanceEln: Long = 1248025000000L // 12,480.25 TKS in eln (1 TKS = 10^8 eln)
)

@Entity(tableName = "transactions")
data class DbTransaction(
    @PrimaryKey val txId: String,
    val sender: String,
    val recipient: String,
    val amountEln: Long,
    val feeEln: Long,
    val timestamp: Long,
    val signature: String,
    val status: String, // "PENDING", "CONFIRMED", "FAILED"
    val blockIndex: Int? = null
)

@Entity(tableName = "blocks")
data class DbBlock(
    @PrimaryKey val blockIndex: Int,
    val timestamp: Long,
    val previousHash: String,
    val nonce: Long,
    val difficulty: Int,
    val blockHash: String,
    val txCount: Int
)

@Entity(tableName = "peers")
data class DbPeer(
    @PrimaryKey val id: String,
    val address: String,
    val carrier: String,
    val lastSeen: Long,
    val isConnected: Boolean
)
