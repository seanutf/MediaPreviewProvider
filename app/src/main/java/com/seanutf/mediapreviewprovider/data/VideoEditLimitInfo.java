package com.seanutf.mediapreviewprovider.data;


import java.io.Serializable;

/**
 * 功能描述: 视频编辑进行编辑信息描述的数据类
 * 描述了一个被编辑的视频需要多高，多宽，什么帧率和最大时长
 * */
public class VideoEditLimitInfo implements Serializable {
    public int videoWith;
    public int videoHight;
    public int videoBitrate;
    public long videoMaxDuration;

    public VideoEditLimitInfo(int videoWith, int videoHight, long videoMaxDuration, int videoBitrate) {
        this.videoWith = videoWith;
        this.videoHight = videoHight;
        this.videoBitrate = videoBitrate;
        this.videoMaxDuration = videoMaxDuration;
    }

    public static VideoEditLimitInfo generateDefaultLimitInfo() {
        return new VideoEditLimitInfo(VideoEditConstant.DEFAULT_VIDEO_EDIT_WIDTH,
                VideoEditConstant.DEFAULT_VIDEO_EDIT_HEIGHT,
                VideoEditConstant.DEFAULT_VIDEO_EDIT_DURATION,
                VideoEditConstant.DEFAULT_VIDEO_EDIT_BITRATE);
    }
}
