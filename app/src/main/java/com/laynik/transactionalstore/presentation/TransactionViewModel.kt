package com.laynik.transactionalstore.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.laynik.transactionalstore.data.TransactionRepositoryImpl
import com.laynik.transactionalstore.domain.ErrorState
import com.laynik.transactionalstore.domain.ResponseState
import com.laynik.transactionalstore.domain.SuccessState
import com.laynik.transactionalstore.domain.TransactionRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TransactionViewModel : ViewModel(), ButtonClickListeners {

    private val repository: TransactionRepository = TransactionRepositoryImpl()
    private val bgScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val _uiState = MutableStateFlow<TransUiState>(WaitingState)
    val uiState: StateFlow<TransUiState> = _uiState

    override fun onCleared() {
        bgScope.cancel()
        super.onCleared()
    }

    private fun validData(data: String) = data.isNotEmpty()

    private fun retrieveData(state: ResponseState): String {
        return when (state) {
            is SuccessState -> {
                state.data
            }
            is ErrorState -> {
                state.data
            }
        }
    }

    private suspend fun sendSuccessData(data: String) {
        repository.addToHistory(data)
        _uiState.emit(SuccessUiState(data))
        delay(WAITING_DELAY)
        _uiState.emit(WaitingState)
    }

    private suspend fun sendErrorData(data: String) {
        _uiState.emit(ErrorUiState(data))
        delay(WAITING_DELAY)
        _uiState.emit(WaitingState)
    }

    override fun onSetClickListener(key: String, value: String) {
        viewModelScope.launch {
            if (!validData(key)) {
                sendErrorData(INVALID_KEY)
                return@launch
            }
            if (!validData(value)) {
                sendErrorData(INVALID_VALUE)
                return@launch
            }
            val data = ">Set $key $value\n" + retrieveData(repository.setValue(key, value)) + "\n"
            sendSuccessData(data)
        }
    }

    override fun onGetClickListener(key: String) {
        viewModelScope.launch {
            if (!validData(key)) {
                sendErrorData(INVALID_KEY)
                return@launch
            }
            val data = ">Get $key\n" + retrieveData(repository.getValue(key)) + "\n"
            sendSuccessData(data)
        }
    }

    override fun onCountClickListener(value: String) {
        viewModelScope.launch {
            if (!validData(value)) {
                sendErrorData(INVALID_VALUE)
                return@launch
            }
            val data = ">Count $value\n" + retrieveData(repository.countValue(value)) + "\n"
            sendSuccessData(data)
        }
    }

    override fun onBeginClickListener() {
        bgScope.launch {
            val data = ">Begin\n" + retrieveData(repository.beginTransaction()) + "\n"
            viewModelScope.launch {
                sendSuccessData(data)
            }
        }
    }

    override fun onCommitClickListener() {
        bgScope.launch {
            val data = ">Commit\n" + retrieveData(repository.commitTransaction()) + "\n"
            viewModelScope.launch {
                sendSuccessData(data)
            }
        }
    }

    override fun onRollbackClickListener() {
        bgScope.launch {
            val data = ">Rollback\n" + retrieveData(repository.rollbackTransaction()) + "\n"
            viewModelScope.launch {
                sendSuccessData(data)
            }
        }
    }

    override fun onDeleteClickListener(key: String) {
        viewModelScope.launch {
            if (!validData(key)) {
                sendErrorData(INVALID_KEY)
                return@launch
            }
            val data = ">Delete $key\n" + retrieveData(repository.deleteEntry(key)) + "\n"
            sendSuccessData(data)
        }
    }

    fun getHistory() = repository.getHistory()

    private companion object {
        private const val INVALID_KEY = "You must fill up Key field"
        private const val INVALID_VALUE = "You must fill up Value field"
        private const val WAITING_DELAY = 100L
    }
}

sealed class TransUiState
object WaitingState : TransUiState()
data class SuccessUiState(val data: String) : TransUiState()
data class ErrorUiState(val data: String) : TransUiState()
