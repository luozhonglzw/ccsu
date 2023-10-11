package cn.ccsu.cecs.student;

import cn.ccsu.cecs.CcsuCecsApplicationTests;
import cn.ccsu.cecs.student.entity.StuCollege;
import cn.ccsu.cecs.student.service.IStuCollegeService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

@Slf4j
public class CollegeTests extends CcsuCecsApplicationTests {
    @Autowired
    private IStuCollegeService stuCollegeService;

    @Test
    public void test_addCollege() {
        String[] colleges = {"土木工程学院", "机电工程学院", "计算机工程与应用数学学院", "电子信息与电气工程学院"};
        ArrayList<StuCollege> list = new ArrayList<>();

        for (String name : colleges) {
            StuCollege college = new StuCollege();
            college.setName(name);
            list.add(college);
        }

        boolean batch = stuCollegeService.saveBatch(list);
        log.info("{}", batch);
    }
}
