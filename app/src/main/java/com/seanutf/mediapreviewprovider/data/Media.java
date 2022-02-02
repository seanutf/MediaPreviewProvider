package com.seanutf.mediapreviewprovider.data;

import java.io.Serializable;

/**
 * 定义媒体文件(图片、视频)的基本类型
 * 本类中的字段大部分为客户端自定， 可用于客户端本地页面之间的数据传输
 * 子类可用于与后台进行数据交互使用
 * 子类中如有字段有本类字段有含义冲突，两个字段都要赋值
 */
public class Media implements Serializable {

    public static final int TYPE_MEDIA_IMAGE = 1;
    public static final int TYPE_MEDIA_VIDEO = 2;

    public int mediaType;    // 这个字段涉及api请求，名称和类型不要变，媒体类型 1图片 2视频

    //以下客户端自定义，主要用于选取媒体时所用
    public int mediaHeight;  //媒体文件的高度
    public int mediaWidth; //媒体文件的宽度
    public String mediaPath;//原始媒体文件的路径,
    public String processPath;//处理后的媒体文件的路径(裁剪，压缩)
    public long dateModified;  //媒体文件的修改时间
    public String artist;  //媒体文件的作者艺术家等信息
    public long size;//媒体文件的文件大小
    //public Uri mediaUri;//媒体文件的uri
    public long duration;//媒体文件的时长(视频)
    public String name;//媒体文件的名称

    public boolean isVideo() {
        return mediaType == TYPE_MEDIA_VIDEO;
    }
}
