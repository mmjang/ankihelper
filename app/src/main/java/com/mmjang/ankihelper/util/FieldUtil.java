package com.mmjang.ankihelper.util;

import com.mmjang.ankihelper.ui.widget.BigBangLayout;

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
                    if (i + 1 < selectedItems.size() && !RegexUtil.isSymbol(selectedItems.get(i + 1).getText().toString())) {
                        sb.append(" ");
                    }
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

    public static String getBoldSentence(List<BigBangLayout.Line> lines) {
        StringBuilder sb = new StringBuilder();
        for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
            BigBangLayout.Line line = lines.get(lineIndex);
            List<BigBangLayout.Item> items = line.getItems();
            for (int itemIndex = 0; itemIndex < items.size(); itemIndex++) {
                BigBangLayout.Item item = items.get(itemIndex);
                if (item.isSelected()) {
                    sb.append("<b>");
                    sb.append(item.getText());
                    sb.append("</b>");
                } else {
                    sb.append(item.getText());
                }
                String current = item.getText().toString();
                if (RegexUtil.isEnglish(current) || RegexUtil.isSpecialWord(current) || RegexUtil.isSymbol(current)) {
                    if (itemIndex + 1 == items.size()) {
                        //当前行最后item
                        if (lineIndex + 1 < lines.size() && !RegexUtil.isSymbol(lines.get(lineIndex + 1).getItems().get(0).getText().toString())) {
                            //当前行不是最后一行，下一行第一个不是符号，则加空格
                            sb.append(" ");
                        }
                    } else {
                        //当前行非最后item, 且该item之后的不是符号，则加空格
                        if (!RegexUtil.isSymbol(items.get(itemIndex + 1).getText().toString())) {
                            sb.append(" ");
                        }
                    }
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
                if (item.isSelected()) {
                    sb.append("{{c1::" + item.getText() + "}}");
                } else {
                    sb.append(item.getText());
                }
                String current = item.getText().toString();
                if (RegexUtil.isEnglish(current) || RegexUtil.isSpecialWord(current) || RegexUtil.isSymbol(current)) {
                    if (itemIndex + 1 == items.size()) {
                        //当前行最后item
                        if (lineIndex + 1 < lines.size() && !RegexUtil.isSymbol(lines.get(lineIndex + 1).getItems().get(0).getText().toString())) {
                            //当前行不是最后一行，下一行第一个不是符号，则加空格
                            sb.append(" ");
                        }
                    } else {
                        //当前行非最后item, 且该item之后的不是符号，则加空格
                        if (!RegexUtil.isSymbol(items.get(itemIndex + 1).getText().toString())) {
                            sb.append(" ");
                        }
                    }
                }
            }
        }
        return sb.toString().trim();
    }
}
