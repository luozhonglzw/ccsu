package cn.ccsu.cecs.student.service.impl;

import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import cn.ccsu.cecs.student.entity.StuProfession;
import cn.ccsu.cecs.student.mapper.StuProfessionMapper;
import cn.ccsu.cecs.student.service.IStuProfessionService;
import cn.ccsu.cecs.student.vo.ProfessionVo;
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
 * 专业表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Service
public class StuProfessionServiceImpl extends ServiceImpl<StuProfessionMapper, StuProfession> implements IStuProfessionService {
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<StuProfession> page = this.page(
                new Query<StuProfession>().getPage(params),
                new QueryWrapper<StuProfession>()
                        .eq("deleted", ProjectConstant.DeletedStatusEnum.NOT_DELETED.getCode())
        );
        // 得到StuProfession分页数据
        List<StuProfession> stuProfessions = page.getRecords();
        // 构造专业Vo分页对象
        IPage<ProfessionVo> professionVoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());

        List<ProfessionVo> professionVos = stuProfessions.stream().map(stuProfession -> {
            ProfessionVo professionVo = new ProfessionVo();
            professionVo.setId(stuProfession.getId());
            professionVo.setProfessionName(stuProfession.getPfName());
            return professionVo;
        }).collect(Collectors.toList());

        professionVoPage.setRecords(professionVos);
        return new PageUtils(professionVoPage);
    }
}
