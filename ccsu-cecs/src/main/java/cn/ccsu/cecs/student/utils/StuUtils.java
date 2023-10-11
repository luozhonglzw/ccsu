package cn.ccsu.cecs.student.utils;

import cn.ccsu.cecs.common.exception.StuCheckFailedException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 学生工具类
 */
public class StuUtils {

    /**
     * 检验学号格式是否错误
     *
     * @param stuNumber 学号
     */
    public static void checkStuNumber(String stuNumber) {
        String pattern = "^[B20]\\d{11}";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(stuNumber);
        if (!m.matches()) {
            throw new StuCheckFailedException("学号格式认证错误");
        }
    }


}
