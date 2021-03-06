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
package com.koma.video

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.koma.video.base.BaseAdapter
import com.koma.video.data.enities.VideoEntry
import com.koma.video.detail.DetailDialog
import com.koma.video.play.PlayActivity
import com.koma.video.util.GlideApp

class VideosAdapter(context: Context) :
    BaseAdapter<VideoEntry, VideosAdapter.VideosVH>(context = context) {
    override fun areItemsTheSame(oldItem: VideoEntry, newItem: VideoEntry): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: VideoEntry, newItem: VideoEntry): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: VideoEntry, newItem: VideoEntry): Any? {
        return null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = VideosVH(
        LayoutInflater.from(context).inflate(R.layout.video_item, parent, false)
    )

    override fun onBindViewHolder(holder: VideosVH, position: Int) {
        val videoEntry = getItem(position)

        holder.bindTo(videoEntry)
    }

    inner class VideosVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val image: ImageView
        private val duration: TextView
        private val name: TextView

        init {
            itemView.setOnClickListener(this)
            image = itemView.findViewById(R.id.iv_video)
            name = itemView.findViewById(R.id.tv_name)
            duration = itemView.findViewById(R.id.tv_duration)
            (itemView.findViewById(R.id.iv_more) as ImageView).setOnClickListener(this)
        }

        fun bindTo(entry: VideoEntry) {
            GlideApp.with(context)
                .asBitmap()
                .placeholder(ColorDrawable(Color.GRAY))
                .thumbnail(0.1f)
                .load(entry.uri)
                .into(image)

            name.text = entry.displayName

            duration.text = entry.formatDuration
        }


        override fun onClick(view: View) {
            when (view.id) {
                R.id.iv_more -> {
                    val popupMenu = PopupMenu(view.context, view)
                    popupMenu.menuInflater.inflate(R.menu.item_video_menu, popupMenu.menu)
                    popupMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.action_detail -> {
                                DetailDialog.show(
                                    (context as AppCompatActivity).supportFragmentManager,
                                    getItem(adapterPosition).id
                                )
                            }
                            R.id.action_delete -> {
                                data.removeAt(adapterPosition)
                                notifyItemRemoved(adapterPosition)
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }
                else -> {
                    val intent = Intent(context, PlayActivity::class.java)
                    intent.putExtra(PlayActivity.KEY_MEDIA_ID, getItem(adapterPosition).id)
                    context.startActivity(intent)
                }
            }
        }
    }
}