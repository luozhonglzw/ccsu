package cn.ccsu.cecs.student.service;

import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.student.entity.StuCollege;
import cn.ccsu.cecs.student.vo.CollegeVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 学院表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
public interface IStuCollegeService extends IService<StuCollege> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 获取学院信息
     *
     * @param id 学院id
     * @return 学院信息
     */
    CollegeVo getCollegeVo(Integer id);
}
