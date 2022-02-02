package com.seanutf.mediapreviewprovider.data;

/**
 * 可以选择的媒体文件
 */

public class MFile extends Media {
    public boolean isFirstSelect;  //用于媒体选择器判断是否第一次选择
    public boolean isSelect; //用于媒体选择器判断是否已被选择
}
