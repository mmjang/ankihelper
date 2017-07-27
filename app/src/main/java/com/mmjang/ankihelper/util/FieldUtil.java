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
                if (item.getText().toString().matches("[a-zA-Z0-9]*")) {
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
        for (BigBangLayout.Line line : lines) {
            List<BigBangLayout.Item> items = line.getItems();
            for (int i = 0; i < items.size(); i++) {
                BigBangLayout.Item item = items.get(i);
                if (item.isSelected()) {
                    sb.append("<b>");
                    sb.append(item.getText());
                    sb.append("</b>");
                } else {
                    sb.append(item.getText());
                }
                if (item.getText().toString().matches("[a-zA-Z0-9]*")) {
                    if (i + 1 < items.size() && !RegexUtil.isSymbol(items.get(i + 1).getText().toString())) {
                        sb.append(" ");
                    }
                }
            }
        }
        return sb.toString().trim();
    }

    public static String getBlankSentence(List<BigBangLayout.Line> lines) {
        StringBuilder sb = new StringBuilder();
        for (BigBangLayout.Line line : lines) {
            List<BigBangLayout.Item> items = line.getItems();
            for (int i = 0; i < items.size(); i++) {
                BigBangLayout.Item item = items.get(i);
                if (item.isSelected()) {
                    sb.append("{{c1::" + item.getText() + "}}");
                } else {
                    sb.append(item.getText());
                }
                if (item.getText().toString().matches("[a-zA-Z0-9]*")) {
                    if (i + 1 < items.size() && !RegexUtil.isSymbol(items.get(i + 1).getText().toString())) {
                        sb.append(" ");
                    }
                }
            }
        }
        return sb.toString().trim();
    }
}
