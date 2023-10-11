package cn.ccsu.cecs.student;


import cn.ccsu.cecs.common.response.ResponseResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Objects;

@RestController
public class TestController {

    @GetMapping("/download/{fileName}")
    public String download(@PathVariable("fileName") String fileName,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException, ServletException {
        response.sendRedirect("http://localhost:8080/testtest/" + fileName);
        return "123";
    }

    @Autowired
    RestTemplate restTemplate;


    @Test
    public void md5(){
        String[] str = {
                "20192019",
                "20202020",
                "20212021",
                "20222022",
                "201920192019",
                "20222022",
                "123456",
        };
        for (int i = 0; i < str.length; i++) {
            String s = DigestUtils.md5DigestAsHex(str[i].getBytes());
            System.out.println(str[i] + " -> md5:" + s);
        }
    }

}
