package com.mmjang.duckmemo.ui.news;

import android.graphics.Color;
import android.util.Base64;

public class CSSUtils {
    public static final String FONT_SERIF = "font-serif";
    public static final String FONT_SANS_SERIF = "font-sans-serif";
    public static final String FONT_GEORGIA = "font-georgia";
    public static final String FONT_CAECILIA = "font-caecilia";
    public static final String FONT_FARICYNEW = "font-faricynew";
    public static final String FONT_THEINHARDT = "font-theinhardt";

    public static final String SIZE1 = "size1";
    public static final String SIZE2 = "size2";
    public static final String SIZE3 = "size3";
    public static final String SIZE4 = "size4";
    public static final String SIZE5 = "size5";

    private static final String[] fontFamilyList = new String[]
            {FONT_SERIF, FONT_SANS_SERIF, FONT_GEORGIA, FONT_CAECILIA, FONT_FARICYNEW, FONT_THEINHARDT};
    private static final String[] fontSizeList = new String[] {SIZE1, SIZE2, SIZE3, SIZE4, SIZE5};

    public static String getClassFromIndex(int familyIndex, int sizeIndex){
        return fontFamilyList[familyIndex] + " " + fontSizeList[sizeIndex];
    }

    public static final String[] BackGroundColorList = new String[]{
            "#f4f4f4", "#fff1e5", "#deebcf"
    };

    public static int[] getIndexFromClass(String classString){
        String[] splitted = classString.split(" ");
        int familyIndex = 0;
        for(int i = 0; i < fontFamilyList.length; i ++){
            if(fontFamilyList[i].equals(splitted[0])){
                familyIndex = i;
                break;
            }
        }

        int sizeIndex = 0;
        for(int i = 0; i < fontSizeList.length; i ++){
            if(fontSizeList[i].equals(splitted[1])){
                sizeIndex = i;
                break;
            }
        }

        return new int[] {familyIndex, sizeIndex};
    }

    public static String[] getBackgroundColorList(){
        return BackGroundColorList;
    }
}
