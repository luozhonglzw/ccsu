package cn.ccsu.cecs.student.service.impl;

import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import cn.ccsu.cecs.student.entity.StuGrade;
import cn.ccsu.cecs.student.mapper.StuGradeMapper;
import cn.ccsu.cecs.student.service.IStuGradeService;
import cn.ccsu.cecs.student.vo.GradeVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 年级表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Service
public class StuGradeServiceImpl extends ServiceImpl<StuGradeMapper, StuGrade> implements IStuGradeService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StuGrade> page = this.page(
                new Query<StuGrade>().getPage(params),
                new QueryWrapper<StuGrade>()
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
        );
        // 得到StuGrade分页数据
        List<StuGrade> stuGrades = page.getRecords();
        // 构造年级Vo分页对象
        IPage<GradeVo> gradeVoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<GradeVo> gradeVos = stuGrades.stream().map(stuGrade -> {
            GradeVo gradeVo = new GradeVo();
            gradeVo.setId(stuGrade.getId());
            gradeVo.setGradeName(stuGrade.getGdName());
            return gradeVo;
        }).collect(Collectors.toList());

        gradeVoPage.setRecords(gradeVos);
        return new PageUtils(gradeVoPage);
    }
}
