package com.seanutf.mediapreviewprovider.core

import com.seanutf.mediapreviewprovider.config.DataConfig
import com.seanutf.mediapreviewprovider.data.Album
import com.seanutf.mediapreviewprovider.data.MFile


class MediaPreviewProvider {

    private val store = MediaPreviewStore()
    private val albumQuery = AlbumQueryConfigProvider()
    private val mediasQuery = MediasQueryConfigProvider()
    private var dataConfig: DataConfig? = null

    fun setConfig(dataConfig: DataConfig?) {
        this.dataConfig = dataConfig
        albumQuery.setConfig(dataConfig)
        mediasQuery.setConfig(dataConfig)
        store.setConfig(dataConfig)
    }


    fun loadAlbumList(): List<Album>? {
        if (dataConfig == null) {
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


    fun loadAlbumMedias(bucketId: Long): List<MFile>? {
        if (dataConfig == null) {
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

    fun loadAlbumMedias2(bucketId: Long, loadAlbum: Boolean): List<MFile>? {
        if (dataConfig == null) {
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