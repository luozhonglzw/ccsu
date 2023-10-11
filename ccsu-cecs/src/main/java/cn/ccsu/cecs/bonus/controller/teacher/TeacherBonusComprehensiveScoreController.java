package cn.ccsu.cecs.bonus.controller.teacher;

import cn.ccsu.cecs.bonus.service.IBonusComprehensiveScoreService;
import cn.ccsu.cecs.bonus.vo.StuScoreVo;
import cn.ccsu.cecs.common.annotation.CatchException;
import cn.ccsu.cecs.common.annotation.PrintTeacherInfo;
import cn.ccsu.cecs.common.constant.RedisKeyConstant;
import cn.ccsu.cecs.common.utils.JsonR;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 综合成绩表
 *
 * @author ccsu-cecs
 */
@Slf4j
@RestController
@RequestMapping("/teacher/bonus-comprehensive-score")
public class TeacherBonusComprehensiveScoreController {

    @Autowired
    private IBonusComprehensiveScoreService bonusComprehensiveScoreService;

    @Autowired
    RedisUtils redisUtils;

    /**
     * 老师端获取学生信息条数
     *
     * @param yearId       学年id
     * @param professionId 专业id
     * @param gradeId      年级id
     * @return 条数
     */
    @CatchException(value = "获取学生信息条数失败")
    @RequestMapping("/rows")
    public R rows(@RequestParam(value = "yearId") Integer yearId,
                  @RequestParam(value = "professionId") Integer professionId,
                  @RequestParam(value = "gradeId") Integer gradeId) {
        /**
         *  key:teacher:score:rows:year_profession_grade
         *  {
         *      [学年id]_[专业id]_[年级id]   xxx
         *  }
         *  这个redis的key采用hash做
         */
        // 1.先拼接redisKey
        String redisKey = RedisKeyConstant.TEACHER_SCORE_ROWS_KEY;

        // 1.查询redis
        String hashKey = yearId + "_" + professionId + "_" + gradeId;
        Long r = redisUtils.hashOperationGet(redisKey, hashKey, Long.class);

        if (r == null) {
            Long count = bonusComprehensiveScoreService.teacherGetStuInfoRows(yearId, professionId, gradeId);
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
     * 查询所有学生的成绩表（根据学年、年级、专业）
     *
     * @param yearId       学年id
     * @param professionId 专业id
     * @param gradeId      年级id
     * @return 结果
     */
    @CatchException(value = "查询学生个人成绩表失败")
    @RequestMapping("/info")
    public R info(@RequestParam(value = "yearId") Integer yearId,
                  @RequestParam(value = "professionId") Integer professionId,
                  @RequestParam(value = "gradeId") Integer gradeId,
                  @RequestParam(value = "page") String page,
                  @RequestParam(value = "limit") String limit) {
        Map<String, Object> params = new HashMap<>() {{
            put("page", page);
            put("limit", limit);
        }};

        /**
         *  key:  teacher:score:year_grade_profession:
         *  {
         *      [学年id]_[年级id]_[专业id]_[页码]_[条数]   xxx
         *  }
         *  这个redis的key采用hash做
         */
        // 1.先拼接redisKey
        String redisKey = RedisKeyConstant.TEACHER_SCORE_ALL_LIST_KEY;

        // 1.查询redis
        String hashKey = yearId + "_" + gradeId + "_" + professionId + "_" + page + "_" + limit;
        JsonR r = redisUtils.hashOperationGet(redisKey, hashKey, JsonR.class);

        if (r == null) {
            List<StuScoreVo> stuScoreVos = bonusComprehensiveScoreService.getStudentScore(yearId, gradeId, professionId, params);
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
     * 刷新学生成绩
     *
     * @param yearId 学年id
     * @return 结果
     */
    @PrintTeacherInfo(value = "管理员刷新学生成绩")
    @CatchException(value = "刷新学生成绩失败")
    @PostMapping("/refresh-student-score")
    public R refresh(@RequestParam("yearId") Integer yearId) {
        // 刷新学生成绩
        bonusComprehensiveScoreService.refreshStuScore(yearId);
        return R.ok("刷新成功");
    }

    /**
     * 查询所有学生的成绩表（根据学年、年级、专业）  -- 用于导出文件接口
     *
     * @param yearId       学年id
     * @param professionId 专业id
     * @param gradeId      年级id
     * @return 结果
     */
    @PrintTeacherInfo(value = "管理员查询所有学生成绩，并导出文件")
    @CatchException(value = "查询学生个人成绩表失败")
    @RequestMapping("/export-info")
    public R exportInfo(@RequestParam(value = "yearId") Integer yearId,
                        @RequestParam(value = "professionId") Integer professionId,
                        @RequestParam(value = "gradeId") Integer gradeId) {

        List<StuScoreVo> stuScoreVos = bonusComprehensiveScoreService.getStudentScore(yearId, gradeId, professionId, new HashMap<>());
        return R.ok().put("data", stuScoreVos);
    }

}
