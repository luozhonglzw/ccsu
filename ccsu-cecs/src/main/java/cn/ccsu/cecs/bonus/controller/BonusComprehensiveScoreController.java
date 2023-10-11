package cn.ccsu.cecs.bonus.controller;

import cn.ccsu.cecs.bonus.service.IBonusComprehensiveScoreService;
import cn.ccsu.cecs.bonus.vo.StuScoreVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.FusingCurrent;
import cn.ccsu.cecs.common.annotation.RedisCache;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.common.utils.JsonR;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.common.utils.RedisUtils;
import cn.ccsu.cecs.student.entity.StuStudent;
import cn.ccsu.cecs.student.service.IStuStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 综合成绩表 前端控制器
 * </p>
 *
 * @author ccsu-cecs
 * @since 2022-03-10
 */
@RestController
@RequestMapping("/student/bonus-comprehensive-score")
public class BonusComprehensiveScoreController {

    @Autowired
    private IBonusComprehensiveScoreService bonusComprehensiveScoreService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    IStuStudentService stuStudentService;

    /**
     * 查询学生个人成绩表（根据学年）
     *
     * @param yearId  学年id
     * @param request 请求
     * @return 结果
     */
    @RedisCache(key = RedisKeyConstant.STUDENT_SCORE_PERSON_BY_YEAR_SINGLE_KEY)
    @CatchException(value = "查询学生个人成绩表失败")
    @RequestMapping("/info")
    public R info(@RequestParam(value = "yearId") Integer yearId, HttpServletRequest request) {
        String id = JWTUtils.getPayload(request, "id");

        StuScoreVo stuScoreVo = bonusComprehensiveScoreService.getStudentPersonScore(Integer.parseInt(id), yearId);
        return R.ok().put("data", stuScoreVo);
    }

    /**
     * 根据不同类别获取学生信息条数
     *
     * @param category 类别
     * @param request  请求
     * @return 条数
     */
    @CatchException(value = "获取学生信息条数失败")
    @RequestMapping("/rows/{category}")
    public R rows(@PathVariable("category") String category,
                  @RequestParam("yearId") Integer yearId,
                  HttpServletRequest request) {
        String id = JWTUtils.getPayload(request, "id");

        /**
         *  key:stu:score:rows:category_year_stu_id
         *  {
         *      [类别id]_[学年id]_[学生id]   xxx
         *  }
         *  这个redis的key采用hash做
         */
        // 1.先拼接redisKey
        String redisKey = RedisKeyConstant.STUDENT_SCORE_ROWS_KEY;

        // 1.查询redis
        String hashKey = category + "_" + yearId + "_" + id;
        Long r = redisUtils.hashOperationGet(redisKey, hashKey, Long.class);

        if (r == null) {
            Long count = bonusComprehensiveScoreService.getStuInfoRows(yearId, Integer.parseInt(id), category);
            if (count == null) {
                redisUtils.hashOperationSet(redisKey, hashKey, null);
                return R.ok().put("data", null);
            }
            redisUtils.hashOperationSet(redisKey, hashKey, count);
            return R.ok().put("data", count);
        } else {
            return R.ok().put("data", r);
        }
    }

    /**
     * 获取学生班级成绩信息 - 分页
     *
     * @param yearId  学年id
     * @param page    页码
     * @param limit   条数
     * @param request 请求
     * @return 结果
     */
//    @FusingCurrent
    @CatchException(value = "获取学生班级分页信息失败")
    @RequestMapping("/info/class-page")
    public R classPage(@RequestParam("yearId") Integer yearId,
                       @RequestParam("page") String page,
                       @RequestParam("limit") String limit,
                       HttpServletRequest request) {
        String id = JWTUtils.getPayload(request, "id");

        Map<String, Object> params = new HashMap<>() {{
            put("page", page);
            put("limit", limit);
        }};

        /**
         *  key:stu:score:year_class
         *  {
         *      [学年id]_[年级id]_[班级id]_[页码]_[条数]   xxx
         *  }
         *  这个redis的key采用hash做
         */
        // 1.先拼接redisKey
        StuStudent stuStudent = stuStudentService.getById(id);
        String redisKey = RedisKeyConstant.STUDENT_SCORE_YEAR_CLASS_LIST_KEY;

        // 2.查询redis
        String hashKey = yearId + "_" + stuStudent.getGradeId() + "_" + stuStudent.getClassId() + "_" + page + "_" + limit;
        JsonR r = redisUtils.hashOperationGet(redisKey, hashKey, JsonR.class);

        if (r == null) {
            List<StuScoreVo> stuScoreVos = bonusComprehensiveScoreService.getStuClassTotalScoreByYearPage(Integer.parseInt(id), yearId, params);
            if (stuScoreVos == null || stuScoreVos.size() == 0) {
                redisUtils.hashOperationSet(redisKey, hashKey, new JsonR());
                return R.ok().put("data", null);
            }
            redisUtils.hashOperationSet(redisKey, hashKey, new JsonR("success", 0, stuScoreVos));
            return R.ok().put("data", stuScoreVos);
        } else {
            return R.ok().put("data", r.getData());
        }
    }


    /**
     * 获取学生专业成绩信息 - 分页
     *
     * @param yearId  学年id
     * @param page    页码
     * @param limit   条数
     * @param request 请求
     * @return 结果
     */
    @CatchException(value = "获取学生专业分页信息失败")
    @RequestMapping("/info/profession-page")
    public R professionPage(@RequestParam("yearId") Integer yearId,
                            @RequestParam("page") String page,
                            @RequestParam("limit") String limit,
                            HttpServletRequest request) {
        String id = JWTUtils.getPayload(request, "id");

        Map<String, Object> params = new HashMap<>() {{
            put("page", page);
            put("limit", limit);
        }};

        /**
         *  key:stu:score:year_profession
         *  {
         *      [学年id]_[年级id]_[专业id]_[页码]_[条数]   xxx
         *  }
         *  这个redis的key采用hash做
         */
        // 1.先拼接redisKey
        StuStudent stuStudent = stuStudentService.getById(id);
        String redisKey = RedisKeyConstant.STUDENT_SCORE_YEAR_PROFESSION_LIST_KEY;

        // 1.查询redis
        String hashKey = yearId + "_" + stuStudent.getGradeId() + "_" + stuStudent.getProfessionId() + "_" + page + "_" + limit;
        JsonR r = redisUtils.hashOperationGet(redisKey, hashKey, JsonR.class);

        if (r == null) {
            List<StuScoreVo> stuScoreVos = bonusComprehensiveScoreService.getStuProfessionTotalScoreByYearPage(Integer.parseInt(id), yearId, params);
            if (stuScoreVos == null || stuScoreVos.size() == 0) {
                redisUtils.hashOperationSet(redisKey, hashKey, new JsonR());
                return R.ok().put("data", null);
            }
            redisUtils.hashOperationSet(redisKey, hashKey, new JsonR("success", 0, stuScoreVos));
            return R.ok().put("data", stuScoreVos);
        } else {
            return R.ok().put("data", r.getData());
        }
    }
}
