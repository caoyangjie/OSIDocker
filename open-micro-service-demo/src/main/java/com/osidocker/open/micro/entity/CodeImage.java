/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.entity;

import jp.sourceforge.qrcode.data.QRCodeImage;

import java.awt.image.BufferedImage;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于12:01 2017/4/19
 * @修改说明：
 * @修改日期： 修改于12:01 2017/4/19
 * @版本号： V1.0.0
 */
public class CodeImage implements QRCodeImage {
    private BufferedImage bufferedImage;

    public CodeImage(BufferedImage image){
        this.bufferedImage = image;
    }

    @Override
    public int getHeight() {
        return bufferedImage.getHeight();
    }

    @Override
    public int getPixel(int x, int y) {
        return bufferedImage.getRGB(x, y);
    }

    @Override
    public int getWidth() {
        return bufferedImage.getHeight();
    }
}
