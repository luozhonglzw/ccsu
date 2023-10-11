package cn.ccsu.cecs.common.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存放工程整体需要的常量
 *
 * @author ccsu-cecs
 */
public class ProjectConstant {

    @AllArgsConstructor
    @Getter
    public enum DeletedStatusEnum {
        /**
         * 显示状态
         */
        DELETED(1, "已删除"),
        NOT_DELETED(0, "未删除");

        private int code;
        private String message;
    }

    /**
     * 一个异步任务的名称
     */
    public static final String BASE_ASYNC = "base";
    /**
     * 多个异步任务的名称
     */
    public static final String BLEND_ASYNC = "blend";

    /**
     * 最大的查询超时的次数
     */
    public static final Integer MAX_QUERY_ERROR_COUNT = 5;

    /**
     * 学生默认密码
     */
    public static final String STUDENT_DEFAULT_PASSWORD = "123456";

    /**
     * 统一的线上审核意见
     */
    public static final String UNITE_APPROVAL_COMMENTS = "线上统一审核";

    /**
     * 上传文件大小
     */
    public static final Long FILE_UPLOAD_SIZE = 1024 * 1024 * 5L;
}
