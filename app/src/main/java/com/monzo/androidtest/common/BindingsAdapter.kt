package com.monzo.androidtest.common

import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.monzo.androidtest.R
import com.monzo.androidtest.domain.Article
import com.monzo.androidtest.ui.articlelists.ArticleAdapter

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Article>?) {
    val adapter = recyclerView.adapter as ArticleAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageURL")
fun bindImage(imageView: ImageView, imageUrl: String?) {
    imageUrl?.let {
        val imageUri = Uri.parse(imageUrl).buildUpon().scheme("https").build()
        Glide.with(imageView.context).load(imageUri).into(imageView)
    }
}

@BindingAdapter("favouriteArticle")
fun bindFavouriteArticle(imageView: ImageView, isFavourite: Boolean) {
    if (isFavourite) {
        imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context, R.drawable.ic_favourite))
    } else {
        imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context, R.drawable.ic_non_favourite))
    }
}

@BindingAdapter("httpText")
fun bindHttpText(textView: TextView, text: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
    } else {
        textView.text = Html.fromHtml(text);
    }
}

@BindingAdapter("shouldShow")
fun bindArticleContainer(linearLayout: LinearLayout, articles: List<Article>?) {
    articles?.let {
        if (articles.isEmpty()) {
            linearLayout.visibility = View.GONE
        } else {
            linearLayout.visibility = View.VISIBLE
        }
    }
}
