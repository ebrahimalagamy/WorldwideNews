package com.example.worldwidenews.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.worldwidenews.R
import com.example.worldwidenews.api.Article
import kotlinx.android.synthetic.main.item_article.view.*

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    // the recycler will always update whole items even the items that didn't change
    // to solve this problem use what is called DiffUtil this calculates the differences
    // between lists and enables us to only update those items
    // another adv is that is happen in background so we don't block our main thread
    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {

        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    // create async list differ so that the list differ is tool that will take our two list and
    // compares them and calculates the differences
    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(ivArticleImage)
            tvSource.text = article.source.name
            tvTitle.text = article.title
            tvDescription.text = article.description
            tvPublishedAt.text = article.publishedAt

            // it refer to our onItemClickListener lambda fun
            setOnClickListener{
                onItemClickListener?.let { it(article) }
            }
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    //to add itemClick to adapter that will open the article
    // lambda fun take article as a parameter and return nothing
    private var onItemClickListener: ((Article) -> Unit)? = null
    // to set onClickListener
    fun setOnItemClickListener(listener:(Article)->Unit){
        onItemClickListener = listener
    }
}