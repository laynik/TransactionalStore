package com.laynik.transactionalstore.data

import com.laynik.transactionalstore.domain.ErrorState
import com.laynik.transactionalstore.domain.ResponseState
import com.laynik.transactionalstore.domain.SuccessState
import com.laynik.transactionalstore.domain.TransactionRepository

class TransactionRepositoryImpl(
    private val dataSource: DataSource
) : TransactionRepository {
    override fun setValue(key: String, value: String): ResponseState {
        decValueCount(key)
        val oldCnt = dataSource.getData().valueMap.getOrDefault(value, 0)
        dataSource.getData().valueMap[value] = oldCnt + 1
        dataSource.getData().storeMap[key] = value
        return SuccessState(SET_SUCCESSFUL)
    }

    override fun getValue(key: String): ResponseState {
        return if (dataSource.getData().storeMap.containsKey(key)) {
            SuccessState(dataSource.getData().storeMap.getOrDefault(key, ""))
        } else {
            ErrorState(NO_KEY)
        }
    }

    override fun deleteEntry(key: String): ResponseState {
        return if (dataSource.getData().storeMap.containsKey(key)) {
            decValueCount(key)
            dataSource.getData().storeMap.remove(key)
            SuccessState(DELETE_SUCCESSFUL)
        } else {
            ErrorState(NO_KEY)
        }
    }

    private fun decValueCount(key: String) {
        if (dataSource.getData().storeMap.containsKey(key)) {
            val value = dataSource.getData().storeMap.getValue(key)
            val cnt = dataSource.getData().valueMap.getValue(value)
            if (cnt > 1) {
                dataSource.getData().valueMap.replace(value, cnt - 1)
            } else {
                dataSource.getData().valueMap.remove(value)
            }
        }
    }

    override fun countValue(value: String): ResponseState {
        return if (dataSource.getData().valueMap.containsKey(value)) {
            SuccessState(dataSource.getData().valueMap.getOrDefault(value, 0).toString())
        } else {
            ErrorState(NO_VALUE)
        }
    }

    override fun beginTransaction(): ResponseState {
        dataSource.begin()
        return SuccessState(BEGIN_SUCCESSFUL)
    }

    override fun commitTransaction(): ResponseState {
        return if (dataSource.commit()) {
            SuccessState(COMMIT_SUCCESSFUL)
        } else {
            ErrorState(NO_TRANSACTION)
        }
    }

    override fun rollbackTransaction(): ResponseState {
        return if (dataSource.rollback()) {
            SuccessState(ROLLBACK_SUCCESSFUL)
        } else {
            ErrorState(NO_TRANSACTION)
        }
    }

    override fun getHistory(): String {
        return dataSource.history.toString()
    }

    override fun addToHistory(newLine: String) {
        dataSource.history.append(newLine)
    }

    private companion object {
        private const val SET_SUCCESSFUL = "Value is successfully set"
        private const val NO_KEY = "There is no such key"
        private const val NO_VALUE = "There is no such value"
        private const val NO_TRANSACTION = "There is no transaction"
        private const val DELETE_SUCCESSFUL = "Entry was successfully deleted"
        private const val BEGIN_SUCCESSFUL = "New transaction started"
        private const val COMMIT_SUCCESSFUL = "Transaction's committed"
        private const val ROLLBACK_SUCCESSFUL = "Rollback successful"
    }
}