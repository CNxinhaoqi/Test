package com.google.Zxing.test;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class ZxingTest {
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;

    public static void main(String[] args) {
        String filename="D:\\qrCodeImg.jpg";
        decode(filename);
    }
    /*
     * 解码：
     * 1 将图片反解码为二维矩阵
     * 2 将该二维矩阵解码为内容
     * */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void decode(String imgPath) {
        try {
            File file = new File(imgPath);//获取该图片文件
            BufferedImage image;
            try {
                image = ImageIO.read(file);
                if (image == null) {
                    System.out.println("Could not decode image");
                }
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                Result result;
                Hashtable hints = new Hashtable();//将图片反解码为二维矩阵
                hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
                result = new MultiFormatReader().decode(bitmap, hints);//将该二维矩阵解码成内容
                String resultStr = result.getText();
                System.out.println("解码结果："+resultStr);

            } catch (IOException ioe) {
                System.out.println(ioe.toString());
            } catch (ReaderException re) {
                System.out.println(re.toString());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * 编码
     * @author ChenGuiYong
     * @data 2015年7月13日 上午11:06:20
     * @param contents
     * @param width
     * @param height
     * @param imgPath
     * @param logoPath
     * @return
     */
    public static boolean encode(String contents, int width, int height, String imgPath,String logoPath) {
        Map<EncodeHintType, Object> hints = new Hashtable<>();
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        // 指定编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //设置二维码内容到边框的距离
        hints.put(EncodeHintType.MARGIN, 1);
        String format = "png";
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height, hints);

            MatrixToImageWriter.writeToStream(bitMatrix, format ,new FileOutputStream(imgPath));

            File qrcodeFile = new File(imgPath);
            //增加logo
            writeToLogo(bitMatrix,format , qrcodeFile, logoPath);
        } catch (Exception e) {
            return false;
        }
        System.out.println("编码成功！");
        return true;
    }
    /**
     * 增加Logo
     * @author ChenGuiYong
     * @data 2015年7月13日 上午11:07:00
     * @param matrix
     * @param format
     * @param file
     * @param logoPath
     * @throws IOException
     */
    public static void writeToLogo(BitMatrix matrix,String format,File file,String logoPath) throws IOException {
        Graphics2D graphics2 = null;
        BufferedImage image = null;
        BufferedImage logo = null;
        try {
            /**
             * 读取二维码图片，并构建绘图对象
             */
            image = toBufferedImage(matrix);
            graphics2 = image.createGraphics();

            /**
             * 读取Logo图片
             */
            logo = ImageIO.read(new File(logoPath));
            int codeWidth = image.getWidth();
            int codeHeight = image.getHeight();
            /**
             * 设置logo的大小,设置为二维码图片的25%,因为过大会盖掉二维码
             */
            int widthLogo = logo.getWidth(null)>codeWidth*2/13?(codeWidth*2/13):logo.getWidth(null),
                    heightLogo = logo.getHeight(null)>codeHeight*2/13?(codeHeight*2/13):logo.getWidth(null);

            /**
             * 计算图片放置位置
             * logo放在中心
             */
            int x = (codeWidth - widthLogo) / 2;
            int y = (codeHeight - heightLogo) / 2;
            int radius = 14;//圆角范围

            //填充与logo大小类似的扁平化圆角矩形背景
            graphics2.setComposite(AlphaComposite.Src);
            graphics2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2.setColor(Color.WHITE);
            graphics2.fill(new RoundRectangle2D.Float(x-2, y-2, widthLogo+4, heightLogo+4,radius,radius));
            graphics2.setComposite(AlphaComposite.SrcAtop);

            //开始绘制logo图片
            graphics2.drawImage(logo, x, y, widthLogo, heightLogo, null);

            if(!ImageIO.write(image, format, file)){
                throw new IOException("Could not write an image of format " + format + " to " + file);
            }
        } catch (Exception e) {
            throw e;
        }finally{
            if(graphics2!=null){
                graphics2.dispose();
            }
            if(logo!=null){
                logo.flush();
            }
            if(image!=null){
                image.flush();
            }
        }
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix){
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for(int x=0;x<width;x++){
            for(int y=0;y<height;y++){
                image.setRGB(x, y, matrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }
}
