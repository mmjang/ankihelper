package com.mmjang.ankihelper.util.com.baidu.translate.demo;

import java.util.Random;
public class RandomAPIKeyGenerator {

    private static String[] APP_ID_AND_KEY_LIST = new String[] {
            "20181126000239193\nCPhOj0FHGhvt2f5x5kag",
            "20180208000121840\n6vcjzWbWq5Swqk8y_VQG",
            "20181125000239165\nXVCzhWeP3QLLzW7TXHGm",
            "20181125000239170\np4CI4cEngtYvRx12HUec",
            "20160220000012831\nISSPx0K_ZyrUN9IAOKel"
    };

    public static String[] next(){
        int min = 0;
        int max = APP_ID_AND_KEY_LIST.length - 1;
        int index = randInt(min, max);
        return APP_ID_AND_KEY_LIST[index].split("\n");
    }

    public static int randInt(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
