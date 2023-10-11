package cn.ccsu.cecs.common.core;

import cn.ccsu.cecs.bonus.vo.ReceiveBonusApplyVo;
import cn.ccsu.cecs.student.vo.StudentLoginVo;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 申请表对象（4月已遗弃、未使用）
 */
@Component
public class DefaultBonusApply {

    // 存放学生临时提交的申请表信息（主要用于保存图片，因为图片可以保存三张）
    private final Map<StudentLoginVo, List<ReceiveBonusApplyVo>> stuBonusApplyMap = new HashMap<>(16);

    @PostConstruct
    public void init() {
    }

    /**
     * 上传学生的申请表（存储在内存中）
     *
     * @param loginVo             学生登录信息
     * @param receiveBonusApplyVo 提交的申请表信息
     */
    public void putStuBonusApply(StudentLoginVo loginVo, ReceiveBonusApplyVo receiveBonusApplyVo) {
        Set<StudentLoginVo> studentLoginVos = stuBonusApplyMap.keySet();
        if (studentLoginVos.size() > 0) {
            studentLoginVos.forEach((studentLoginVo -> {
                if (Objects.equals(studentLoginVo.getId(), loginVo.getId())) {
                    List<ReceiveBonusApplyVo> receiveBonusApplyVos = stuBonusApplyMap.get(studentLoginVo);
                    if (receiveBonusApplyVos == null || receiveBonusApplyVos.size() == 0) {
                        receiveBonusApplyVos = new ArrayList<>();
                    }
                    receiveBonusApplyVos.add(receiveBonusApplyVo);
                    synchronized (stuBonusApplyMap) {
                        stuBonusApplyMap.put(studentLoginVo, receiveBonusApplyVos);
                    }
                }
            }));
        } else {
            List<ReceiveBonusApplyVo> list = new ArrayList<>();
            list.add(receiveBonusApplyVo);
            synchronized (stuBonusApplyMap) {
                stuBonusApplyMap.put(loginVo, list);
            }
        }
    }

    /**
     * 移除该用户的所有申请表
     *
     * @param studentLoginVo 用户
     */
    public void removeStuAllBonusApply(StudentLoginVo studentLoginVo) {
        if (stuBonusApplyMap.get(studentLoginVo) != null) {
            synchronized (stuBonusApplyMap) {
                if (stuBonusApplyMap.get(studentLoginVo) != null) {
                    stuBonusApplyMap.remove(studentLoginVo);
                }
            }
        }
    }


    public Map<StudentLoginVo, List<ReceiveBonusApplyVo>> getStuBonusApplyMap() {
        return stuBonusApplyMap;
    }
}
