package com.seanutf.mediapreviewprovider.config

import com.seanutf.mediapreviewprovider.data.ImageEditInfo
import com.seanutf.mediapreviewprovider.data.VideoEditLimitInfo
import java.io.Serializable

class InteractionConfig : Serializable {

    var videoEditInfo: VideoEditLimitInfo = VideoEditLimitInfo.generateDefaultLimitInfo()
    var imgEditInfo: ImageEditInfo = ImageEditInfo.getDefaultEditInfo()
    var isNeedImageEdit = false
    var isNeedVideoEdit = false
    var forCustomerService = false
    var videoEditedSavePath: String? = null
    var needPreview = false
    var immediatelyJumpForSingleImg = false
    var backCheckToNotice = false
    var reverseSelect = false
}