package cn.ccsu.cecs.common.oos;

import cn.ccsu.cecs.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@Slf4j
@Component
public abstract class SaveOos {
    // url
    private final String url;
    // savePath
    private final String savePath;

    /**
     * 构造器
     *
     * @param url      访问url
     * @param savePath 保存路径
     */
    public SaveOos(@Value("") String url, @Value("") String savePath) {
        this.url = url;
        this.savePath = savePath;
    }

    /**
     * 文件类型
     *
     * @return 文件类型
     */
    public abstract HashMap<String, String> getContentType();

    public void print() {
        log.info("{}", url);
        log.info("{}", savePath);
        log.info("{}", getContentType());
    }

    // 校验文件大小、类型
    public Oos checkout(MultipartFile target, String owner) {
        Oos oos = new Oos();
        boolean flag = false;
        if (target.getSize() > 0) {
            for (String type : getContentType().keySet()) {
                if (StringUtils.equals(type, target.getContentType())) {
                    flag = true;
                    // 设置 校验通过
                    oos.setResult(true);
                    // 设置 图片名字
                    oos.setName(StringUtils.join(getUUID(), getContentType().get(type)));
                    // 设置 保存路径
                    // 格式  oos\images\学号\文件名
                    String pathInfo;
                    if (StringUtils.isEmpty(owner)) {
                        pathInfo = oos.getName();
                    } else {
                        String path = StringUtils.join(owner, Config.FILES_SEPARATOR);
                        // 创建文件夹
                        Config.createSaveImagePath(
                                StringUtils.join(Config.SAVE_OOS_ROOT_PATH, "images", Config.FILES_SEPARATOR, path));

                        pathInfo = StringUtils.join(path, oos.getName());
                    }

                    oos.setSavePath(StringUtils.join(savePath, pathInfo));
                    // 设置 存储url
                    oos.setDbUrl(StringUtils.join(url, pathInfo.replace(Config.FILES_SEPARATOR, "/")));
                    try {
                        // 设置 md5值
                        oos.setMd5(DigestUtils.md5DigestAsHex(target.getBytes()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        if (!flag) {
            throw new RuntimeException("上传的文件格式不合法");
        }
        return oos;
    }

    // 校验文件大小、类型
    public Oos checkout(MultipartFile target) {
        return this.checkout(target, null);
    }

    /**
     * 保存oos
     *
     * @param file 文件
     * @param oos  oos对象
     * @return true or false
     */
    public boolean saveOos(MultipartFile file, Oos oos) {
        boolean result = false;
        try {
            file.transferTo(new File(oos.getSavePath()));
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    // 获取 UUID
    protected String getUUID() {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }
}
