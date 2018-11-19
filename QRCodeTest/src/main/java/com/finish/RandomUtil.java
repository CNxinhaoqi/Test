package com.finish;

import java.util.Random;
import java.util.UUID;

public class RandomUtil {

    public static Random random=new Random();

    public static String generateUserName(){
        char[] zifus=new char[3];
        for (int i = 0; i <3 ; i++) {
            int num = random.nextInt(26) + 65;
            char zifu= (char) num;
            zifus[i]=zifu;
        }
        return String.copyValueOf(zifus);
    }

    public static String generateUserId(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("\\-", "");
    }

    public static int generateUserAge(){
       return random.nextInt(80);
    }

}
