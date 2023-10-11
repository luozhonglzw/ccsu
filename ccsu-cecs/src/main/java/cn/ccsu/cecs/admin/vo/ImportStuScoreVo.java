package cn.ccsu.cecs.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 导入学生成绩Vo
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ImportStuScoreVo {
    // 学年id
    private Integer yearId;
    // 类别id
    private Integer categoryId;
    // 文件
    private MultipartFile file;
}
