package cn.ccsu.cecs.common.oos;

import cn.ccsu.cecs.CcsuCecsApplicationTests;
import cn.ccsu.cecs.common.oos.image.SaveImage;
import cn.ccsu.cecs.common.response.ResponseResult;
import cn.ccsu.cecs.oos.entity.OosUpload;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.type.ReferenceType;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
//@SpringBootTest
public class OosTests {

    @Autowired
    private SaveImage saveImage;

    @Test
    public void test_ok() {
        saveImage.print();
    }


    @Test
    public void test_json(){
        ResponseResult result = new ResponseResult();
        Map<String, Object> map = new HashMap<>();
        OosUpload oosUpload = new OosUpload();
        oosUpload.setMd5("asfnkalnas");
        oosUpload.setDbUrl("1234123asdq21ee");
        oosUpload.setName("asdaasda");

        map.put("oos", oosUpload);
        result.setData(map);

        Gson gson = new Gson();
        String json = gson.toJson(result.getData().values());
        List<OosUpload> upload = gson.fromJson(json, new TypeToken<List<OosUpload>>() {}.getType());
        System.out.println(upload);
    }
}
