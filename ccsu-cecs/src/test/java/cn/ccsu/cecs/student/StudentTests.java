package cn.ccsu.cecs.student;

import cn.ccsu.cecs.CcsuCecsApplicationTests;
import cn.ccsu.cecs.common.entity.StudentCoreInfo;
import cn.ccsu.cecs.student.entity.StuStudent;
import cn.ccsu.cecs.student.mapper.StuStudentMapper;
import cn.ccsu.cecs.student.service.IStuStudentService;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StudentTests extends CcsuCecsApplicationTests {
    @Autowired
    private IStuStudentService stuStudentService;

    @Autowired
    private StuStudentMapper stuStudentMapper;

    @Test
    public void test_findAll() {
        List<StuStudent> students = stuStudentService.lambdaQuery().select().list();
        for (StuStudent student : students) {
            log.info("{}", student);
        }
    }

    @Test
    public void test_encode(){
        String s = DigestUtils.md5DigestAsHex("123".getBytes());
        System.out.println(s);
    }

    @Test
    public void test_core(){
        StudentCoreInfo studentCoreInfo = stuStudentMapper.getStudentCoreInfo(1);
        System.out.println(studentCoreInfo);
    }
}
