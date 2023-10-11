package cn.ccsu.cecs.admin.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员工具类
 */
public class AdminUtils {

    /**
     * 检查管理员账号是否格式错误
     *
     * @param username 管理员账号
     */
    public static void checkAdminAccount(String username) {
//        String pattern = "^[B20]\\d{11}";
//        Pattern r = Pattern.compile(pattern);
//        Matcher m = r.matcher(username);
//        if (!m.matches()) {
//            throw new StuCheckFailedException("学号认证错误");
//        }
    }

    /**
     * 找出两个list中的不同元素
     * 用Map存放List1和List2的元素作为key，value为其在List1和List2中出现的次数
     * 出现次数为1的即为不同元素，查找次数为list1.size() + list2.size()，较方法1和2，是极大简化
     * 【效率最高】
     */
    public static List<Integer> getDiff(List<Integer> listA, List<Integer> listB) {
        List<Integer> diff = new ArrayList<Integer>();
        List<Integer> maxList = listA;
        List<Integer> minList = listB;
        if (listB.size() > listA.size()) {
            maxList = listB;
            minList = listA;
        }
        Map<Integer, Integer> map = new HashMap<Integer, Integer>(maxList.size());
        for (Integer string : maxList) {
            map.put(string, 1);
        }
        for (Integer string : minList) {
            if (map.get(string) != null) {
                map.put(string, 2);
                continue;
            }
            diff.add(string);
        }
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                diff.add(entry.getKey());
            }
        }
        return diff;
    }
}
