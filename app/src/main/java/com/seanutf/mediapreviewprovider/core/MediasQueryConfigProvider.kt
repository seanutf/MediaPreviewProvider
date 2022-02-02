package com.seanutf.mediapreviewprovider.core

import android.net.Uri
import android.provider.MediaStore
import com.seanutf.mediapreviewprovider.SelectMode
import com.seanutf.mediapreviewprovider.config.DataConfig

class MediasQueryConfigProvider {
    private var dataConfig: DataConfig? = null

    private val orderBy:String = MediaStore.Files.FileColumns.DATE_MODIFIED +" DESC"

    fun setConfig(dataConfig: DataConfig?) {
        this.dataConfig = dataConfig
    }

    fun getOrderBy(): String{
        return orderBy
    }

    fun getMediasUri(): Uri {
        val uri: Uri
        when(dataConfig?.mode ?: SelectMode.IMG){

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

    fun getMediasProjection(): Array<String>? {
        val albumProjection: Array<String>?
        when(dataConfig?.mode ?: SelectMode.IMG){

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
        //return null
        return arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.Video.Media.ARTIST,
                "bucket_id",
                MediaStore.MediaColumns.DATE_ADDED)
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
        //return null
        return arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.Video.Media.ARTIST,
                "bucket_id",
                MediaStore.MediaColumns.DATE_ADDED)
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
        //return null
        return arrayOf(
                MediaStore.Files.FileColumns._ID,
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT,
                MediaStore.Video.Media.DURATION,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.MediaColumns.DATE_MODIFIED,
                MediaStore.Video.Media.ARTIST,
                "bucket_id",
                MediaStore.MediaColumns.DATE_ADDED)
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

    fun getSelection(bucketId: Long): String {
        val selection: String
        when(dataConfig?.mode ?: SelectMode.IMG){

            SelectMode.IMG -> {
                selection = getSelectionForImages(bucketId)
            }

            SelectMode.VIDEO -> {
                selection = getSelectionForVideos(bucketId)
            }

            SelectMode.ALL -> {
                selection = getSelectionForAllMedias(bucketId)
            }

            else -> {
                selection = getSelectionForAllMedias(bucketId)
            }
        }

        return selection
    }

    private fun getSelectionForAllMedias(bucketId: Long): String {
        return if(bucketId == -1L){
            "(media_type=? AND (mime_type='image/png' OR mime_type='image/jpeg') OR media_type=? AND mime_type='video/mp4')"
        } else if (bucketId == -2L) {
            (MediaStore.Video.Media.MIME_TYPE + "=?")
        } else {
            "(media_type=? AND (mime_type='image/png' OR mime_type='image/jpeg') OR media_type=? AND mime_type='video/mp4') AND bucket_id=?"
        }
    }

    private fun getSelectionForImages(bucketId: Long): String {
        return if(bucketId == -1L){
            (MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=?")
        } else {
            (MediaStore.Images.Media.MIME_TYPE + "=? or "
                    + MediaStore.Images.Media.MIME_TYPE + "=? AND "
                    + "bucket_id=?")
        }

    }

    private fun getSelectionForVideos(bucketId: Long): String {
        return if(bucketId == -1L){
            (MediaStore.Video.Media.MIME_TYPE + "=?")
        } else {
            (MediaStore.Video.Media.MIME_TYPE + "=? AND" + "bucket_id=?")
        }
    }

    fun getSelectionArgs(bucketId: Long): Array<String> {
        val selectionArgs: Array<String>
        when(dataConfig?.mode ?: SelectMode.IMG){

            SelectMode.IMG -> {
                selectionArgs = getSelectionArgsForImages(bucketId)
            }

            SelectMode.VIDEO -> {
                selectionArgs = getSelectionArgsForVideos(bucketId)
            }

            SelectMode.ALL -> {
                selectionArgs = getSelectionArgsForAllMedias(bucketId)
            }

            else -> {
                selectionArgs = getSelectionArgsForAllMedias(bucketId)
            }
        }

        return selectionArgs
    }

    /**
     * Gets a file of the specified type
     *
     * @return
     */
    private fun getSelectionArgsForAllMedias(bucketId: Long): Array<String> {
        return if (bucketId == -1L) {
            arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
        } else if (bucketId == -2L) {
            arrayOf("video/mp4")
        } else {
            arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString(), bucketId.toString())
        }
    }

    /**
     * Gets a file of the specified type
     *
     * @return
     */
    private fun getSelectionArgsForImages(bucketId: Long): Array<String> {
        return if(bucketId == -1L) {
            arrayOf("image/png", "image/jpeg")
        } else {
            arrayOf("image/png", "image/jpeg", bucketId.toString())
        }
    }

    /**
     * Gets a file of the specified type
     *
     * @return
     */
    private fun getSelectionArgsForVideos(bucketId: Long): Array<String> {
        return if(bucketId == -1L) {
            arrayOf("video/mp4")
        } else {
            arrayOf("video/mp4", bucketId.toString())
        }
    }

}