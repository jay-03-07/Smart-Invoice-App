package com.example.projectapp;

import java.text.DecimalFormat;



public class NumberToWords {

    private static final String[] units = {"", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};
    private static final String[] teens = {"", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"};
    private static final String[] tens = {"", "Ten", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"};

    private NumberToWords() {
        // Private constructor to prevent instantiation
    }

    public static String convert(double number) {
        if (number == 0) {
            return "Zero";
        }

        String words = "";

        // Handle the integer part
        if ((long) number > 0) {
            words += convertToWords((long) number) + " Rupees ";
        }

        // Handle the decimal part
        int paise = (int) ((number - (long) number) * 100);
        if (paise > 0) {
            words += "and " + convertToWords(paise) + " Paise";
        }

        return words;
    }

    private static String convertToWords(long num) {
        if (num < 10) {
            return units[(int) num];
        } else if (num < 20) {
            return teens[(int) (num - 10)];
        } else if (num < 100) {
            return tens[(int) (num / 10)] + ((num % 10 > 0) ? " " + convertToWords(num % 10) : "");
        } else if (num < 1000) {
            return units[(int) (num / 100)] + " Hundred" + ((num % 100 > 0) ? " " + convertToWords(num % 100) : "");
        } else if (num < 100000) {
            return convertToWords(num / 1000) + " Thousand" + ((num % 1000 > 0) ? " " + convertToWords(num % 1000) : "");
        } else if (num < 10000000) {
            return convertToWords(num / 100000) + " Lakh" + ((num % 100000 > 0) ? " " + convertToWords(num % 100000) : "");
        } else {
            return "Number out of range";
        }
    }

    public static void main(String[] args) {
        double amount = 1234567.89;
        System.out.println(convert(amount));
    }
}