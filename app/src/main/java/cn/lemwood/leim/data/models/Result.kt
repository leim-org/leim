package cn.lemwood.leim.data.models

/**
 * 通用结果包装类
 * 用于处理异步操作的成功和失败状态
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    val isSuccess: Boolean
        get() = this is Success
    
    val isError: Boolean
        get() = this is Error
    
    val isLoading: Boolean
        get() = this is Loading
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun exceptionOrNull(): Throwable? = when (this) {
        is Error -> exception
        else -> null
    }
    
    inline fun onSuccess(action: (T) -> Unit): Result<T> {
        if (this is Success) action(data)
        return this
    }
    
    inline fun onError(action: (Throwable) -> Unit): Result<T> {
        if (this is Error) action(exception)
        return this
    }
    
    inline fun onLoading(action: () -> Unit): Result<T> {
        if (this is Loading) action()
        return this
    }
}