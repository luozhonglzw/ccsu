package cn.ccsu.cecs.oos.service;

import cn.ccsu.cecs.common.response.ResponseResult;
import cn.ccsu.cecs.common.utils.PageUtils;
import cn.ccsu.cecs.oos.entity.OosFileInfo;
import cn.ccsu.cecs.oos.entity.OosImages;
import cn.ccsu.cecs.student.vo.StudentLoginVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * <p>
 * 图片资源表 服务类
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-05
 */

public interface IOosImagesService extends IService<OosImages> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 上传用户申请文件
     *
     * @param target         申请文件
     * @param studentLoginVo 用户信息
     * @return 上传结果
     */
    ResponseResult uploadFile(MultipartFile target, StudentLoginVo studentLoginVo);

    ResponseResult uploadFileNoWithData(MultipartFile target, StudentLoginVo studentLoginVo);

    /**
     * 根据文件路径，获取申请文件的图片IO
     *
     * @param filePath 申请文件的图片路径
     * @return oos文件信息
     * @throws IOException io异常
     */
    OosFileInfo getFileIO(String filePath) throws IOException;

    /**
     * 下载用户申请文件
     *
     * @param oosImagesId 图片id
     * @param response    响应
     * @throws IOException
     */
    void downloadApply(Integer oosImagesId, HttpServletResponse response) throws IOException;

    /**
     * 查看用户申请文件
     *
     * @param oosImagesId 图片id
     * @param response    响应
     */
    void lookApplyImage(Integer oosImagesId, HttpServletResponse response) throws IOException;
}
