package cn.ccsu.cecs.oos.common.utils;

import java.time.LocalDate;

public class DateUtils {

    /**
     * 获取当前所处的学年
     *
     * @return 如：2021-2022学年
     */
    public static String getCurrentYear() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int monthValue = now.getMonthValue();
        if (monthValue < 9) {
            return (year - 1) + "-" + year + "学年";
        }
        return year + "-" + (year + 1) + "学年";
    }

}
