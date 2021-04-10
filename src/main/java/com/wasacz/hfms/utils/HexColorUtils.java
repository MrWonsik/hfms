package com.wasacz.hfms.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexColorUtils {
    private static final String HEX_COLOR_PATTERN = "^#([a-fA-F0-9]{6}|[a-fA-F0-9]{3})$";
    private static final Pattern patternHex = Pattern.compile(HEX_COLOR_PATTERN);

    public static String getRandomHexColor() {
        Random obj = new Random();
        int rand_num = obj.nextInt(0xffffff + 1);
        return String.format("#%06x", rand_num);
    }

    public static boolean isCorrectHexColor(String hexColor) {
        Matcher matcher = patternHex.matcher(hexColor);
        return matcher.matches();
    }

    public static boolean isNotCorrectHexColor(String hexColor) {
        return !isCorrectHexColor(hexColor);
    }
}
