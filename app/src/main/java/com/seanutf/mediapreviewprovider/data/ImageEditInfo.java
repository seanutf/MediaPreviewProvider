package com.seanutf.mediapreviewprovider.data;

import java.io.Serializable;

public class ImageEditInfo implements Serializable {
    public int imgWidth;
    public int imgHeight;

    ImageEditInfo(int imgWidth, int imgHeight){
        this.imgHeight = imgHeight;
        this.imgWidth = imgWidth;
    }

    public static ImageEditInfo getDefaultEditInfo(){
        return new ImageEditInfo(ImageEditConstant.DEFAULT_IMG_EDIT_WIDTH, ImageEditConstant.DEFAULT_IMG_EDIT_HEIGHT);
    }
}
