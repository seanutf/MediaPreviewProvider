package com.seanutf.mediapreviewprovider.config

import com.seanutf.mediapreviewprovider.SelectMode
import com.seanutf.mediapreviewprovider.data.ImgFormat
import com.seanutf.mediapreviewprovider.data.VideoFormat


class QueryConfig {

    /**
     * 设置媒体的查找范围
     * 默认查找全部(包括图片和视频，暂不支持音频)
     * */
    var mode: SelectMode = SelectMode.ALL

    /**
     * 对查找图片的最小宽度要求
     * 只查找大于设置值的图片
     * 默认为-1，即对最小宽度无要求
     * */
    var imgMinWidth = -1

    /**
     * 对查找图片的最小高度要求
     * 只查找大于设置值的图片
     * 默认为-1，即对最小高度无要求
     * */
    var imgMinHeight = -1

    /**
     * 对查找图片的最大宽度要求
     * 只查找小于设置值的图片
     * 默认为-1，即对最大宽度无要求
     * */
    var imgMaxWidth = -1

    /**
     * 对查找图片的最大高度要求
     * 只查找小于设置值的图片
     * 默认为-1，即对最大高度无要求
     * */
    var imgMaxHeight = -1

    /**
     * 对查找图片的最小文件体积要求
     * 只查找大于设置值的图片
     * 默认为-1，即对最小文件体积无要求
     * */
    var imgMinSize = -1

    /**
     * 对查找图片的最大文件体积要求
     * 只查找小于设置值的图片
     * 默认为-1，即对最大文件体积无要求
     * */
    var imgMaxSize = -1

    /**
     * 对查找视频的最小宽度要求
     * 只查找大于设置值的视频
     * 默认为-1，即对最小宽度无要求
     * */
    var videoMinWidth = -1

    /**
     * 对查找视频的最小高度要求
     * 只查找大于设置值的视频
     * 默认为-1，即对最小高度无要求
     * */
    var videoMinHeight = -1

    /**
     * 对查找视频的最大宽度要求
     * 只查找小于设置值的视频
     * 默认为-1，即对最大宽度无要求
     * */
    var videoMaxWidth = -1

    /**
     * 对查找视频的最大高度要求
     * 只查找小于设置值的视频
     * 默认为-1，即对最大高度无要求
     * */
    var videoMaxHeight = -1

    /**
     * 对查找视频的最小文件体积要求
     * 只查找大于设置值的视频
     * 默认为-1，即对最小文件体积无要求
     * */
    var videoMinSize = -1

    /**
     * 对查找视频的最大文件体积要求
     * 只查找小于设置值的视频
     * 默认为-1，即对最大文件体积无要求
     * */
    var videoMaxSize = -1

    /**
     * 对查找图片的格式设置
     * 默认jpg和png格式
     * */
    var imgQueryFormatArray = arrayOf(ImgFormat.IMG_JPG, ImgFormat.IMG_PNG)

    /**
     * 对查找视频的格式设置
     * 默认mp4
     * */
    var videoQueryFormatArray = arrayOf(VideoFormat.VIDEO_MP4)

    /**
     * 设置查找时间范围的开始时间
     * 单位为毫秒
     * 默认为-1，即不限制开始时间
     * */
    var startTime = -1L

    /**
     * 设置查找时间范围的结束时间
     * 单位为毫秒
     * 默认为-1，即不限制结束时间
     * */
    var endTime = -1L

    /**
     * 设置媒体列表排序规则是否为创建时间
     * */
    var sortByCreate = true


    /**
     * 预留,支持分页加载
     * */
    var nextPageId = -1L
}