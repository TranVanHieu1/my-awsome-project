package com.ojt.mockproject.utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StringUtil {

    public List<Integer> stringToList(String string) {
        return Arrays.stream(string.split(","))
                .map(Integer::parseInt)
                .toList();
    }

    public String listToString(List<Integer> listInt) {
        return listInt.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

}
