package com.example.worldwidenews.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.worldwidenews.MainActivity
import com.example.worldwidenews.R
import com.example.worldwidenews.adapters.NewsAdapter
import com.example.worldwidenews.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_saves_news.*

class SavedNewsFragment : Fragment(R.layout.fragment_saves_news) {
    lateinit var viewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = (activity as MainActivity).viewModel
        setupNewsAdapter()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
        }
        // to able to swipe to delete or edit we need implement item touch helper
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            // to specify the directions
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // we want get the position of the item that want to delete here
                val position = viewHolder.adapterPosition
                // to get article that want delete in our database
                val article = newsAdapter.differ.currentList[position]
                // now we know that article we want to delete
                viewModel.deleteArticle(article)
                // undo fun
                Snackbar.make(view, "Article Deleted", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }.show()
                }


            }

        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(rvSavedNews)
        }


        // to observe our changes on database in our saveNewsFragment
        // this observer will passes the new list of article whatever the database changes
        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            // update our recycler view
            // list differ will calculate the differences of the new list and old list and update it
            newsAdapter.differ.submitList(articles)

        })
    }

    private fun setupNewsAdapter() {
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}