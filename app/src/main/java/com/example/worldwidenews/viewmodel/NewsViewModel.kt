package com.example.worldwidenews.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.worldwidenews.api.Article
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
    var breakingNewsPage = 1

    // we want two load more pages but if rotate our device then the response will basically rest
    // and will show the first page again , we want to show all the news that already loaded
    // when we rotate this device and because the viewModel doesn't get destroyed on device rotation
    // we need to save the current response here
    // we make this response null because we don't know yet
    var breakingNewsResponse: NewsResponse? = null

    // for searchNews
    val searchNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponse: NewsResponse? = null

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
        // check if response success and the body noy equal null then increase the breakingNewsPage
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                breakingNewsPage++
                // check if breakingNewsResponse == null
                if (breakingNewsResponse == null) {
                    // if null then get the first response
                    breakingNewsResponse = resultResponse

                } else {// but if not the first response then take the old and new response and save with it
                    val oldArticles = breakingNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                searchNewsPage++
                if (searchNewsResponse == null) {
                    searchNewsResponse = resultResponse
                } else {
                    val oldArticles = searchNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // this fun to save article
    // we just took our fun from article dao and implemented them into viewModel and repository
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSaveNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

}