/*
 * Copyright 2018 koma_mj
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.koma.video.play

import com.koma.video.data.source.VideoRepository
import com.koma.video.util.LogUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class PlayPresenter @Inject constructor(
    private val view: PlayContract.View,
    private val repository: VideoRepository
) : PlayContract.Presenter {
    private val disposables: CompositeDisposable = CompositeDisposable()

    init {
        view.presenter = this
    }

    override fun subscribe() {
        LogUtils.i(TAG, "subscribe")
    }

    override fun unSubscribe() {
        LogUtils.i(TAG, "unSubscribe")

        disposables.clear()
    }

    override fun loadTitle(mediaId: Long) {
        val disposable = repository.getTitle(mediaId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    view.showTitle(it)
                },
                onError = {
                    LogUtils.e(TAG, "loadTitle error ${it.message}")
                }
            )
        disposables.add(disposable)
    }

    companion object {
        private const val TAG = "PlayPresenter"
    }
}