package com.example.worldwidenews.util

// use to wrap around our network responses to differentiate between successful and
// and error responses and help us to handle success, error and loading state
// sealed class is one of abstract class but we can define which class would allowed to inherit from
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()


}