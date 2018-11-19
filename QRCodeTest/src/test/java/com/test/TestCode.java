package com.test;

import com.finish.ImageUtils;
import com.finish.PDFUtils;
import com.finish.QRCodeUtils;
import com.finish.RandomUtil;
import com.lowagie.text.DocumentException;
import com.orcode.test.User;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestCode {

    @Test
    public void generateCode() throws IOException {
        for (int i = 0; i <100 ; i++) {
            User user=new User();
            user.setUsername(RandomUtil.generateUserName());
            user.setAge(RandomUtil.generateUserAge());
            user.setUserid(RandomUtil.generateUserId());
            QRCodeUtils.createQRCode(user);
        }
    }

    @Test
    public void export() throws IOException, DocumentException {
        PDFUtils.exportPDFFiles(new File("D:/QRCodes"));
    }

    @Test
    public void cutImages(){
        ImageUtils.CutImages(new File("D:/PDFImages"));
    }

    @Test
    public void readCutImage() throws IOException {
        File file = new File("D:/CutImages");
        List<String> fileUrls = PDFUtils.getFileUrls(file);
        for (String fileUrl:fileUrls) {
            String substring = fileUrl.substring(13, fileUrl.length() - 4);
            QRCodeUtils.readQRCode(new File("D:/CutImages/"+substring+".jpg"));
        }
    }


    @Test
    public void testCamera(){

    }



}
