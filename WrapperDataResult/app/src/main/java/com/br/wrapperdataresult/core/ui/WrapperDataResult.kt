package com.br.wrapperdataresult.core.ui

class WrapperDataResult<T> {
}

data class DataResult<T>(
    val data: T? = null,
    val error: Throwable? = null,
    val dataResultState: DataResultState = DataResultState.Idle
)

sealed class DataResultState {
    object Idle : DataResultState()
    object Loading : DataResultState()
    object Success : DataResultState()
    data class Error(val throwable: Throwable) : DataResultState()
}