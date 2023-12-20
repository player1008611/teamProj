package com.teamProj.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditByword {
    public static String editByword(String studentWord, String recruitmentWord) {
        if (studentWord == null) {
            studentWord = "";
        }
        if (recruitmentWord == null) {
            recruitmentWord = "";
        }
        Map<String, Integer> result = processStrings(studentWord, recruitmentWord);
        return mapToStr(result);
    }

    private static Map<String, Integer> processStrings(String str1, String str2) {
        Map<String, Integer> dict1 = strToMap(str1);
        Map<String, Integer> dict2 = new HashMap<>();
        String[] parts = str2.split("\\|");
        int i = 1;
        for (String part : parts) {
            dict2.put(part, i); // 初始化值为1
            i++;
        }

        for (Map.Entry<String, Integer> entry : dict2.entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();

            if (dict1.containsKey(key)) {
                // 如果字符串1中有相同的键，值加5
                dict1.put(key, dict1.get(key) + 50);
            } else {
                // 如果字符串1中没有该键，创建，并设值为5
                dict1.put(key, 50);
            }
        }

        for (Map.Entry<String, Integer> entry : dict1.entrySet()) {
            String key = entry.getKey();

            if (!dict2.containsKey(key)) {
                // 对字符串1中有而字符串2中没有的键值对，值取ln
                int currentValue = entry.getValue();
                double newValue = currentValue * 0.9;
                dict1.put(key, (int) Math.round(newValue)); // 四舍五入为整数
            }
        }

        return dict1;
    }

    private static Map<String, Integer> strToMap(String input) {
        Map<String, Integer> map = new HashMap<>();
        Pattern pattern = Pattern.compile("\\(([^,]+),(\\d+)\\)");
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            String key = matcher.group(1);
            int value = Integer.parseInt(matcher.group(2));
            map.put(key, value);
        }

        return map;
    }

    private static String mapToStr(Map<String, Integer> map) {
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        // 对Map按值排序
        map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));

        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
            result.append("(").append(entry.getKey()).append(",").append(entry.getValue()).append(");");
        }

        // 删除最后一个分号
        if (result.length() > 0) {
            result.deleteCharAt(result.length() - 1);
        }

        return result.toString();
    }

    public static Integer getScore(String studentWord, String recruitmentWord) {
        Map<String, Integer> dict1 = strToMap(studentWord);
        Map<String, Integer> dict2 = new HashMap<>();
        String[] parts = recruitmentWord.split("\\|");
        int i = 1;
        for (String part : parts) {
            dict2.put(part, i); // 初始化值为1
            i++;
        }
        int score = 0;
        for (Object key : dict1.keySet()) {
            if (dict2.containsKey(key)) {
                score += dict1.get(key) * dict2.get(key);
            }
        }
        return score;
    }
}
