package com.seanutf.mediapreviewprovider.core

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.seanutf.mediapreviewprovider.ProviderContext
import com.seanutf.mediapreviewprovider.SelectMode
import com.seanutf.mediapreviewprovider.config.DataConfig
import com.seanutf.mediapreviewprovider.data.Album
import com.seanutf.mediapreviewprovider.data.MFile
import com.seanutf.mediapreviewprovider.data.Media
import java.util.*

class MediaPreviewStore {

    private var dataConfig: DataConfig? = null
    private var allAlbumList: List<Album>? = null

    /**
     * 设置查找的数据参数
     *
     * [dataConfig] 查找数据的参数配置
     * */
    fun setConfig(dataConfig: DataConfig?) {
        this.dataConfig = dataConfig
    }

    /**
     * [mediaUri] 所查找媒体的类型
     * [projection] 所查找数据库的列
     * [selection] 所查找媒体的参数
     * [selectionArgs] 所查找媒体的参数的值
     * [sortOrder] 所查找媒体的排列规则
     * @return 媒体目录的列表，获取异常或失败时返回 null
     * */
    fun queryAlbum(
        mediaUri: Uri, projection: Array<String>?, selection: String, selectionArgs: Array<String>,
        sortOrder: String
    ): List<Album>? {

        var cursor: Cursor? = null
        try {
            cursor = ProviderContext.context.contentResolver.query(
                mediaUri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            return queryAlbum(cursor!!)
        } catch (e: Exception) {
            Log.e("MediaPreview", "loadMediaAlbums Data Error: " + e.message)
            return null
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    /**
     * 查询用户媒体库中
     * 所有的媒体文件目录
     *
     * @param cursor 游标
     * @return 媒体目录的列表
     * */
    @SuppressLint("Range")
    private fun queryAlbum(cursor: Cursor): List<Album> {
        val count = cursor.count
        var totalCount = 0
        var videoTotalCount = 0
        val mediaAlbums: MutableList<Album> = ArrayList<Album>()
        if (count > 0) {
            val countMap: MutableMap<Long, Long> = HashMap()
            while (cursor.moveToNext()) {
                val bucketId: Long = cursor.getLong(cursor.getColumnIndex("bucket_id"))
                val mimeType: String = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                if (dataConfig?.mode == SelectMode.ALL && !mimeType.startsWith("image")) {
                    videoTotalCount += 1
                }
                var newCount = countMap[bucketId]
                if (newCount == null) {
                    newCount = 1L
                } else {
                    newCount++
                }
                countMap[bucketId] = newCount
            }

            if (cursor.moveToFirst()) {
                val hashSet: MutableSet<Long> = HashSet()
                do {
                    //这里没有用MediaStore.MediaColumns.BUCKET_ID,是因为
                    //MediaStore.MediaColumns.BUCKET_ID这个父级常量值在 API 29才有
                    //而"bucket_id"之前就有，只不过处于子级
                    //在MediaStore.Images.Media.BUCKET_ID或MediaStore.Video.Media.BUCKET_ID中
                    //"bucket_display_name"同理

                    val bucketId: Long = cursor.getLong(cursor.getColumnIndex("bucket_id"))
                    if (hashSet.contains(bucketId)) {
                        continue
                    }
                    val album = Album()
                    album.bucketId = bucketId
                    val bucketDisplayName: String? = cursor.getString(
                        cursor.getColumnIndex("bucket_display_name")
                    )
                    val mimeType: String = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
                    val size = countMap[bucketId]!!
                    //val id: Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                    album.name = bucketDisplayName
                    album.count = size.toInt()
                    val url = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
                    album.firstImagePath = url
                    album.firstMimeType = mimeType
                    mediaAlbums.add(album)
                    hashSet.add(bucketId)
                    totalCount += size.toInt()
                } while (cursor.moveToNext())
            }

//            else {
//                cursor.moveToFirst()
//                do {
//                    val album = AlbumData()
//                    val bucketId = cursor.getLong(cursor.getColumnIndex("bucket_id"))
//                    val bucketDisplayName = cursor.getString(cursor.getColumnIndex("bucket_display_name"))
//                    val mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))
//                    val size = cursor.getInt(cursor.getColumnIndex("count"))
//                    album.bucketId = bucketId
//                    val url = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
//                    album.firstImagePath = url
//                    album.name = bucketDisplayName
//                    album.firstMimeType = mimeType
//                    album.count = size
//                    mediaAlbums.add(album)
//                    totalCount += size
//                    if(dataConfig?.mode == SelectMode.ALL && mimeType.contains("video")){
//                        videoTotalCount += size
//                    }
//                } while (cursor.moveToNext())
//            }


            when (dataConfig?.mode) {
                SelectMode.IMG -> {
                    // 所有图片文件夹
                    val allImageAlbum = generateAllImageAlbum(cursor, totalCount)
                    allImageAlbum.isChecked = true
                    allImageAlbum.isSelected = true
                    mediaAlbums.add(0, allImageAlbum)
                }

                SelectMode.VIDEO -> {
                    // 所有视频文件夹
                    val allVideoAlbum = generateAllVideoAlbum(cursor, totalCount)
                    allVideoAlbum.isChecked = true
                    allVideoAlbum.isSelected = true
                    mediaAlbums.add(0, allVideoAlbum)
                }

                SelectMode.ALL -> {
                    // 图片和视频文件夹
                    val allMediaAlbum = generateAllMediaAlbum(cursor, totalCount)
                    allMediaAlbum.isChecked = true
                    allMediaAlbum.isSelected = true
                    mediaAlbums.add(0, allMediaAlbum)

                    // 所有视频文件夹
                    val allVideoAlbum = generateAllVideoAlbum(cursor, videoTotalCount)
                    mediaAlbums.add(1, allVideoAlbum)
                }
            }

            return mediaAlbums
        }

        return ArrayList<Album>()
    }

    /**
     * 生成全部图片相册(包含全部的符合查询规则的图片媒体)
     * 即: "所有图片"
     * 其中的 bucketId = -1
     * 为本选择器默认的所有自定义文件夹的规则
     * 其值会在之后的获取某个指定文件夹的媒体列表中使用
     * 如果需要更改该规则，需要更改全部自定义文件夹
     *
     * @param cursor 游标
     * @param totalCount 全部图片的数量
     * @return 媒体目录数据结构
     * */
    private fun generateAllImageAlbum(cursor: Cursor, totalCount: Int): Album {
        val allImageAlbum = Album()
        allImageAlbum.count = totalCount
        allImageAlbum.bucketId = -1
        if (cursor.moveToFirst()) {
            allImageAlbum.firstImagePath = getFirstUrl(cursor)
            allImageAlbum.firstMimeType = getFirstCoverMimeType(cursor)
        }

        allImageAlbum.name = "所有图片"

        return allImageAlbum
    }

    /**
     * 生成全部视频相册(包含全部的符合查询规则的视频媒体)
     * 即: "所有视频"
     * 其中的 bucketId = -1
     * 为本选择器默认的所有自定义文件夹的规则
     * 其值会在之后的获取某个指定文件夹的媒体列表中使用
     * 如果需要更改该规则，需要更改全部自定义文件夹
     *
     * 特别注意：在查询为全部媒体时也会生成此文件夹
     * 为什么呢？猜测是：
     * 最初的的开发者开发媒体选择功能时
     * 仅知道 MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     * 和 MediaStore.Video.Media.EXTERNAL_CONTENT_URI
     * 不知道 MediaStore.Files.getContentUri("external")
     * 所以在通过相册查询相册内的媒体列表时，他只能
     * 二选一去查询，因为明显相册会更多一些
     * 所以通过相册查询相册内的媒体列表时，他选择了使用
     * MediaStore.Images.Media.EXTERNAL_CONTENT_URI
     * 这样就会把 同一个相册下的视频漏掉，没有展示
     * 所以就又单加一个全部视频的相册目录
     * 至于旧有选择媒体库中，全部列表中同时可以罗列
     * 图片和视频
     * 是因为开发者先获取全部视频再获取全部图片，
     * 再用自定义算法将两个列表按时间穿插排列组合
     * 具体可以查看旧有媒体选择库
     * 新的媒体选择库为了兼容用户体验
     * 保留了这一特性
     *
     * @param cursor 游标
     * @param videoTotalCount 全部视频的数量
     * @return 媒体目录数据结构
     * */
    private fun generateAllVideoAlbum(cursor: Cursor, videoTotalCount: Int): Album {
        val allVideoAlbum = Album()
        allVideoAlbum.count = videoTotalCount
        allVideoAlbum.bucketId = -1
        if (cursor.moveToFirst()) {
            allVideoAlbum.firstImagePath = getFirstUrl(cursor)
            allVideoAlbum.firstMimeType = getFirstCoverMimeType(cursor)
        }

        allVideoAlbum.name = "所有视频"

        return allVideoAlbum
    }

    /**
     * 生成全部媒体相册(包含全部的符合查询规则的图片、视频等媒体)
     * 即: "图片和视频"或 "相机胶卷"
     * 其中的 bucketId = -1
     * 为本选择器默认的所有自定义文件夹的规则
     * 其值会在之后的获取某个指定文件夹的媒体列表中使用
     * 如果需要更改该规则，需要更改全部自定义文件夹
     *
     * @param cursor 游标
     * @param totalCount 全部媒体的数量
     * @return 媒体目录数据结构
     * */
    private fun generateAllMediaAlbum(cursor: Cursor, totalCount: Int): Album {
        val allMediaAlbum = Album()
        allMediaAlbum.count = totalCount
        allMediaAlbum.bucketId = -1
        if (cursor.moveToFirst()) {
            allMediaAlbum.firstImagePath = getFirstUrl(cursor)
            allMediaAlbum.firstMimeType = getFirstCoverMimeType(cursor)
        }

        allMediaAlbum.name = "图片和视频"

        return allMediaAlbum
    }

    /**
     * 获取相册封面的真实地址
     * @param cursor 游标
     * @return 文件的真实地址的字符串
     */
    @SuppressLint("Range")
    private fun getFirstUrl(cursor: Cursor): String {
        return cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.DATA))
    }

    /**
     * 获取相册封面的文件扩展类型
     *
     * @param cursor 游标
     * @return 文件扩展类型的字符串
     */
    @SuppressLint("Range")
    private fun getFirstCoverMimeType(cursor: Cursor): String {
        return cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE))
    }

    /**
     * 针对 Android Q 以上的系统
     * 利用 MediaStore.Files.FileColumns._ID 获取的id
     * 生成真实的文件路径
     * 但测试发现并没有生成真实文件路径
     * 而是：Uri地址
     * 所以放弃使用
     * 暂留
     * id MediaStore.Files.FileColumns._ID 获取的id
     */
//    private fun getRealPath(id: Long): String {
//        return MediaStore.Files.getContentUri("external").buildUpon().appendPath(id.toString()).build().toString()
//    }


    /**
     * [mediaUri] 所查找媒体的类型
     * [projection] 所查找数据库的列
     * [selection] 所查找媒体的参数
     * [selectionArgs] 所查找媒体的参数的值
     * [sortOrder] 所查找媒体的排列规则
     * @return 媒体目录的列表，获取异常或失败时返回 null
     * */
    fun queryMedias(
        mediaUri: Uri,
        projection: Array<String>?,
        selection: String,
        selectionArgs: Array<String>,
        sortOrder: String
    ): MutableList<MFile>? {
        var cursor: Cursor? = null
        try {
            cursor = ProviderContext.context.contentResolver.query(
                mediaUri,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )
            return queryMediaList(cursor!!)
        } catch (e: Exception) {
            Log.e("MediaPreview", "loadMediaList Data Error: " + e.message)
            return null
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    @SuppressLint("Range")
    private fun queryMediaList(cursor: Cursor): MutableList<MFile> {
        val mediaItems: MutableList<MFile> = mutableListOf()

        while (cursor.moveToNext()) {
            val absolutePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)) ?: continue

            val mediaItem = MFile()
            mediaItem.mediaPath = absolutePath

            val mimeType: String = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))

            mediaItem.name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
            mediaItem.size = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))
            mediaItem.mediaWidth = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
            mediaItem.mediaHeight = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
            mediaItem.dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED))

            if (mimeType.contains("video")) {
                mediaItem.duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))
                mediaItem.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST))
            }


            if (mimeType.contains("video")) {
                mediaItem.mediaType = Media.TYPE_MEDIA_VIDEO
            } else if (mimeType.contains("image")) {
                mediaItem.mediaType = Media.TYPE_MEDIA_IMAGE
            }

            mediaItems.add(mediaItem)
        }

        return mediaItems
    }

    /**
     * [mediaUri] 所查找媒体的类型
     * [projection] 所查找数据库的列
     * [selection] 所查找媒体的参数
     * [selectionArgs] 所查找媒体的参数的值
     * [sortOrder] 所查找媒体的排列规则
     * @return 媒体目录的列表，获取异常或失败时返回 null
     * */
    fun queryMedias2(
        loadAlbum: Boolean,
        mediaUri: Uri,
        projection: Array<String>?,
        selection: String,
        selectionArgs: Array<String>,
        sortOrder: String
    ): MutableList<MFile>? {
        var cursor: Cursor? = null
        try {
            cursor = if (Build.VERSION.SDK_INT >= 30) {
                val queryArgs: Bundle = createQueryArgsBundle(selection, selectionArgs)
                ProviderContext.context.contentResolver.query(mediaUri, projection, queryArgs, null)
            } else {
                ProviderContext.context.contentResolver.query(
                    mediaUri,
                    projection,
                    selection,
                    selectionArgs,
                    sortOrder
                )
            }
            return queryMediaList2(cursor!!, loadAlbum)
        } catch (e: Exception) {
            Log.e("MediaPreview", "loadMediaList Data Error: " + e.message)
            return null
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
        }
    }

    /**
     * R  createQueryArgsBundle
     *
     * @param selection
     * @param selectionArgs
     * @param limitCount
     * @param offset
     * @return
     */
    private fun createQueryArgsBundle(selection: String, selectionArgs: Array<String>): Bundle {
        val queryArgs = Bundle()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, MediaStore.Files.FileColumns._ID + " DESC")
        }
        return queryArgs
    }

    /**
     * 查询用户媒体库中
     * 所有的媒体文件目录
     *
     * @param cursor 游标
     * @return 媒体目录的列表
     * */
    @SuppressLint("Range")
    private fun queryMediaList2(cursor: Cursor, loadAlbum: Boolean): MutableList<MFile> {

        val mediaItems: MutableList<MFile> = mutableListOf()
        val bucketIdMap: MutableMap<Long, Int> = mutableMapOf()
        var needGetVideoCover = true
        var videoTotalCount = 0
        //val retriever = MediaMetadataRetriever()

        val mediaAlbums: MutableList<Album> = ArrayList<Album>()
        val allVideoAlbum = Album()
        val allMediaAlbum = Album()
        val allImageAlbum = Album()


        while (cursor.moveToNext()) {
            val absolutePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)) ?: continue

            if (absolutePath.contains("_.thumbnails") || absolutePath.contains("thumb")) {
                continue
            }

            val bucketId: Long = cursor.getLong(cursor.getColumnIndex("bucket_id"))
            val bucketDisplayName: String? = cursor.getString(cursor.getColumnIndex("bucket_display_name"))
            val mimeType: String = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE))

            if (loadAlbum) {
                when (dataConfig?.mode ?: SelectMode.IMG) {

                    SelectMode.IMG -> {
                        allImageAlbum.bucketId = -1
                        if (cursor.isFirst) {
                            allImageAlbum.firstImagePath = getFirstUrl(cursor)
                            allImageAlbum.firstMimeType = getFirstCoverMimeType(cursor)
                            allImageAlbum.name = "所有图片"
                            mediaAlbums.add(0, allImageAlbum)
                            bucketIdMap[-1] = 1
                        }
                    }

                    SelectMode.VIDEO -> {
                        allVideoAlbum.bucketId = -1
                        if (cursor.isFirst) {
                            allVideoAlbum.firstImagePath = getFirstUrl(cursor)
                            allVideoAlbum.firstMimeType = getFirstCoverMimeType(cursor)
                            allVideoAlbum.name = "所有视频"
                            mediaAlbums.add(0, allVideoAlbum)
                            bucketIdMap[-1] = 1
                        }
                    }

                    SelectMode.ALL -> {
                        allMediaAlbum.bucketId = -1
                        allVideoAlbum.bucketId = -2

                        if (cursor.isFirst) {
                            allMediaAlbum.firstImagePath = getFirstUrl(cursor)
                            allMediaAlbum.firstMimeType = getFirstCoverMimeType(cursor)
                            allMediaAlbum.name = "图片和视频"
                            allVideoAlbum.name = "所有视频"
                            mediaAlbums.add(0, allMediaAlbum)
                            mediaAlbums.add(1, allVideoAlbum)
                            bucketIdMap[-1] = 1
                        }
                    }
                }

                if (!bucketIdMap.contains(bucketId)) {
                    bucketIdMap[bucketId] = 1

                    val album = Album()
                    album.bucketId = bucketId
                    //val id: Long = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID))
                    album.name = bucketDisplayName
                    album.firstImagePath = absolutePath
                    album.firstMimeType = mimeType
                    mediaAlbums.add(album)
                } else {
                    bucketIdMap[bucketId] = bucketIdMap[bucketId]?.plus(1) ?: 1
                }

                bucketIdMap[-1] = bucketIdMap[-1]?.plus(1) ?: 1

                if (dataConfig?.mode == SelectMode.ALL && mimeType.startsWith("video")) {
                    videoTotalCount += 1

                    if (mediaAlbums.size >= 2 && needGetVideoCover) {
                        needGetVideoCover = false
                        mediaAlbums[1].firstImagePath = getFirstUrl(cursor)
                        mediaAlbums[1].firstMimeType = getFirstCoverMimeType(cursor)
                    }
                }
            }

            val mediaItem = MFile()
            mediaItem.mediaPath = absolutePath
            mediaItem.name = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
            mediaItem.size = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.SIZE))
            mediaItem.mediaWidth = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH))
            mediaItem.mediaHeight = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT))
            mediaItem.dateModified = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED))

            if (mimeType.contains("video")) {
//                var tt1 :String? = null
//                try {
//                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
//                    val id = cursor.getLong(idColumn)
//                    val videoUri: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
//                    retriever.setDataSource(AppContext.context, videoUri)
//                    tt1 = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
//                    val aa = "fff"
//                    retriever.release()
//                } catch (e: RuntimeException) {
//                    Log.e("hgh", "Cannot retrieve video file", e)
//                }


                val tt = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                mediaItem.duration = tt
                mediaItem.artist = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.ARTIST))
            }


            if (mimeType.contains("video")) {
                mediaItem.mediaType = Media.TYPE_MEDIA_VIDEO
            } else if (mimeType.contains("image")) {
                mediaItem.mediaType = Media.TYPE_MEDIA_IMAGE
            }

            mediaItems.add(mediaItem)
        }


        if (loadAlbum) {
            for ((id, count) in bucketIdMap) {
                mediaAlbums.forEach {
                    if (it.bucketId == id) {
                        it.count = count
                    }
                    it.bucketId
                }
            }

            allAlbumList = mediaAlbums

            if (dataConfig?.mode == SelectMode.ALL && mediaAlbums.size >= 2) {
                mediaAlbums[1].count = videoTotalCount
            }
        }

        return mediaItems
    }

    fun getAlbumList(): List<Album>? {
        return allAlbumList
    }
}