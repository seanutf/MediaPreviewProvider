package com.seanutf.mediapreviewprovider.core

import android.net.Uri
import android.provider.MediaStore
import com.seanutf.mediapreviewprovider.QueryMode
import com.seanutf.mediapreviewprovider.config.QueryConfig

class MediasQueryConfigProvider {
    private var queryConfig: QueryConfig? = null

    private val orderBy: String = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"

    fun setConfig(queryConfig: QueryConfig?) {
        this.queryConfig = queryConfig
    }

    fun getOrderBy(): String {
        return orderBy
    }

    fun getMediasUri(): Uri {
        val uri: Uri
        when (queryConfig?.mode ?: QueryMode.IMG) {

            QueryMode.IMG -> {
                uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            QueryMode.VIDEO -> {
                uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            }

            QueryMode.ALL -> {
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
        when (queryConfig?.mode ?: QueryMode.IMG) {

            QueryMode.IMG -> {
                albumProjection = getAlbumProjectionForImages()
            }

            QueryMode.VIDEO -> {
                albumProjection = getAlbumProjectionForVideos()
            }

            QueryMode.ALL -> {
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
            MediaStore.MediaColumns.DATE_ADDED
        )
        //??????????????????????????????????????????????????????????????????
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
            MediaStore.MediaColumns.DATE_ADDED
        )
        //??????????????????????????????????????????????????????????????????
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
            MediaStore.MediaColumns.DATE_ADDED
        )
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

    fun getSelection(bucketId: Long): String? {
        val selection: String?
        when (queryConfig?.mode ?: QueryMode.IMG) {

            QueryMode.IMG -> {
                selection = getSelectionForImages(bucketId)
            }

            QueryMode.VIDEO -> {
                selection = getSelectionForVideos(bucketId)
            }

            QueryMode.ALL -> {
                selection = getSelectionForAllMedias(bucketId)
            }

            else -> {
                selection = getSelectionForAllMedias(bucketId)
            }
        }

        return selection
    }

    private fun getSelectionForAllMedias(bucketId: Long): String {
        return if (bucketId == -1L) {
            "(media_type=?${buildSelectionImgMineTypesForAllMedias()} OR media_type=?${buildSelectionVideoMineTypesForAllMedias()})"
        } else if (bucketId == -2L) {
            "(media_type=?${buildSelectionVideoMineTypesForAllMedias()})"
        } else {
            "(media_type=?${buildSelectionImgMineTypesForAllMedias()} OR media_type=?${buildSelectionVideoMineTypesForAllMedias()}) AND bucket_id=?"
        }
    }

    private fun buildSelectionImgMineTypesForAllMedias() = buildString {
        if (queryConfig?.imgQueryFormatArray.isNullOrEmpty()) {
            ""
        } else {
            append(" AND ")
            append("(")
            for ((index, type) in (queryConfig?.imgQueryFormatArray ?: return@buildString).withIndex()) {
                append(MediaStore.Images.Media.MIME_TYPE + "=?")
                if (index != (queryConfig?.imgQueryFormatArray ?: return@buildString).size - 1) {
                    append(" OR ")
                }
            }
            append(")")
        }
    }

    private fun buildSelectionVideoMineTypesForAllMedias() = buildString {
        if (queryConfig?.videoQueryFormatArray.isNullOrEmpty()) {
            ""
        } else {
            append(" AND ")
            append("(")
            for ((index, type) in (queryConfig?.videoQueryFormatArray ?: return@buildString).withIndex()) {
                append(MediaStore.Video.Media.MIME_TYPE + "=?")
                if (index != (queryConfig?.videoQueryFormatArray ?: return@buildString).size - 1) {
                    append(" OR ")
                }
            }
            append(")")
        }
    }

    private fun getSelectionForImages(bucketId: Long): String? {
        return if (bucketId == -1L) {
            if (queryConfig?.imgQueryFormatArray.isNullOrEmpty()) {
                null
            } else {
                val builderStr = StringBuilder()
                for ((index, type) in ((queryConfig?.imgQueryFormatArray) ?: return null).withIndex()) {
                    builderStr.append(MediaStore.Images.Media.MIME_TYPE + "=?")
                    if (index != (queryConfig?.imgQueryFormatArray ?: return null).size - 1) {
                        builderStr.append(" OR ")
                    }
                }
                builderStr.toString()
            }
        } else {
            if (queryConfig?.imgQueryFormatArray.isNullOrEmpty()) {
                "bucket_id=?"
            } else {
                val builderStr = StringBuilder()
                for ((index, type) in (queryConfig?.imgQueryFormatArray ?: return null).withIndex()) {
                    builderStr.append(MediaStore.Images.Media.MIME_TYPE + "=?")
                    if (index != (queryConfig?.imgQueryFormatArray ?: return null).size - 1) {
                        builderStr.append(" OR ")
                    }
                }
                builderStr.append(" AND bucket_id=?")
                builderStr.toString()
            }
        }
    }

    private fun getSelectionForVideos(bucketId: Long): String? {
        return if (bucketId == -1L) {
            if (queryConfig?.videoQueryFormatArray.isNullOrEmpty()) {
                null
            } else {
                val builderStr = StringBuilder()
                for ((index, type) in ((queryConfig?.videoQueryFormatArray) ?: return null).withIndex()) {
                    builderStr.append(MediaStore.Video.Media.MIME_TYPE + "=?")
                    if (index != (queryConfig?.videoQueryFormatArray ?: return null).size - 1) {
                        builderStr.append(" OR ")
                    }
                }
                builderStr.toString()
            }
        } else {
            if (queryConfig?.videoQueryFormatArray.isNullOrEmpty()) {
                "bucket_id=?"
            } else {
                val builderStr = StringBuilder()
                for ((index, type) in (queryConfig?.videoQueryFormatArray ?: return null).withIndex()) {
                    builderStr.append(MediaStore.Video.Media.MIME_TYPE + "=?")
                    if (index != (queryConfig?.videoQueryFormatArray ?: return null).size - 1) {
                        builderStr.append(" OR ")
                    }
                }
                builderStr.append(" AND bucket_id=?")
                builderStr.toString()
            }
        }
    }

    fun getSelectionArgs(bucketId: Long): Array<String>? {
        val selectionArgs: Array<String>?
        when (queryConfig?.mode ?: QueryMode.IMG) {

            QueryMode.IMG -> {
                selectionArgs = getSelectionArgsForImages(bucketId)
            }

            QueryMode.VIDEO -> {
                selectionArgs = getSelectionArgsForVideos(bucketId)
            }

            QueryMode.ALL -> {
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
    private fun getSelectionArgsForAllMedias(bucketId: Long): Array<String>? {

        return if (bucketId == -1L) {
            //????????????????????????????????????
            val argsArr1 = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())

            val argsArr2 = if (!queryConfig?.imgQueryFormatArray.isNullOrEmpty()) {
                argsArr1.plus((queryConfig ?: return null).imgQueryFormatArray ?: return null)
            } else {
                argsArr1
            }

            val argsArr3 = argsArr2.plus(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())

            val finalArgsArr = if (!queryConfig?.videoQueryFormatArray.isNullOrEmpty()) {
                argsArr3.plus((queryConfig ?: return null).videoQueryFormatArray ?: return null)
            } else {
                argsArr3
            }

            finalArgsArr
        } else if (bucketId == -2L) {
            val argsArr1 = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
            val finalArgsArr = if (!queryConfig?.videoQueryFormatArray.isNullOrEmpty()) {
                argsArr1.plus((queryConfig ?: return null).videoQueryFormatArray ?: return null)
            } else {
                argsArr1
            }

            finalArgsArr
        } else {
            val argsArr1 = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())

            val argsArr2 = if (!queryConfig?.imgQueryFormatArray.isNullOrEmpty()) {
                argsArr1.plus((queryConfig ?: return null).imgQueryFormatArray ?: return null)
            } else {
                argsArr1
            }

            val argsArr3 = argsArr2.plus(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())

            val argsArr4 = if (!queryConfig?.videoQueryFormatArray.isNullOrEmpty()) {
                argsArr3.plus((queryConfig ?: return null).videoQueryFormatArray ?: return null)
            } else {
                argsArr3
            }
            val finalArr = argsArr4.plus(bucketId.toString())
            finalArr
        }
    }

    /**
     * ?????? ?????????????????? ????????????
     * bucketId == -1L ??????????????????
     * queryConfig?.imgQueryFormatArray?????????null?????????????????????????????????
     * ??????null????????????????????????
     * bucketId != -1L ???????????????
     * queryConfig?.imgQueryFormatArray?????????null?????????????????????
     * queryConfig?.imgQueryFormatArray????????????null?????????????????????
     *
     * @return ???????????????????????????????????????
     */
    private fun getSelectionArgsForImages(bucketId: Long): Array<String>? {
        return if (bucketId == -1L) {
            //????????????????????????????????????
            queryConfig?.imgQueryFormatArray
        } else {
            //????????????????????????
            val argsArr = if (queryConfig?.imgQueryFormatArray.isNullOrEmpty()) {
                arrayOf(bucketId.toString())
            } else {
                queryConfig?.imgQueryFormatArray?.plus(bucketId.toString())
            }
            argsArr
        }
    }

    /**
     * ?????? ?????????????????? ????????????
     * bucketId == -1L ??????????????????
     * queryConfig?.videoQueryFormatArray?????????null?????????????????????????????????
     * ??????null????????????????????????
     * bucketId != -1L ???????????????
     * queryConfig?.videoQueryFormatArray?????????null?????????????????????
     * queryConfig?.videoQueryFormatArray????????????null?????????????????????
     *
     * @return ???????????????????????????????????????
     */
    private fun getSelectionArgsForVideos(bucketId: Long): Array<String>? {
        return if (bucketId == -1L) {
            //????????????????????????????????????
            queryConfig?.videoQueryFormatArray
        } else {
            //????????????????????????
            val argsArr = if (queryConfig?.videoQueryFormatArray.isNullOrEmpty()) {
                arrayOf(bucketId.toString())
            } else {
                queryConfig?.videoQueryFormatArray?.plus(bucketId.toString())
            }
            argsArr
        }
    }

}