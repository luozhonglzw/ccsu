package cn.ccsu.cecs.admin.service.impl;

import cn.afterturn.easypoi.entity.vo.NormalExcelConstants;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.view.PoiBaseView;
import cn.ccsu.cecs.admin.service.AdminExportService;
import cn.ccsu.cecs.admin.vo.excel.ExportStuScore;
import cn.ccsu.cecs.bonus.service.IBonusYearService;
import cn.ccsu.cecs.bonus.vo.BonusCategoryScoreVo;
import cn.ccsu.cecs.bonus.vo.StuScoreVo;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.common.cache.DefaultCache;
import cn.ccsu.cecs.common.constant.BonusConstant;
import cn.ccsu.cecs.common.constant.ProjectConstant;
import cn.ccsu.cecs.common.misc.GlobalExecutor;
import cn.ccsu.cecs.common.utils.R;
import cn.ccsu.cecs.student.entity.StuGrade;
import cn.ccsu.cecs.student.entity.StuProfession;
import cn.ccsu.cecs.student.service.IStuGradeService;
import cn.ccsu.cecs.student.service.IStuProfessionService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class AdminExportServiceImpl implements AdminExportService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    IBonusYearService bonusYearService;

    @Autowired
    IStuGradeService stuGradeService;

    @Autowired
    IStuProfessionService stuProfessionService;

    @Autowired
    DefaultCache defaultCache;

    @Value("${server.port}")
    String serverPort;



    private static final String EXCEL_SEPARATOR = "_";

    private static final String SHEET_NAME = "学生成绩";

    @Override
    public void exportStuScore(Integer yearId, Integer gradeId, Integer professionId, ModelMap map,
                               HttpServletRequest request, HttpServletResponse response) {
        List<ExportStuScore> exportStuScores = new ArrayList<>();
        // 发送rest请求，拿到学生成绩信息
        R r = restTemplate.getForObject("http://localhost:" + serverPort +
                "/teacher/bonus-comprehensive-score/export-info?" +
                "yearId=" + yearId +
                "&gradeId=" + gradeId +
                "&professionId=" + professionId, R.class);
        ArrayList arrayList = (ArrayList) r.get("data");

        Gson gson = new Gson();
        List<StuScoreVo> stuScoreVos = gson.fromJson(String.valueOf(arrayList), new TypeToken<List<StuScoreVo>>() {
        }.getType());

        if (stuScoreVos == null || stuScoreVos.size() == 0) {
            throw new RuntimeException("学生成绩信息为空");
        }

        String yearName = defaultCache.getYearVo(yearId).getYearName();
        String gradeName = defaultCache.getIdAndGradeNameMap().get(gradeId);
        String professionName = defaultCache.getIdAndProfessionNameMap().get(professionId);

        // 拼接文件名和文件抬头
        String fileNameAndRise = yearName + EXCEL_SEPARATOR +
                gradeName + EXCEL_SEPARATOR +
                professionName + EXCEL_SEPARATOR + "成绩表";

        // 遍历每个班级的学生
        for (StuScoreVo scoreVo : stuScoreVos) {
            ExportStuScore exportStuScore = new ExportStuScore();
            exportStuScore.setYearName(scoreVo.getYearName());
            exportStuScore.setStuName(scoreVo.getStuName());
            exportStuScore.setStuNumber(scoreVo.getStuNumber());
            exportStuScore.setProfessionName(scoreVo.getProfessionName());
            exportStuScore.setClassName(scoreVo.getClassName());
            exportStuScore.setCollegeName(scoreVo.getCollegeName());
            exportStuScore.setTotalScore(scoreVo.getStuScore());
            exportStuScore.setRank(scoreVo.getRank());
            List<BonusCategoryScoreVo> bonusCategoryScoreVos = scoreVo.getBonusCategoryScoreVos();

            // 先进行分数初始化
            exportStuScore.setProfessionScore(new BigDecimal("0.00"));
            exportStuScore.setPracticeInnovationScore(new BigDecimal("0.00"));
            exportStuScore.setBasicQualityScore(new BigDecimal("0.00"));
            if (bonusCategoryScoreVos != null && bonusCategoryScoreVos.size() > 0) {
                // 遍历，分类进行分数分发
                for (BonusCategoryScoreVo bonusCategoryScoreVo : bonusCategoryScoreVos) {
                    if (Objects.equals(bonusCategoryScoreVo.getCategoryVo().getId(), BonusConstant.CategoryWeightEnum.BASE_SCORE.getCode())) {
                        exportStuScore.setProfessionScore(bonusCategoryScoreVo.getScore());
                    } else if (Objects.equals(bonusCategoryScoreVo.getCategoryVo().getId(), BonusConstant.CategoryWeightEnum.PRACTICE_INNOVATION_SCORE.getCode())) {
                        exportStuScore.setPracticeInnovationScore(bonusCategoryScoreVo.getScore());
                    } else if (Objects.equals(bonusCategoryScoreVo.getCategoryVo().getId(), BonusConstant.CategoryWeightEnum.BASIC_QUALITY_ADD_SCORE.getCode())) {
                        exportStuScore.setBasicQualityScore(bonusCategoryScoreVo.getScore());
                    }
                }
            }
            exportStuScores.add(exportStuScore);
        }

        // 按照排名排序
        exportStuScores.sort(((o1, o2) -> {
            return o1.getRank().compareTo(o2.getRank());
        }));

        // TODO 这里可以做一下表格美化
        ExportParams params = new ExportParams(fileNameAndRise.replace(EXCEL_SEPARATOR, ""), SHEET_NAME, ExcelType.XSSF);
        //对导出结果进行自定义处理
        map.put(NormalExcelConstants.DATA_LIST, exportStuScores);
        map.put(NormalExcelConstants.CLASS, ExportStuScore.class);
        map.put(NormalExcelConstants.PARAMS, params);
        // 文件格式：学年_学院_年级_专业_成绩表     如：【2021-2022学年_19级_软件工程_成绩表】
        map.put(NormalExcelConstants.FILE_NAME, fileNameAndRise);

        PoiBaseView.render(map, request, response, NormalExcelConstants.EASYPOI_EXCEL_VIEW);
    }
}
