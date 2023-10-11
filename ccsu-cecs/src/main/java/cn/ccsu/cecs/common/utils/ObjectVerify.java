package cn.ccsu.cecs.common.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;

/**
 * 参数校验器
 */
@Slf4j
@Component
public class ObjectVerify implements Serializable {

    private static final long serialVersionUID = -8011088026145884843L;

    // checkoutObject 检查对象的属性是否为空
    public boolean checkoutObject(Object target) {
        // 取到obj的class, 并取到所有属性
        // 遍历所有属性
        for (Field field : target.getClass().getDeclaredFields()) {
            if (!isNotNull(target, field)) {
                return false;
            }
        }
        return true;
    }

    // isNotNull 检查属性是否不为空
    private boolean isNotNull(Object source, Field field) {
        // 结果
        boolean result = false;
        // 设置可访问
        field.setAccessible(true);
        try {
            // 获取属性类型名称
            String type = field.getType().getName();
            if (Objects.nonNull(field.get(source))) { // 对象值不为空
                result = true;
                // 类型值校验是否合格
                if (StringUtils.equals(String.class.getName(), type)) {// String 类型
                    result = string((String) field.get(source));
                } else if (StringUtils.equals(Integer.class.getName(), type)) {// Integer 类型
                    result = integer((Integer) field.get(source));
                }// else if (StringUtils.equals(Date.class.getName(), type)) {// Date 类型
                //  result = date((Date) field.get(source));
                // }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 检查Boolean类型  可能用不到
    private boolean aBool(Boolean bool) {
        return bool;
    }

    // 检查Date类型
    private boolean date(Date date) {
        return false;
    }

    // 检查Integer类型
    private boolean integer(Integer target) {
        return target >= 0;
    }

    // 检查String类型
    private boolean string(String target) {
        return StringUtils.isNotBlank(target);
    }

}
