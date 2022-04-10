package com.laynik.transactionalstore.data

object DataSource {
    private val versions = mutableListOf<DataStorage>()
    val history = StringBuilder()

    init {
        versions.add(DataStorage())
    }

    fun rollback(): Boolean {
        return if (versions.size == 1) {
            false
        } else {
            versions.removeLastOrNull()
            true
        }
    }

    fun commit(): Boolean {
        return if (versions.size == 1) {
            false
        } else {
            val stableVersion = versions.last()
            versions.clear()
            versions.add(stableVersion)
            true
        }
    }

    fun begin() {
        versions.add(versions.last().copy())
    }

    fun getData() = versions.last()
}

data class DataStorage(
    val storeMap: MutableMap<String, String> = mutableMapOf(),
    val valueMap: MutableMap<String, Int> = mutableMapOf()
) {
    fun copy(): DataStorage = DataStorage(storeMap.toMutableMap(), valueMap.toMutableMap())
}

