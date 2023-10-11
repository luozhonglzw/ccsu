package cn.ccsu.cecs.oos;

import cn.ccsu.cecs.oos.controller.OosController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
class CcsuCecsOosApplicationTests {

    @Autowired
    private OosController oosController;
    @Test
    void contextLoads() throws IOException {
        oosController.look(23, null, null);
    }

}
