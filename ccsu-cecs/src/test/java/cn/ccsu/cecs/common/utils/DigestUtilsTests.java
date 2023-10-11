package cn.ccsu.cecs.common.utils;

import cn.ccsu.cecs.CcsuCecsApplicationTests;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

@Slf4j
public class DigestUtilsTests extends CcsuCecsApplicationTests {
    @Test
    public void test_string_md5() {
        String zhw = DigestUtils.md5DigestAsHex("zhw".getBytes());
        log.info("{},{}", zhw, zhw.length());
    }

    @Test
    public void test_file_md5() {

    }
}
