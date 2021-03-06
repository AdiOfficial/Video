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
package com.koma.video.folder.detail

import com.koma.video.base.BasePresenter
import com.koma.video.base.BaseView
import com.koma.video.data.enities.VideoEntry

interface FolderDetailContract {
    interface View : BaseView<Presenter> {
        var isActive: Boolean

        fun setLoadingIndicator(active: Boolean)

        fun showVideoEntries(entries: List<VideoEntry>)

        fun setEmptyIndicator(active: Boolean)
    }

    interface Presenter : BasePresenter {
        fun loadVideoEntries(bucketId: Int)
    }
}