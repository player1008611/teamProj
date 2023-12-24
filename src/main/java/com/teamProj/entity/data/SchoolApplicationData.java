package com.teamProj.entity.data;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class SchoolApplicationData {

    @Data
    @AllArgsConstructor
    public static class CollegeApplicationData {
        Integer studentNum;
        Map<String, Integer> enterprise;
        Map<String, Integer> city;
        Map<String, MajorApplicationData> major;
        Integer total;

        public CollegeApplicationData() {
            setStudentNum(0);
            setEnterprise(new HashMap<>());
            setCity(new HashMap<>());
            setMajor(new HashMap<>());
        }
    }

    @Data
    @AllArgsConstructor
    public static class MajorApplicationData {
        Integer studentNum;
        Map<String, Integer> enterprise;
        Map<String, Integer> city;
        Integer total;

        public MajorApplicationData() {
            setStudentNum(0);
            setCity(new HashMap<>());
            setEnterprise(new HashMap<>());
        }
    }

    Integer total;
    Integer studentNum;
    Map<String, Integer> enterprise;
    Map<String, Integer> city;
    Map<String, CollegeApplicationData> college;

    public SchoolApplicationData() {
        setStudentNum(0);
        setCollege(new HashMap<>());
        setEnterprise(new HashMap<>());
        setCity(new HashMap<>());
    }

    // 示例方法，对Map中的值进行排序
    public static LinkedHashMap<String, Integer> sortMapByValue(Map<String, Integer> unsortedMap) {
        LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<>();

        // 将Map中的键值对放入List中
        List<Map.Entry<String, Integer>> list = new ArrayList<>(unsortedMap.entrySet());

        // 使用Comparator对List中的Entry按值进行排序
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // 将排序后的键值对放入新的LinkedHashMap中
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static LinkedHashMap<String,CollegeApplicationData> sortCollegeMapByValue(Map<String, CollegeApplicationData> unsortedMap) {
        LinkedHashMap<String, CollegeApplicationData> sortedMap = new LinkedHashMap<>();

        // 将Map中的键值对放入List中
        List<Map.Entry<String, CollegeApplicationData>> list = new ArrayList<>(unsortedMap.entrySet());

        // 使用Comparator对List中的Entry按值进行排序
        list.sort(Map.Entry.comparingByValue(Comparator.comparing(CollegeApplicationData::getStudentNum).reversed()));

        // 将排序后的键值对放入新的LinkedHashMap中
        for (Map.Entry<String, CollegeApplicationData> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static LinkedHashMap<String,MajorApplicationData> sortMajorMapByValue(Map<String, MajorApplicationData> unsortedMap) {
        LinkedHashMap<String, MajorApplicationData> sortedMap = new LinkedHashMap<>();

        // 将Map中的键值对放入List中
        List<Map.Entry<String, MajorApplicationData>> list = new ArrayList<>(unsortedMap.entrySet());

        // 使用Comparator对List中的Entry按值进行排序
        list.sort(Map.Entry.comparingByValue(Comparator.comparing(MajorApplicationData::getStudentNum).reversed()));

        // 将排序后的键值对放入新的LinkedHashMap中
        for (Map.Entry<String, MajorApplicationData> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
