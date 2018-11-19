package com.orcode.test;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import jp.sourceforge.qrcode.QRCodeDecoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swetake.util.Qrcode;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class QRCodeUtil {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    public static void main(String[] args) throws IOException {

//        User user=new User();
//        user.setUserid(1);
//        user.setAge(22);
//        user.setUsername("xxx");
//        createQRCode(user);
        //File file=new File("D:\\qrcode.png");
       // String filename="D://corrected.jpg";
        String filename="D:\\qrCodeImg.jpg";
        File file=new File(filename);
//        //读取原图像
//        Mat image = Imgcodecs.imread(filename);
//        //建立灰度图像存储空间
//        Mat gray = new Mat(image.rows(), image.cols(), CvType.CV_8UC1);
//        //彩色图像灰度化
//        Imgproc.cvtColor(image, gray, Imgproc.COLOR_RGB2GRAY);
//        Imgcodecs.imwrite(filename, gray);
        readQRCode(file);
    }

    public static void readQRCode(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        //声称能够解码指定格式
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        Iterator<ImageReader> ir = ImageIO.getImageReadersByFormatName(suffix);
        ImageReader imageReader = ir.next();
        ImageInputStream iis = ImageIO.createImageInputStream(fis);
        /**
         * 将它标记为 ‘只向前搜索'。
         * 此设置意味着包含在输入源中的图像将只按顺序读取
         * 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
         */
        imageReader.setInput(iis, true);
        //读取图片到缓冲区
        BufferedImage bufferedImage = imageReader.read(0);
        //QRCode解码器
        QRCodeDecoder codeDecoder = new QRCodeDecoder();
        //通过解析二维码获得信息
        String result = new String(codeDecoder.decode(new MyQRCodeImage(bufferedImage)), "utf-8");
        System.out.println(result);
        ObjectMapper mapper=new ObjectMapper();
        User user = mapper.readValue(result, User.class);
        System.out.println(user.toString());
    }
    /**
     * 生成二维码
     */
    public static void createQRCode(User user) throws IOException{
        //版本号
        int v =3;
        int width = 67 + 12 * (v - 1);
        int height = 67 + 12 * (v - 1);

        Qrcode x = new Qrcode();
        /**
         * 纠错等级分为
         * level L : 最大 7% 的错误能够被纠正；
         * level M : 最大 15% 的错误能够被纠正；
         * level Q : 最大 25% 的错误能够被纠正；
         * level H : 最大 30% 的错误能够被纠正；
         */
        x.setQrcodeErrorCorrect('L');
        x.setQrcodeEncodeMode('B');//注意版本信息 N代表数字 、A代表 a-z,A-Z、B代表 其他)
        x.setQrcodeVersion(v);//版本号  1-40
       
        ObjectMapper mapper=new ObjectMapper();
        String qrData =mapper.writeValueAsString(user) ;//内容信息

        byte[] d = qrData.getBytes("utf-8");//汉字转格式需要抛出异常

        //缓冲区
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);

        //绘图
        Graphics2D gs = bufferedImage.createGraphics();

        gs.setBackground(Color.WHITE);
        gs.setColor(Color.BLACK);
        gs.clearRect(0, 0, width, height);

        //偏移量
        int pixoff = 2;


        /**
         * 容易踩坑的地方
         * 1.注意for循环里面的i，j的顺序，
         *   s[j][i]二维数组的j，i的顺序要与这个方法中的 gs.fillRect(j*3+pixoff,i*3+pixoff, 3, 3);
         *   顺序匹配，否则会出现解析图片是一串数字
         * 2.注意此判断if (d.length > 0 && d.length < 120)
         *   是否会引起字符串长度大于120导致生成代码不执行，二维码空白
         *   根据自己的字符串大小来设置此配置
         */
        if (d.length > 0 && d.length < 120) {
            boolean[][] s = x.calQrcode(d);

            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < s.length; j++) {
                    if (s[j][i]) {
                        gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
                    }
                }
            }
        }
        gs.dispose();
        bufferedImage.flush();
        //设置图片格式，与输出的路径
        ImageIO.write(bufferedImage, "png", new File("D:/+"+user.getUserid()+".png"));
    }
    
    /**
     * 解析二维码
     */
    public static User readCode(File file) throws IOException{
        //读取图片到缓冲区
        BufferedImage bufferedImage = cutImage(file);
        //QRCode解码器
        QRCodeDecoder codeDecoder = new QRCodeDecoder();
        //通过解析二维码获得信息
        String result = new String(codeDecoder.decode(new MyQRCodeImage(bufferedImage)), "utf-8");
        System.out.println(result);
        ObjectMapper mapper=new ObjectMapper();
        User user = mapper.readValue(result, User.class);
        ImageIO.write(bufferedImage, "png", new File("D:/"+user.getUserid()+".png"));
        return user;
    }
    
    /**
     * 截取图片中二维码部分
     */
    public static BufferedImage cutImage(File file) throws IOException{
        FileInputStream fis = new FileInputStream(file);
        //声称能够解码指定格式    
        String fileName = file.getName();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        Iterator<ImageReader> ir = ImageIO.getImageReadersByFormatName(suffix);
        ImageReader imageReader = ir.next();
        //获取图片流
        ImageInputStream iis = ImageIO.createImageInputStream(fis);
        /**
         * 将它标记为 ‘只向前搜索'。 
         * 此设置意味着包含在输入源中的图像将只按顺序读取
         * 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。 
         */
        imageReader.setInput(iis, true);
        ImageReadParam param = imageReader.getDefaultReadParam();
        /**
         * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象 
         * 的左上顶点的坐标（x，y）、宽度和高度可以定义这个区域。
         */
        //Rectangle rect = new Rectangle(40, 80, 120, 120);
        //Rectangle rect = new Rectangle(40, 80, 200, 200);
        Rectangle rect = new Rectangle(40, 90, 280, 280);
        param.setSourceRegion(rect);
        BufferedImage read = imageReader.read(0, param);
        return read;
    }

}
