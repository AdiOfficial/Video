package com.koma.video.data.source

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.koma.video.data.enities.BucketEntry
import com.koma.video.data.enities.VideoEntry
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoDataSource @Inject constructor(private val context: Context) : IVideoDataSource {
    private val resolver: ContentResolver by lazy<ContentResolver> {
        context.contentResolver
    }

    override fun getVideoEntries(): Flowable<List<VideoEntry>> {
        return Flowable.create({
            val entries = ArrayList<VideoEntry>()
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION
            )
            val cursor =
                resolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    SORT_ORDER
                )
            cursor?.use {
                it.moveToFirst()
                do {
                    val entry = VideoEntry(it.getLong(0), it.getString(1), it.getInt(2))
                    entries.add(entry)
                } while (it.moveToNext())
            }
            it.onNext(entries)
            it.onComplete()
        }, BackpressureStrategy.LATEST)
    }

    override fun getBucketEntries(): Flowable<List<BucketEntry>> {
        return Flowable.create({
            val entries = ArrayList<BucketEntry>()
            val projection = arrayOf(
                MediaStore.Video.VideoColumns.BUCKET_ID,
                MediaStore.Video.VideoColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATE_TAKEN
            )
            val selection = "1) GROUP BY (1"
            val cursor = resolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection,
                selection, null, null
            )
            cursor?.use {
                it.moveToFirst()
                do {
                    val bucketId = it.getInt(0)
                    val dateTaken = it.getInt(2)
                    val entry = BucketEntry(bucketId, it.getString(1))
                    entry.dateTaken = dateTaken
                    entries.add(entry)
                    val cur = resolver.query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        arrayOf(MediaStore.Video.Media._ID),
                        MediaStore.Video.Media.BUCKET_ID + " = ?",
                        arrayOf(bucketId.toString()), SORT_ORDER
                    )
                    cur?.use {
                        cur.moveToFirst()
                        entry.uri = ContentUris.withAppendedId(
                            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            cur.getLong(0)
                        )
                        entry.count = cur.count
                    }
                } while (it.moveToNext())
            }
            it.onNext(entries)
            it.onComplete()
        }, BackpressureStrategy.LATEST)
    }

    override fun getVideoEntries(bucketId: Int): Flowable<List<VideoEntry>> {
        return Flowable.create({
            val entries = ArrayList<VideoEntry>()
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION
            )
            val selection = MediaStore.Video.Media.BUCKET_ID + " = ?"
            val cursor =
                resolver.query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    arrayOf(bucketId.toString()),
                    SORT_ORDER
                )
            cursor?.use {
                it.moveToFirst()
                do {
                    val entry = VideoEntry(it.getLong(0), it.getString(1), it.getInt(2))
                    entries.add(entry)
                } while (it.moveToNext())
            }
            it.onNext(entries)
            it.onComplete()
        }, BackpressureStrategy.LATEST)
    }

    companion object {
        private const val TAG = "VideoDataSource"
        private const val SORT_ORDER = MediaStore.Video.Media.DATE_TAKEN + " DESC"
    }
}