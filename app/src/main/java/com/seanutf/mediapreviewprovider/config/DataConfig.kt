package com.seanutf.mediapreviewprovider.config

import com.seanutf.mediapreviewprovider.SelectMode
import com.seanutf.mediapreviewprovider.data.MFile
import java.io.Serializable


class DataConfig : Serializable {
    var mode: SelectMode = SelectMode.ALL
    var maxNumInThisTime: Int = 1
    var selectListInLastTime: MutableList<MFile>? = null
    var resultCode: Int = 10500
}