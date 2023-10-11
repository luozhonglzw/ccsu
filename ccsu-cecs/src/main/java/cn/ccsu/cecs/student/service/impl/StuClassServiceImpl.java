package cn.ccsu.cecs.student.service.impl;

import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import cn.ccsu.cecs.student.entity.StuClass;
import cn.ccsu.cecs.student.mapper.StuClassMapper;
import cn.ccsu.cecs.student.service.IStuClassService;
import cn.ccsu.cecs.student.vo.ClassVo;
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
 * 班级表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Service
public class StuClassServiceImpl extends ServiceImpl<StuClassMapper, StuClass> implements IStuClassService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StuClass> page = this.page(
                new Query<StuClass>().getPage(params),
                new QueryWrapper<StuClass>()
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
        );
        // 得到班级分页数据
        List<StuClass> stuClasses = page.getRecords();
        // 构造班级Vo分页对象
        IPage<ClassVo> classVoIPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<ClassVo> classVos = stuClasses.stream().map(stuClass -> {
            ClassVo classVo = new ClassVo();
            classVo.setId(stuClass.getId());
            classVo.setClassName(stuClass.getClName());
            return classVo;
        }).collect(Collectors.toList());

        classVoIPage.setRecords(classVos);
        return new PageUtils(classVoIPage);
    }

    @Override
    public ClassVo getClassVo(Integer id) {
        StuClass stuClass = this.getOne(new QueryWrapper<StuClass>().eq("id", id)
                .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode()));
        ClassVo classVo = new ClassVo();
        classVo.setId(stuClass.getId());
        classVo.setClassName(stuClass.getClName());
        return classVo;
    }

}
