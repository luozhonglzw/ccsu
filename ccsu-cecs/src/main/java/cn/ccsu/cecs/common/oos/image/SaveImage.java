package cn.ccsu.cecs.common.oos.image;

import cn.ccsu.cecs.common.oos.SaveOos;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 图片保存
 */
@Slf4j
@Component
public class SaveImage extends SaveOos {

    private final static HashMap<String, String> CONTENT_TYPE = new HashMap<String, String>() {
        {
            put("image/png", ".png");
            put("image/jpg", ".jpg");
            put("image/gif", ".gif");
            put("image/jpeg", ".jpeg");
        }
    };

    public SaveImage(@Qualifier("imageUrl") String url, @Qualifier("saveImagePath") String savePath) {
        super(url, savePath);
    }

    @Override
    public HashMap<String, String> getContentType() {
        return CONTENT_TYPE;
    }
}
