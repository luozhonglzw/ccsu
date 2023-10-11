package cn.ccsu.cecs.test;

import java.math.BigDecimal;
import java.util.Vector;

class NowCoder {
    public static void main(String[] args) {
        int i = 1;
        int j = i++;
        if ((j > ++j) && (i++ == j)) {
            j += i;
        }
        System.out.println(j);

    }
}


class Example {
    String str = new String("good");
    char[] ch = {'a', 'b', 'c'};

    public static void main(String args[]) {

        try {
            BigDecimal bigDecimal = new BigDecimal("6分");
            System.out.println(bigDecimal);
        } catch (NumberFormatException e) {
            System.out.println("分数需要提供数值型");
        }

        try {
            BigDecimal bigDecimal = new BigDecimal("6");
            System.out.println(bigDecimal);
        } catch (NumberFormatException e) {
            System.out.println("分数需要提供数值型");
        }

        try {
            BigDecimal bigDecimal = new BigDecimal("6.01");
            System.out.println(bigDecimal);
        } catch (NumberFormatException e) {
            System.out.println("分数需要提供数值型");
        }

    }

    public static void change(String str, char ch[]) {

    }
}
