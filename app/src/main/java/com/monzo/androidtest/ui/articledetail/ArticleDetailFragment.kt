package com.monzo.androidtest.ui.articledetail

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.monzo.androidtest.R
import com.monzo.androidtest.databinding.FragmentArticleDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleDetailFragment : Fragment(R.layout.fragment_article_detail) {

    private val viewModel: ArticleDetailViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupBinding(view)
    }

    private fun setupBinding(view: View) {
        arguments?.let { bundle ->
            val article = ArticleDetailFragmentArgs.fromBundle(bundle).article
            viewModel.setup(article)
            val binding = FragmentArticleDetailBinding.bind(view)
            binding.apply {
                articleDetailViewModel = viewModel
                executePendingBindings()
            }
        }!!
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.article_menu, menu)
        val favouritesItem = menu.findItem(R.id.select_favourites)
        viewModel.favouriteSelected.observe(viewLifecycleOwner, Observer { isFavourited ->
            if (isFavourited) {
                favouritesItem.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favourite)
            } else {
                favouritesItem.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_non_favourite)
            }
            favouritesItem.isChecked = isFavourited
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.select_favourites -> {
                item.isChecked = !item.isChecked
                if (item.isChecked) {
                    item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_favourite)
                } else {
                    item.icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_non_favourite)
                }
                viewModel.onArticleFavourited(item.isChecked)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}