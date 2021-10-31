package com.example.worldwidenews.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldwidenews.api.NewsResponse
import com.example.worldwidenews.database.NewsRepository
import com.example.worldwidenews.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

// this class take repository as a parameter
class NewsViewModel(
    val newsRepository: NewsRepository

) : ViewModel() {

    val breakingNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    // create one variable to manage pagination in this viewModel
    val breakingNewsPage = 1

    // for searchNews
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val searchNewsPage = 1

    init {
        getBreakingNews("us")
    }

    // create fun to execute our API call from repository
    // we use viewModelScope because in our repository this data is suspend fun
    // and use this type of coroutine because it stay alive as long as viewModel alive
    fun getBreakingNews(countryCode: String) = viewModelScope.launch {
        // to set loading state
        breakingNews.postValue(Resource.Loading())
        val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
        breakingNews.postValue(handleBreakingNewsResponse(response))

    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = newsRepository.searchNews(searchQuery, searchNewsPage)
        searchNews.postValue(handleSearchNewsResponse(response))
    }

    // to handle response
    private fun handleBreakingNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)

            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)

            }
        }
        return Resource.Error(response.message())
    }
}