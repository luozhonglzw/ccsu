package cn.ccsu.cecs.admin.schedule;

import cn.ccsu.cecs.bonus.service.IBonusComprehensiveScoreService;
import cn.ccsu.cecs.bonus.vo.YearVo;
import cn.ccsu.cecs.common.cache.DefaultCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class RefreshController {

    @Autowired
    IBonusComprehensiveScoreService bonusComprehensiveScoreService;

    @Autowired
    DefaultCache defaultCache;

    @Autowired
    ThreadPoolExecutor executor;

    /**
     * 定时刷新学生成绩
     */
    @Scheduled(cron = "0 0 5 * * ?")  // 每天凌晨5点刷新加分申请表
    public void refresh() {
        log.info("开始刷新学生成绩....");
        List<YearVo> allYear = defaultCache.getYearVoCache();
        for (YearVo yearVo : allYear) {
            // 刷新学生成绩
            CompletableFuture.runAsync(() -> {
                bonusComprehensiveScoreService.refreshStuScore(yearVo.getYearId());
            }, executor);
        }
    }

}
