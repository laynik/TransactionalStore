package com.laynik.transactionalstore.domain

sealed class ResponseState
data class SuccessState(val data: String) : ResponseState()
data class ErrorState(val data: String) : ResponseState()
