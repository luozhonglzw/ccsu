package cn.ccsu.cecs.student.service;

import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.student.entity.StuClass;
import cn.ccsu.cecs.student.vo.ClassVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 班级表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
public interface IStuClassService extends IService<StuClass> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取班级信息
     *
     * @param id 班级id
     * @return 班级信息
     */
    ClassVo getClassVo(Integer id);
}
