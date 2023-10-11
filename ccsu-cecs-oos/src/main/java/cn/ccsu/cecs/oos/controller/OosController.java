package cn.ccsu.cecs.oos.controller;


import cn.ccsu.cecs.oos.common.constant.TokenType;
import cn.ccsu.cecs.oos.common.entity.StudentLoginVo;
import cn.ccsu.cecs.oos.common.interceptor.OosInterceptor;
import cn.ccsu.cecs.oos.common.response.ResponseResult;
import cn.ccsu.cecs.oos.common.utils.JWTUtils;
import cn.ccsu.cecs.oos.entity.OosImage;
import cn.ccsu.cecs.oos.service.IOosImagesService;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/oos/oos-images")
public class OosController {

    @Autowired
    IOosImagesService oosImagesService;

    @PostMapping("/upload")
    public ResponseResult upload(
            @RequestPart(value = "file", required = false) MultipartFile multipartFile,
            @RequestPart("user") StudentLoginVo user) {

        ResponseResult result = oosImagesService.uploadFile(multipartFile, user);
        log.info("上传文件成功 —> user:{}, fileName:{}", user, multipartFile.getOriginalFilename());
        return result;
    }

    /**
     * 用户查看申请文件
     *
     * @param oosImagesId 图片id
     * @param response    流，下载图片
     * @throws IOException 文件IO异常
     */
    @GetMapping("/look")
    public void look(@RequestParam("oosImagesId") Integer oosImagesId,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException {
//        checkUser(request, oosImagesId);

        oosImagesService.lookApplyImage(oosImagesId, response);
        log.info("用户查看文件 info: {}", OosInterceptor.threadLocal.get());
    }

    /**
     * 学生下载申请文件
     *
     * @param response 流，下载图片
     * @throws IOException 文件IO异常
     */
    @GetMapping("/download")
    public void download(@RequestParam("oosImagesId") Integer oosImagesId,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        checkUser(request, oosImagesId);

        oosImagesService.downloadApply(oosImagesId, response);
        log.info("用户下载文件 info: {}", OosInterceptor.threadLocal.get());
    }

    /**
     * 获取加分项图片信息
     */
    @GetMapping("/look-bonus-image")
    public void lookBonusImage(@RequestParam("oosImagesId") Integer oosImagesId,
                               HttpServletResponse response) throws IOException {
        // 查看学生文件（只需要oosImages_id）-
        oosImagesService.lookApplyImage(oosImagesId, response);
        log.info("用户查看加分项图片信息 info: {}", OosInterceptor.threadLocal.get());
    }


    /**
     * ①检查token有效性
     * ②检查是否是同一用户
     *
     * @param request     请求
     * @param oosImagesId 图片id
     */
    public void checkUser(HttpServletRequest request, Integer oosImagesId) {
        // 获取请求头中的令牌
        String studentToken = request.getHeader(TokenType.STUDENT_TOKEN.getType());
        String teacherToken = request.getHeader(TokenType.TEACHER_TOKEN.getType());

        try {
            if (teacherToken != null && !teacherToken.equals("null") && !teacherToken.equals("")) {
                JWTUtils.verify(teacherToken, TokenType.TEACHER_TOKEN);
                return;
            }
            if (studentToken != null && !studentToken.equals("null") && !studentToken.equals("")) {
                // 验证toke令牌
                JWTUtils.verify(studentToken, TokenType.STUDENT_TOKEN);

                // 用户只能访问到自己的文件
                String stuName = JWTUtils.getStuPayload(request, "stuName");
                OosImage oosImage = oosImagesService.getById(oosImagesId);
                if (!oosImage.getCreatedBy().equals(stuName)) {
                    throw new RuntimeException("token无效");
                } else {
                    return;
                }
            }
            throw new RuntimeException("token无效");
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("签名不一致");
        } catch (TokenExpiredException e) {
            throw new RuntimeException("令牌过期");
        } catch (AlgorithmMismatchException e) {
            throw new RuntimeException("算法不匹配");
        } catch (InvalidClaimException e) {
            throw new RuntimeException("失效的payload");
        } catch (Exception e) {
            throw new RuntimeException("token无效");
        }
    }
}
