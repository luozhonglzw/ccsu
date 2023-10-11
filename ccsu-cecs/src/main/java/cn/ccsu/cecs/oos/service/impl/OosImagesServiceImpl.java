package cn.ccsu.cecs.oos.service.impl;

import cn.ccsu.cecs.bonus.entity.BonusApplyImage;
import cn.ccsu.cecs.bonus.service.IBonusApplyImageService;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.misc.GlobalTimeManage;
import cn.ccsu.cecs.common.oos.Oos;
import cn.ccsu.cecs.common.oos.image.SaveImage;
import cn.ccsu.cecs.common.response.ResponseResult;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.common.utils.Query;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.config.Config;
import cn.ccsu.cecs.oos.entity.OosFileInfo;
import cn.ccsu.cecs.oos.entity.OosImages;
import cn.ccsu.cecs.oos.mapper.OosImagesMapper;
import cn.ccsu.cecs.oos.service.IOosImagesService;
import cn.ccsu.cecs.student.vo.StudentLoginVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 * 图片资源表 服务实现类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */
@Slf4j
@Service
public class OosImagesServiceImpl extends ServiceImpl<OosImagesMapper, OosImages> implements IOosImagesService {

    private final SaveImage saveImage;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private IBonusApplyImageService bonusApplyImageService;

    public OosImagesServiceImpl(SaveImage saveImage) {
        this.saveImage = saveImage;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OosImages> page = this.page(
                new Query<OosImages>().getPage(params),
                new QueryWrapper<OosImages>()
        );

        return new PageUtils(page);
    }

    @Autowired
    RestTemplate restTemplate;

    @Override
    public ResponseResult uploadFile(MultipartFile target, StudentLoginVo studentLoginVo) {
        Oos oos = saveImage.checkout(target, studentLoginVo.getStuNumber());
        log.info("{}", oos);
        ResponseResult result = new ResponseResult();
        result.status = saveImage.saveOos(target, oos);
        result.data = new HashMap<String, Object>() {{
            put("oos", oos);
        }};
        return result;
    }

    @Override
    public ResponseResult uploadFileNoWithData(MultipartFile target, StudentLoginVo studentLoginVo) {
        Oos oos = saveImage.checkout(target, studentLoginVo.getStuNumber());
        log.info("{}", oos);
        ResponseResult result = new ResponseResult();
        result.status = true;
        result.data = new HashMap<String, Object>() {{
            put("oos", oos);
        }};
        return result;
    }

    @Override
    public OosFileInfo getFileIO(String filePath) throws IOException {
        // 切割文件url
        String[] pathSplit = filePath.split("/");

        // 得到文件x信息
        String fileInfo = pathSplit[pathSplit.length - 1];
        String[] fileInfoSplit = fileInfo.split("\\.");

        // 得到下载路径
        String fileRootPath;

        // 不同的操作系统，文件夹的\/有区别
        String os = System.getProperty("os.name");
        //Linux操作系统
        if (os != null && os.toLowerCase().startsWith("linux")) {
            fileRootPath = Config.FILE_ROOT_PATH.replace("/oos/images/", "");
        } else {
            fileRootPath = Config.FILE_ROOT_PATH.replace("\\oos\\images\\", "");
        }

        String downloadPath = StringUtils.join(fileRootPath.replace("\\", "/"), filePath);

        BufferedInputStream bufferedInputStream;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(downloadPath)));
        } catch (IOException e) {
            throw new IOException("获取文件异常");
        }

        return new OosFileInfo(fileInfo, fileInfoSplit[1], bufferedInputStream);
    }

    @Override
    public void downloadApply(Integer oosImagesId, HttpServletResponse response) throws IOException {
        OosImages oosImages = getById(oosImagesId);

        String filePath = oosImages.getUrl();
        if (StringUtils.isEmpty(filePath)) {
            Gson gson = new Gson();
            String rJson = gson.toJson(R.error("未找到该文件"));
            response.getWriter().write(rJson);
        }

        OosFileInfo oosFileInfo = getFileIO(filePath);
        InputStream inputStream = oosFileInfo.getInputStream();
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();

        BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());

        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + oosFileInfo.getFileName());
        outputStream.write(buffer);
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public void lookApplyImage(Integer oosImagesId, HttpServletResponse response) throws IOException {
        OosImages oosImages = getById(oosImagesId);
        String filePath = oosImages.getUrl();

        if (StringUtils.isEmpty(filePath)) {
            Gson gson = new Gson();
            String rJson = gson.toJson(R.error("未找到该文件"));
            response.getWriter().write(rJson);
        }

        OosFileInfo oosFileInfo = getFileIO(filePath);
        InputStream inputStream = oosFileInfo.getInputStream();
        byte[] buffer = new byte[inputStream.available()];
        inputStream.read(buffer);
        inputStream.close();

        BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());

        response.reset();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("image/png");
        outputStream.write(buffer);
        outputStream.flush();
        outputStream.close();
    }

}
