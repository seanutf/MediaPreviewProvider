package com.seanutf.mediapreviewprovider.core

import android.net.Uri
import android.provider.MediaStore
import com.seanutf.mediapreviewprovider.SelectMode
import com.seanutf.mediapreviewprovider.config.QueryConfig

/**
 * 媒体选择器中
 * 获取相册列表的参数提供者
 *
 * 与获取某个指定相册中的媒体列表
 * 代码相似
 * 但因为两者目的明显不同，
 * 所以为避免代码混乱和逻辑混淆
 * 将两者代码分开，方便理解
 * 如果一方代码配置有所改动
 * 记得检查另一方代码是否需要配合修改
 * */
class AlbumQueryConfigProvider {
    private var queryConfig: QueryConfig? = null

    private val orderBy: String = MediaStore.Files.FileColumns.DATE_ADDED + " DESC"

    fun setConfig(queryConfig: QueryConfig?) {
        this.queryConfig = queryConfig
    }

    fun getOrderBy(): String {
        return orderBy
    }

    fun getAlbumUri(): Uri {
        val uri: Uri
        when (queryConfig?.mode ?: SelectMode.IMG) {

            SelectMode.IMG -> {
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            SelectMode.VIDEO -> {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            SelectMode.ALL -> {
                uri = MediaStore.Files.getContentUri("external")
            }

            else -> {
                uri = MediaStore.Files.getContentUri("external")
            }
        }

        return uri
    }

    fun getAlbumProjection(): Array<String>? {
        val albumProjection: Array<String>?
        when (queryConfig?.mode ?: SelectMode.IMG) {

            SelectMode.IMG -> {
                albumProjection = getAlbumProjectionForImages()
            }

            SelectMode.VIDEO -> {
                albumProjection = getAlbumProjectionForVideos()
            }

            SelectMode.ALL -> {
                albumProjection = getAlbumProjectionForAllMedias()
            }

            else -> {
                albumProjection = getAlbumProjectionForAllMedias()
            }
        }

        return albumProjection
    }

    private fun getAlbumProjectionForImages(): Array<String>? {
        return null
        //使用下面注释的代码不能正确的返回结果值，待查
//        return if(RunTimeVersionUtil.isLargeApi29()){
//            arrayOf(
//                    MediaStore.Images.ImageColumns._ID,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE)
//        } else {
//            arrayOf(
//                    MediaStore.Images.ImageColumns._ID,
//                    MediaStore.MediaColumns.DATA,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE,
//                    "COUNT(*) AS " + "count")
//        }
    }

    private fun getAlbumProjectionForVideos(): Array<String>? {
        return null
        //使用下面注释的代码不能正确的返回结果值，待查
//        return if(RunTimeVersionUtil.isLargeApi29()){
//            arrayOf(
//                    MediaStore.Video.VideoColumns._ID,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE)
//        } else {
//            arrayOf(
//                    MediaStore.Video.VideoColumns._ID,
//                    MediaStore.MediaColumns.DATA,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE,
//                    "COUNT(*) AS " + "count")
//        }
    }

    private fun getAlbumProjectionForAllMedias(): Array<String>? {
        return null
//        return if(RunTimeVersionUtil.isLargeApi29()){
//            arrayOf(
//                    MediaStore.Files.FileColumns._ID,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE)
//        } else {
//            arrayOf(
//                    MediaStore.Files.FileColumns._ID,
//                    MediaStore.MediaColumns.DATA,
//                    "bucket_id",
//                    "bucket_display_name",
//                    MediaStore.MediaColumns.MIME_TYPE,
//                    "COUNT(*) AS " + "count")
//        }
    }

    fun getSelection(): String {
        val selection: String
        when (queryConfig?.mode ?: SelectMode.IMG) {

            SelectMode.IMG -> {
                selection = getSelectionForImages()
            }

            SelectMode.VIDEO -> {
                selection = getSelectionForVideos()
            }

            SelectMode.ALL -> {
                selection = getSelectionForAllMedias()
            }

            else -> {
                selection = getSelectionForAllMedias()
            }
        }

        return selection
    }

    private fun getSelectionForAllMedias(): String {
        return "(media_type=? AND (mime_type='image/png' OR mime_type='image/jpeg')) OR media_type=? AND (mime_type='video/mp4')"
    }

    private fun getSelectionForImages(): String {
        return (MediaStore.Images.Media.MIME_TYPE + "=? or "
                + MediaStore.Images.Media.MIME_TYPE + "=?")
    }

    private fun getSelectionForVideos(): String {
        return (MediaStore.Video.Media.MIME_TYPE + "=?")
    }

    fun getSelectionArgs(): Array<String> {
        val selectionArgs: Array<String>
        when (queryConfig?.mode ?: SelectMode.IMG) {

            SelectMode.IMG -> {
                selectionArgs = getSelectionArgsForImages()
            }

            SelectMode.VIDEO -> {
                selectionArgs = getSelectionArgsForVideos()
            }

            SelectMode.ALL -> {
                selectionArgs = getSelectionArgsForAllMedias()
            }

            else -> {
                selectionArgs = getSelectionArgsForAllMedias()
            }
        }

        return selectionArgs
    }

    /**
     * Gets a file of the specified type
     *
     * @return
     */
    private fun getSelectionArgsForAllMedias(): Array<String> {
        return arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
    }

    /**
     * Gets a file of the specified type
     *
     * @return
     */
    private fun getSelectionArgsForImages(): Array<String> {
        return arrayOf("image/png", "image/jpeg")
    }

    /**
     * Gets a file of the specified type
     *
     * @return
     */
    private fun getSelectionArgsForVideos(): Array<String> {
        return arrayOf("video/mp4")
    }

}