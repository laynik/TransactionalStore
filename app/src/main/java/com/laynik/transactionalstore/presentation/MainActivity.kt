package com.laynik.transactionalstore.presentation

import android.content.res.Configuration
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.laynik.transactionalstore.R
import com.laynik.transactionalstore.data.DataSource
import com.laynik.transactionalstore.data.TransactionRepositoryImpl
import kotlinx.coroutines.flow.collectLatest

class MainActivity : AppCompatActivity() {

    private val transactionViewModel = TransactionViewModel(TransactionRepositoryImpl(DataSource))
    private lateinit var btnSet: MaterialButton
    private lateinit var btnGet: MaterialButton
    private lateinit var btnDelete: MaterialButton
    private lateinit var btnCount: MaterialButton
    private lateinit var btnBegin: MaterialButton
    private lateinit var btnCommit: MaterialButton
    private lateinit var btnRollback: MaterialButton
    private lateinit var etSetKey: EditText
    private lateinit var etSetValue: EditText
    private lateinit var etGetKey: EditText
    private lateinit var etDeleteKey: EditText
    private lateinit var etCountValue: EditText
    private lateinit var tvOutputPortrait: TextView
    private lateinit var tvOutputLandscape: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnSet = findViewById(R.id.btn_set)
        btnGet = findViewById(R.id.btn_get)
        btnDelete = findViewById(R.id.btn_delete)
        btnCount = findViewById(R.id.btn_count)
        btnBegin = findViewById(R.id.btn_begin)
        btnCommit = findViewById(R.id.btn_commit)
        btnRollback = findViewById(R.id.btn_rollback)
        etSetKey = findViewById(R.id.et_set_key)
        etSetValue = findViewById(R.id.et_set_value)
        etGetKey = findViewById(R.id.et_get_key)
        etDeleteKey = findViewById(R.id.et_delete_key)
        etCountValue = findViewById(R.id.et_count_value)
        tvOutputPortrait = findViewById(R.id.tv_output_portrait)
        tvOutputLandscape = findViewById(R.id.tv_output_landscape)
        tvOutputPortrait.movementMethod = ScrollingMovementMethod()
        tvOutputLandscape.movementMethod = ScrollingMovementMethod()
        setupListeners()
        setupOutput(transactionViewModel.getHistory())
        setupFlow()
    }

    private fun setupListeners() {
        btnSet.clickWithHide {
            transactionViewModel.onSetClickListener(
                etSetKey.text.toString(),
                etSetValue.text.toString()
            )
        }
        btnGet.clickWithHide { transactionViewModel.onGetClickListener(etGetKey.text.toString()) }
        btnDelete.clickWithHide { transactionViewModel.onDeleteClickListener(etDeleteKey.text.toString()) }
        btnCount.clickWithHide { transactionViewModel.onCountClickListener(etCountValue.text.toString()) }
        btnBegin.clickWithHide { transactionViewModel.onBeginClickListener() }
        btnCommit.clickWithHide { transactionViewModel.onCommitClickListener() }
        btnRollback.clickWithHide { transactionViewModel.onRollbackClickListener() }

    }

    private fun setupFlow() {
        lifecycleScope.launchWhenStarted {
            transactionViewModel.uiState.collectLatest {
                when (it) {
                    is SuccessUiState -> {
                        hideKeyboard()
                        appendData(it.data)
                    }
                    is ErrorUiState -> {
                        hideKeyboard()
                        Snackbar.make(btnRollback, it.data, Snackbar.LENGTH_LONG).show()
                    }
                    WaitingState -> {}
                }
            }
        }
    }

    private fun appendData(data: String) {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                tvOutputLandscape.append(data)
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                tvOutputPortrait.append(data)
            }
        }
    }

    private fun setupOutput(data: String) {
        when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                tvOutputPortrait.visibility = GONE
                tvOutputLandscape.visibility = VISIBLE
                tvOutputLandscape.text = data
            }
            Configuration.ORIENTATION_PORTRAIT -> {
                tvOutputPortrait.visibility = VISIBLE
                tvOutputLandscape.visibility = GONE
                tvOutputPortrait.text = data
            }
        }
    }

    private fun View.clickWithHide(listener: View.OnClickListener) {
        hideKeyboard()
        this.setOnClickListener(listener)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}