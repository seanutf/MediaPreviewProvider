package com.seanutf.mediapreviewprovider.core

import com.seanutf.mediapreviewprovider.config.QueryConfig
import com.seanutf.mediapreviewprovider.data.Album
import com.seanutf.mediapreviewprovider.data.Media


class MediaPreviewProvider {

    private val store = MediaPreviewStore()
    private val albumQuery = AlbumQueryConfigProvider()
    private val mediasQuery = MediasQueryConfigProvider()
    private var queryConfig: QueryConfig? = null

    fun setConfig(queryConfig: QueryConfig?) {
        this.queryConfig = queryConfig
        albumQuery.setConfig(queryConfig)
        mediasQuery.setConfig(queryConfig)
        store.setConfig(queryConfig)
    }


    fun loadAlbumList(): List<Album>? {
        if (queryConfig == null) {
            return null
        }
        return store.queryAlbum(
            albumQuery.getAlbumUri(),
            albumQuery.getAlbumProjection(),
            albumQuery.getSelection(),
            albumQuery.getSelectionArgs(),
            albumQuery.getOrderBy()
        )
    }


    fun loadAlbumMedias(bucketId: Long): List<Media>? {
        if (queryConfig == null) {
            return null
        }
        return store.queryMedias(
            mediasQuery.getMediasUri(),
            mediasQuery.getMediasProjection(),
            mediasQuery.getSelection(bucketId),
            mediasQuery.getSelectionArgs(bucketId),
            mediasQuery.getOrderBy()
        )
    }

    fun loadAlbumMedias2(bucketId: Long, loadAlbum: Boolean): List<Media>? {
        if (queryConfig == null) {
            return null
        }
        return store.queryMedias2(
            loadAlbum,
            mediasQuery.getMediasUri(),
            mediasQuery.getMediasProjection(),
            mediasQuery.getSelection(bucketId),
            mediasQuery.getSelectionArgs(bucketId),
            mediasQuery.getOrderBy()
        )
    }

    fun getAlbumList(): List<Album>? {
        return store.getAlbumList()
    }
}