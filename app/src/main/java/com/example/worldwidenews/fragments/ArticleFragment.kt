package com.example.worldwidenews.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.worldwidenews.MainActivity
import com.example.worldwidenews.R
import com.example.worldwidenews.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_artical.*

class ArticleFragment : Fragment(R.layout.fragment_artical) {

    lateinit var viewModel: NewsViewModel
    val args: ArticleFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        // receive the article that we passed from our three different fragment
        val article = args.article

        webView.apply {
            // this will make the page always load in this webView don't load in the standard browser of the phone
            webViewClient = WebViewClient()
            loadUrl(article.url!!)
        }

        fab.setOnClickListener{
            // this article that was passed to this fragment as argument
            viewModel.saveArticle(article)
            Snackbar.make(view,"Article Saved",Snackbar.LENGTH_LONG).show()

        }

    }

}