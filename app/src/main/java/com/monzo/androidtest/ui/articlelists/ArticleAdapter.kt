package com.monzo.androidtest.ui.articlelists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.monzo.androidtest.databinding.ListItemArticleBinding
import com.monzo.androidtest.domain.Article

internal class ArticleAdapter(private val articleOnClickListener: ArticleOnClickListener) : ListAdapter<Article, ArticleAdapter.ArticleViewHolder>(ArticleDiffChecker()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ArticleViewHolder(ListItemArticleBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ArticleViewHolder(private val binding: ListItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                articleTitle.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val article = getItem(position)
                        articleOnClickListener.articleClicked(article)
                    }
                }
                articleImage.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val article = getItem(position)
                        articleOnClickListener.articleClicked(article)
                    }
                }
                articleFavouriteIcon.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val article = getItem(position)
                        articleOnClickListener.articleFavourited(article, !article.favourite!!)
                    }
                }
            }
        }

        fun bind(article: Article) {
            binding.articleProperty = article
            binding.executePendingBindings()
        }
    }

    interface ArticleOnClickListener {
        fun articleFavourited(article: Article, isFavourite: Boolean)
        fun articleClicked(article: Article) {}
    }

    class ArticleDiffChecker() : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean = oldItem == newItem
    }

}

