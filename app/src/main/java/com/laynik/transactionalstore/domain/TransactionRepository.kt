package com.laynik.transactionalstore.domain

interface TransactionRepository {
    fun setValue(key: String, value: String): ResponseState
    fun getValue(key: String): ResponseState
    fun deleteEntry(key: String): ResponseState
    fun countValue(value: String): ResponseState
    fun beginTransaction(): ResponseState
    fun commitTransaction(): ResponseState
    fun rollbackTransaction(): ResponseState
    fun getHistory(): String
    fun addToHistory(newLine: String)
}