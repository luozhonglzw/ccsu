package cn.ccsu.cecs.yangjr;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;


public class PushEvent {


    @Test
    public void test(){
        RestTemplate restTemplate = new RestTemplate();

        String forObject = restTemplate.getForObject("http://47.97.74.179:8080/array?info=123", String.class, new HashMap<>());
        System.out.println(forObject);

    }


}
