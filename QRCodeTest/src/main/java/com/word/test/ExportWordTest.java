package com.word.test;



import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.codec.Base64;
import com.lowagie.text.rtf.RtfWriter2;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportWordTest {
    public static void main(String[] args) throws IOException, DocumentException {
        exportDoc();
    }
    /**
     *
     * @Description: 将网页内容导出为word
     * @param @param file
     * @param @throws DocumentException
     * @param @throws IOException 设定文件
     * @return void 返回类型
     * @throws
     */
    public static String exportDoc() throws DocumentException, IOException {
        // 设置纸张大小
        Document document = new Document(PageSize.A4);
        // 建立一个书写器(Writer)与document对象关联，通过书写器(Writer)可以将文档写入到磁盘中
        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
        File file = new File("D://report.pdf");
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        // 设置中文字体
//        BaseFont bfChinese = BaseFont.createFont("STSongStd-Light",
//                "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
//        // 标题字体风格
//        Font titleFont = new Font(bfChinese, 12, Font.BOLD);
//        // // 正文字体风格
//        Font contextFont = new Font(bfChinese, 10, Font.NORMAL);
        Paragraph title = new Paragraph("统计报告");
        // 设置标题格式对齐方式
        title.setAlignment(Element.ALIGN_CENTER);
        // title.setFont(titleFont);
        document.add(title);
        String contextString = "iText是一个能够快速产生PDF文件的java类库。"
                + " \n"// 换行 + "iText的java类对于那些要产生包含文本，"
                + "表格，图形的只读文档是很有用的。它的类库尤其与java Servlet有很好的给合。"
                + "使用iText与PDF能够使你正确的控制Servlet的输出。";
        Paragraph context = new Paragraph(contextString);
        // 正文格式左对齐
        context.setAlignment(Element.ALIGN_LEFT);
        // context.setFont(contextFont);
        // 离上一段落（标题）空的行数
        context.setSpacingBefore(5);
        // 设置第一行空的列数
        context.setFirstLineIndent(20);
        document.add(context);
        // // 利用类FontFactory结合Font和Color可以设置各种各样字体样式
        // Paragraph underline = new Paragraph("下划线的实现", FontFactory.getFont(
        // FontFactory.HELVETICA_BOLDOBLIQUE, 18, Font.UNDERLINE,
        // new Color(0, 0, 255)));
        // document.add(underline);
        //
        // // 添加图片 Image.getInstance即可以放路径又可以放二进制字节流
        //
        Image img = Image.getInstance("D:\\qrcode.png");
        img.setAbsolutePosition(0, 0);
        img.setAlignment(Image.LEFT);// 设置图片显示位置
        // img.scaleAbsolute(60, 60);// 直接设定显示尺寸
        //
        // // img.scalePercent(50);//表示显示的大小为原尺寸的50%
        //
        // // img.scalePercent(25, 12);//图像高宽的显示比例
        //
        // // img.setRotation(30);//图像旋转一定角度
        //
        document.add(img);
        document.close();
        // 得到输入流
        // wordFile = new ByteArrayInputStream(baos.toByteArray());
        // baos.close();
        pdf2png(file,"jpg");
        return "";


    }

    public static void pdf2png(File file,String type) {
        // 将pdf装图片 并且自定义图片得格式大小
        try {
            PDDocument doc = PDDocument.load(file);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 144); // Windows native DPI
                // BufferedImage srcImage = resize(image, 240, 240);//产生缩略图
                ImageIO.write(image, type, new File(file.getParent()+"\\"+file.getName()+"."+type));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
