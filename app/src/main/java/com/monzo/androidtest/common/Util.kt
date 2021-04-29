package com.monzo.androidtest.common

import com.monzo.androidtest.domain.Article
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    add(disposable)
}