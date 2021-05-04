package com.monzo.androidtest.common

import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.chip.ChipGroup
import com.monzo.androidtest.R
import com.monzo.androidtest.databinding.ListItemSectionChipBinding
import com.monzo.androidtest.data.domain.Article
import com.monzo.androidtest.data.domain.SectionType
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
fun bindHttpText(textView: TextView, text: String?) {
    text?.let {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.text = Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            textView.text = Html.fromHtml(text);
        }
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

@BindingAdapter("sectionChips")
fun bindChipGroup(chipGroup: ChipGroup, listOfSections: List<SectionType>?) {
    listOfSections?.let { safeChips ->
        for (chip in safeChips) {
            val layoutInflater = LayoutInflater.from(chipGroup.context)
            val sectionChip = ListItemSectionChipBinding.inflate(layoutInflater, chipGroup, false)
            sectionChip.sectionType = chip
            chipGroup.addView(sectionChip.root)
        }
    }
}

@BindingAdapter("scrollListener")
fun bindNestedViewScrollView(nestedScrollView: NestedScrollView, listener: NestedScrollView.OnScrollChangeListener?) {
    listener?.let {
        nestedScrollView.setOnScrollChangeListener(it)
    }
}
