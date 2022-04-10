package com.laynik.transactionalstore.presentation

interface ButtonClickListeners {

    fun onSetClickListener(key: String, value: String)
    fun onGetClickListener(key: String)
    fun onDeleteClickListener(key: String)
    fun onCountClickListener(value: String)
    fun onBeginClickListener()
    fun onCommitClickListener()
    fun onRollbackClickListener()
}