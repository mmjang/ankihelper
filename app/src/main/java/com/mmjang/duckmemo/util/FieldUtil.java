package com.mmjang.duckmemo.util;

import com.mmjang.duckmemo.ui.widget.BigBangLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiangjie on 2017/7/26.
 */

public class FieldUtil {


    public static String getSelectedText(List<BigBangLayout.Line> lines) {
        StringBuilder sb = new StringBuilder();
        List<BigBangLayout.Item> selectedItems = getSelectedItems(lines);
        for (int i = 0; i < selectedItems.size(); i++) {
            BigBangLayout.Item item = selectedItems.get(i);
            if (item.isSelected()) {
                sb.append(item.getText());
                if (RegexUtil.isEnglish(item.getText().toString()) || RegexUtil.isSpecialWord(item.getText().toString())) {
                    sb.append(" ");
                }
            }
        }
        return sb.toString().trim();
    }

    private static List<BigBangLayout.Item> getSelectedItems(List<BigBangLayout.Line> lines) {
        List<BigBangLayout.Item> selectedItems = new ArrayList<>();
        for (BigBangLayout.Line line : lines) {
            for (BigBangLayout.Item item : line.getItems()) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }
        }
        return selectedItems;
    }

    public static String getNormalSentence(List<BigBangLayout.Line> lines) {
        StringBuilder sb = new StringBuilder();
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            BigBangLayout.Line line = lines.get(lineIndex);
            List<BigBangLayout.Item> items = line.getItems();
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                BigBangLayout.Item item = items.get(itemIndex);
                if(item.getText().equals("\n")){
                    sb.append("<br/>");
                }
                if (item.isSelected()) {
                    //sb.append("<b>");
                    sb.append(item.getText());
                    //sb.append("</b>");
                } else {
                    sb.append(item.getText());
                }
            }
        }
        return sb.toString().trim();
    }

    public static String getBoldSentence(List<BigBangLayout.Line> lines) {
        StringBuilder sb = new StringBuilder();
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            BigBangLayout.Line line = lines.get(lineIndex);
            List<BigBangLayout.Item> items = line.getItems();
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                BigBangLayout.Item item = items.get(itemIndex);
                if (item.getText().equals("\n")) {
                    sb.append("<br/>");
                }
                    if (item.isSelected()) {
                        sb.append("<b>");
                        sb.append(item.getText());
                        sb.append("</b>");
                    } else {
                        sb.append(item.getText());
                    }
                }
            }
            return sb.toString().trim();
    }

    public static String getBlankSentence(List<BigBangLayout.Line> lines) {
        StringBuilder sb = new StringBuilder();
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            BigBangLayout.Line line = lines.get(lineIndex);
            List<BigBangLayout.Item> items = line.getItems();
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                BigBangLayout.Item item = items.get(itemIndex);
                if(item.getText().equals("\n")) {
                    sb.append("<br/>");
                }
                if (item.isSelected()) {
                    sb.append("{{c1::" + item.getText() + "}}");
                } else {
                    sb.append(item.getText());
                }
            }
        }
        return sb.toString().replace("}}{{c1::","").trim(); //combine adjacent cloze
    }
}
