/**
 * ===================================================================================
 * <p>
 * <p>
 * <p>
 * <p>
 * ===================================================================================
 */
package com.osidocker.open.micro.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.osidocker.open.micro.entity.CodeImage;
import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @公司名称： 深圳原形信息技术有限公司
 * @类功能说明：
 * @类修改者： 曹杨杰
 * @创建日期： 创建于11:33 2017/4/19
 * @修改说明：
 * @修改日期： 修改于11:33 2017/4/19
 * @版本号： V1.0.0
 */
public class BarCodeFactory {

    /**
     * 图片格式定义
     * @value Array
     */
    private static String[] IMAGE_TYPE = {"BMP", "bmp", "jpg", "JPG", "wbmp", "jpeg", "png", "PNG", "JPEG", "WBMP", "GIF", "gif","ICON","icon"};

    /**
     * 二维码宽度
     */
    public static final int WIDTH = 260;

    /**
     * 二维码高度
     */
    public static final int HEIGHT = 260;

    /**
     * 图标宽度
     */
    private static final int IMAGE_WIDTH = 68;
    /**
     * 图标高度
     */
    private static final int IMAGE_HEIGHT = 68;
    /**
     * 底图大小【正方形】
     */
    private static final int IMAGE_HALF_WIDTH = IMAGE_WIDTH / 2;
    /**
     * 底图边框
     */
    private static final int FRAME_WIDTH = 5;

    /**
     * 二维码写码器
     */
    private static MultiFormatWriter mutiWriter = new MultiFormatWriter();

    /**
     * 二维码生成-方法入口
     * @param content 内容
     * @param width 宽度
     * @param height 高度
     * @param iconImagePath 图标原路径
     * @param qrcodeImagePath 二维码存放路径
     * @param hasFiller
     *             比例不对时是否需要补白：true为补白; false为不补白
     * @return
     *         成功：返回输出图片绝对路径；失败：返回null
     */
    public static String encode(String content, int width, int height,
                                String iconImagePath, String qrcodeImagePath,boolean hasFiller) {
        try {
            /**
             * 图标格式校验
             */
            File icon = new File(iconImagePath);
            if(!icon.exists()){
                System.err.println("系统找不到图标所在文件 ！");
                return null;
            }
            String iconFileName = icon.getName();
            // 得到上传文件的扩展名
            String fileExtName = iconFileName.substring(iconFileName.lastIndexOf(".") + 1);
            if(!checkIMGType(fileExtName)){
                System.err.println("图标格式有误 ！");
                return null;
            }

            if(width<260||height<260){
                width = BarCodeFactory.WIDTH;
                height = BarCodeFactory.HEIGHT;
            }
            ImageIO.write(genBarcode(content, width, height, iconImagePath,hasFiller),
                    "png", new File(qrcodeImagePath));
            System.err.println("二维码已生成  "+qrcodeImagePath);
            return qrcodeImagePath;
        } catch (IOException e) {
            System.err.println("图片读取异常 ： "+e.getMessage());
        } catch (WriterException e) {
            System.err.println("图片输出异常 ："+e.getMessage());
        }
        return null;
    }

    /**
     * 图片处理方法
     * @param content
     * @param width
     * @param height
     * @param iconImagePath
     * @param hasFiller
     *             比例不对时是否需要补白：true为补白; false为不补白;
     * @return
     * @throws WriterException
     * @throws IOException
     */
    private static BufferedImage genBarcode(String content, int width,
                                            int height, String iconImagePath,boolean hasFiller) throws WriterException,
            IOException {
        // 读取源图像
        BufferedImage scaleImage = scale(iconImagePath, IMAGE_WIDTH,
                IMAGE_HEIGHT, hasFiller);
        int[][] srcPixels = new int[IMAGE_WIDTH][IMAGE_HEIGHT];
        for (int i = 0; i < scaleImage.getWidth(); i++) {
            for (int j = 0; j < scaleImage.getHeight(); j++) {
                srcPixels[i][j] = scaleImage.getRGB(i, j);
            }
        }

        Map<EncodeHintType, Object> hint = new HashMap<EncodeHintType, Object>();
        hint.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        // 生成二维码
        BitMatrix matrix = mutiWriter.encode(content, BarcodeFormat.QR_CODE,
                width, height, hint);

        // 二维矩阵转为一维像素数组
        int halfW = matrix.getWidth() / 2;
        int halfH = matrix.getHeight() / 2;
        int[] pixels = new int[width * height];

        for (int y = 0; y < matrix.getHeight(); y++) {
            for (int x = 0; x < matrix.getWidth(); x++) {
                // 读取图片
                if (x > halfW - IMAGE_HALF_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH
                        && y < halfH + IMAGE_HALF_WIDTH) {
                    pixels[y * width + x] = srcPixels[x - halfW
                            + IMAGE_HALF_WIDTH][y - halfH + IMAGE_HALF_WIDTH];
                }
                // 在图片四周形成边框
                else if ((x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW - IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        + IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW + IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        + IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH - IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        - IMAGE_HALF_WIDTH + FRAME_WIDTH)
                        || (x > halfW - IMAGE_HALF_WIDTH - FRAME_WIDTH
                        && x < halfW + IMAGE_HALF_WIDTH + FRAME_WIDTH
                        && y > halfH + IMAGE_HALF_WIDTH - FRAME_WIDTH && y < halfH
                        + IMAGE_HALF_WIDTH + FRAME_WIDTH)) {
                    pixels[y * width + x] = 0xfffffff;
                } else {
                    // 此处可以修改二维码的颜色，可以分别制定二维码和背景的颜色；
                    pixels[y * width + x] = matrix.get(x, y) ? 0xff000000
                            : 0xfffffff;
                }
            }
        }

        BufferedImage image = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        image.getRaster().setDataElements(0, 0, width, height, pixels);

        return image;
    }

    /**
     * 把传入的原始图像按高度和宽度进行缩放，生成符合要求的图标
     *
     * @param iconImagePath
     *            小图标源文件地址
     * @param height
     *            目标高度
     * @param width
     *            目标宽度
     * @param hasFiller
     *            比例不对时是否需要补白：true为补白; false为不补白;
     * @throws IOException
     */
    private static BufferedImage scale(String iconImagePath, int height,
                                       int width, boolean hasFiller) throws IOException {
        double ratio = 0.0; // 缩放比例
        File file = new File(iconImagePath);
        BufferedImage srcImage = ImageIO.read(file);
        Image destImage = srcImage.getScaledInstance(width, height,
                BufferedImage.SCALE_SMOOTH);
        // 计算比例
        if ((srcImage.getHeight() > height) || (srcImage.getWidth() > width)) {
            if (srcImage.getHeight() > srcImage.getWidth()) {
                ratio = (new Integer(height)).doubleValue()
                        / srcImage.getHeight();
            } else {
                ratio = (new Integer(width)).doubleValue()
                        / srcImage.getWidth();
            }
            AffineTransformOp op = new AffineTransformOp(
                    AffineTransform.getScaleInstance(ratio, ratio), null);
            destImage = op.filter(srcImage, null);
        }
        if (hasFiller) {// 补白
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics2D graphic = image.createGraphics();
            graphic.setColor(Color.white);
            graphic.fillRect(0, 0, width, height);
            if (width == destImage.getWidth(null)) {
                graphic.drawImage(destImage, 0,
                        (height - destImage.getHeight(null)) / 2,
                        destImage.getWidth(null), destImage.getHeight(null),
                        Color.white, null);
            }else {
                graphic.drawImage(destImage,
                        (width - destImage.getWidth(null)) / 2, 0,
                        destImage.getWidth(null), destImage.getHeight(null),
                        Color.white, null);
            }
            graphic.dispose();
            destImage = image;
            System.err.println("INFO 图标补白已完成 ");
        }
        return (BufferedImage) destImage;
    }

    /**
     * 图片格式校验
     * @param fileExtName
     * @return
     */
    private static boolean checkIMGType(String fileExtName){
        boolean flag = false;
        for (String type : IMAGE_TYPE) {
            //-- 图片格式正确
            if(type.toLowerCase().equals(fileExtName.toLowerCase())){
                flag = true;
                break;
            }
        }
        //------------图片格式校验结束
        return flag;
    }

    /**
     * 解析二维码
     */
    public static String decode(String imagePath){
        // QRCode 二维码图片的文件
        File imageFile = new File(imagePath);
        BufferedImage bufImg = null;
        String content = null;
        try {
            bufImg = ImageIO.read(imageFile);
            QRCodeDecoder decoder = new QRCodeDecoder();
            content = new String(decoder.decode(new CodeImage(bufImg)), "utf-8");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (DecodingFailedException dfe) {
            System.out.println("Error: " + dfe.getMessage());
            dfe.printStackTrace();
        }
        return content;
    }

    /**
     * 测试主方法入口
     * @param args
     */
    public static void main(String[] args) {
        String iconPath = "G:/logo.png";
        String qrCodePath = "G:/"+System.currentTimeMillis()+".png";

        /**
         * 测试方法入口
         */
        BarCodeFactory.encode("http://www.yuancredit.com/",300, 300, iconPath, qrCodePath,false);
//        BarCodeFactory.createQRCode(new QrCodeEntity("http://www.yuancredit.com",qrCodePath, iconPath,300,300, 1));
        System.out.println(BarCodeFactory.decode(qrCodePath));

    }
}
