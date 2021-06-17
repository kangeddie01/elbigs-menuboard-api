package com.elbigs.util;

import java.security.Timestamp;
import java.util.regex.Pattern;

public class ElbigsUtil {


    public static String makeRandAlpabet(int length) {
        char[] randChars = new char[length];
        for (int i = 0; i < length; i++) {

            int rand = (int) (Math.random() * 26);
            int bigSmal = (int) (Math.random() * 2);
            char ch = (char) (rand + (bigSmal == 1 ? 65 : 97));
            randChars[i] = ch;

        }
        return String.valueOf(randChars);
    }

    public static String makeRandAlpabet(int length, boolean isUpperOnly) {
        return isUpperOnly ? makeRandAlpabet(length).toUpperCase() : makeRandAlpabet(length);
    }

    /**
     * 랜덤 텍스트 ( 알파벳대문자 + 숫자 )
     *
     * @param length
     * @return
     */
    public static String makeRandValue(int length) {
        char[] randChars = new char[length];
        for (int i = 0; i < length; i++) {

            int rand = (int) (Math.random() * 26);
            char ch = (char) (rand + 65);
            int rand2 = (int) (Math.random() * 10);
            char ch2 = (char) (rand2 + 48);

            int bigSmal = (int) (Math.random() * 2);
            randChars[i] = (bigSmal == 1 ? ch : ch2);

        }
        return String.valueOf(randChars);
    }

    public static boolean isValidBusinessNum(String text) {
        if (text == null) {
            return false;
        }
        if (text.indexOf("-") > 0) {
            text = text.replaceAll("-", "");
        }

        return checkCompNumber(text);
    }

    public static boolean checkCompNumber(String compNumber) {

        int hap = 0;
        int temp = 0;
        int check[] = {1, 3, 7, 1, 3, 7, 1, 3, 5};  //사업자번호 유효성 체크 필요한 수

        if (compNumber.length() != 10)    //사업자번호의 길이가 맞는지를 확인한다.
            return false;

        for (int i = 0; i < 9; i++) {
            if (compNumber.charAt(i) < '0' || compNumber.charAt(i) > '9')  //숫자가 아닌 값이 들어왔는지를 확인한다.
                return false;
            hap = hap + (Character.getNumericValue(compNumber.charAt(i)) * check[temp]); //검증식 적용
            temp++;
        }

        hap += (Character.getNumericValue(compNumber.charAt(8)) * 5) / 10;
        if ((10 - (hap % 10)) % 10 == Character.getNumericValue(compNumber.charAt(9))) //마지막 유효숫자와 검증식을 통한 값의 비교
            return true;
        else
            return false;
    }

    public static boolean isValidEmail(String str) {
        String pattern = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
        return Pattern.matches(pattern, str);
    }

    public static boolean isValidCellPhone(String str) {
        String pattern = "^\\d{3}-\\d{3,4}-\\d{4}$";
        return Pattern.matches(pattern, str);
    }

    public static boolean isValidPhone(String str) {
        String pattern = "^\\d{2,3}-\\d{3,4}-\\d{4}$";
        return Pattern.matches(pattern, str);
    }

    public static boolean isValidPhone1(String str) {
        String pattern = "^\\d{2,3}$";
        return Pattern.matches(pattern, str);
    }

    public static boolean isValidPhone2(String str) {
        String pattern = "^\\d{3,4}$";
        return Pattern.matches(pattern, str);
    }

    public static boolean isValidPhone3(String str) {
        String pattern = "^\\d{4}$";
        return Pattern.matches(pattern, str);
    }

    public static boolean isValidNumber(String str) {
        String pattern = "^[0-9]*$";
        return Pattern.matches(pattern, str);
    }


    public static boolean isValidTime(String str) {

        if (str == null) {
            return false;
        }

        String[] times = str.split(":");

        if (times[0].length() != 2 || times[0].length() != 2 ||
                !isValidNumber(str.replaceAll(":", ""))
                || Integer.parseInt(times[0]) > 23 || Integer.parseInt(times[0]) > 59) {
            return false;
        }

        return true;
    }


    public static String ifNull(String text, String replaceText) {
        return text == null ? replaceText : text;
    }

    public static String ifEmpty(String text, String replaceText) {
        return (text == null || text.equals("")) ? replaceText : text;
    }

}
