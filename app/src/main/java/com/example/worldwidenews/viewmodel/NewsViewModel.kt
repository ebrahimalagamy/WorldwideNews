package com.example.worldwidenews.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.worldwidenews.NewsApplication
import com.example.worldwidenews.api.Article
import com.example.worldwidenews.api.NewsResponse
import com.example.worldwidenews.database.NewsRepository
import com.example.worldwidenews.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

// this class take repository as a parameter
class NewsViewModel(
    app: Application,
    val newsRepository: NewsRepository
// we inherit from AndroidViewModel not ViewModel because we can use application context in AndroidViewModel
) : AndroidViewModel(app) {

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
        safeBreakingNewsCall(countryCode)

    }

    fun searchNews(searchQuery: String) = viewModelScope.launch {
        safeSearchNewsCall(searchQuery)
    }

    // this fun to safe breakingNews call and searchNews call
    // and catch all possible exceptions
    private suspend fun safeBreakingNewsCall(countryCode: String) {
        breakingNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.getBreakingNews(countryCode, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponse(response))
            } else {
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }


        } catch (t: Throwable) {
            when (t) {
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }

        }

    }

    private suspend fun safeSearchNewsCall(searchQuery: String) {
        searchNews.postValue(Resource.Loading())
        try {
            if (hasInternetConnection()) {
                val response = newsRepository.searchNews(searchQuery, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponse(response))
            } else {
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }


        } catch (t: Throwable) {
            when (t) {
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }

        }

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

    // this fun to know to internet state
    // this fun need connectivity manager this a system service that we request the context but we
    // can't call this inside viewModel and we need context for this and we can't implement this fun
    // inside main to solve this problem
    private fun hasInternetConnection(): Boolean {
        // getApplication this fun available only fom androidViewModel
        // this will detect if the user is currently connect to the internet or not
        // getSystemService this return an object that could be anything we want cast this to ConnectivityManager
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        // first we need check API level
        // if we user API above 23 we use this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // if activeNetwork == null then return false
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            // with this capabilities we can check different type of network and check
            // if are available or not
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false

            }

        } else {
            // if API below 23
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_ETHERNET -> true
                    TYPE_MOBILE -> true
                    else -> false
                }

            }
        }

        return false
    }

}