package cn.ccsu.cecs.student.service.impl;

import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import cn.ccsu.cecs.student.entity.StuCollege;
import cn.ccsu.cecs.student.mapper.StuCollegeMapper;
import cn.ccsu.cecs.student.service.IStuCollegeService;
import cn.ccsu.cecs.student.vo.CollegeVo;
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
 * 学院表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Service
public class StuCollegeServiceImpl extends ServiceImpl<StuCollegeMapper, StuCollege> implements IStuCollegeService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StuCollege> page = this.page(
                new Query<StuCollege>().getPage(params),
                new QueryWrapper<StuCollege>()
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
        );
        // 得到学院分页数据
        List<StuCollege> stuColleges = page.getRecords();
        // 构造学院Vo分页对象
        IPage<CollegeVo> collegeVoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<CollegeVo> collegeVos = stuColleges.stream().map(stuCollege -> {
            CollegeVo collegeVo = new CollegeVo();
            collegeVo.setId(stuCollege.getId());
            collegeVo.setCollegeName(stuCollege.getName());
            return collegeVo;
        }).collect(Collectors.toList());

        collegeVoPage.setRecords(collegeVos);
        return new PageUtils(collegeVoPage);
    }

    @Override
    public CollegeVo getCollegeVo(Integer id) {
        StuCollege stuCollege = this.getOne(new QueryWrapper<StuCollege>().eq("id", id)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
        CollegeVo collegeVo = new CollegeVo();
        collegeVo.setId(stuCollege.getId());
        collegeVo.setCollegeName(stuCollege.getName());
        return collegeVo;
    }
}
