package cn.ccsu.cecs.student.service;

import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.student.entity.StuProfession;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 专业表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
public interface IStuProfessionService extends IService<StuProfession> {

    PageUtils queryPage(Map<String, Object> params);
}
