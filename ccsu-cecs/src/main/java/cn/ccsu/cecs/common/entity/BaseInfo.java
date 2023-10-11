package cn.ccsu.cecs.common.entity;

import cn.ccsu.cecs.student.vo.ClassVo;
import cn.ccsu.cecs.student.vo.CollegeVo;
import cn.ccsu.cecs.student.vo.GradeVo;
import cn.ccsu.cecs.student.vo.ProfessionVo;
import lombok.Data;

import java.util.List;

/**
 * 学院、年级、专业、班级信息汇总表
 */
@Data
public class BaseInfo {
    private List<CollegeVo> collegeVos;
    private List<GradeVo> gradeVos;
    private List<ProfessionVo> professionVos;
    private List<ClassVo> classVos;
}
