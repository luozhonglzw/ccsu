package cn.ccsu.cecs.bonus.controller.teacher;

import cn.ccsu.cecs.bonus.service.IBonusApplyService;
import cn.ccsu.cecs.bonus.vo.teacher.VerifyVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.PrintTeacherInfo;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.oos.service.IOosImagesService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/teacher/approval")
public class TeacherApproveController {

    @Autowired
    IBonusApplyService bonusApplyService;

    @Autowired
    IOosImagesService oosImagesService;

    /**
     * 管理员审批加分申请表
     *
     * @param verifyVo 审核对象Vo
     * @return 审核结果
     */
    @PrintTeacherInfo(value = "管理员审核加分申请表")
    @CatchException(value = "审核加分申请表异常")
    @PostMapping("/verify")
    public R approval(@RequestBody VerifyVo verifyVo, HttpServletRequest request) {
        // 利用jwt从请求头中获取认证的管理员信息
        String name = JWTUtils.getPayload(request, "name");

        // 管理员审核加分申请表
        bonusApplyService.verify(name, verifyVo);

        return R.ok("审核成功!");
    }

    /**
     * 已转至文件服务器
     */
//    /**
//     * 管理员查看申请文件
//     */
//    @CatchException(value = "查看申请文件异常")
//    @GetMapping("/look-apply-image")
//    public void lookApplyImage(@RequestParam("oosImagesId") Integer oosImagesId,
//                               HttpServletResponse response) throws IOException {
//
//        // 查看学生文件（只需要oosImages_id）
//        oosImagesService.lookApplyImage(oosImagesId, response);
//    }

}
