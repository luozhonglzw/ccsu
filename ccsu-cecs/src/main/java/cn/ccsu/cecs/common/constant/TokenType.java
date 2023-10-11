package cn.ccsu.cecs.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TokenType {
    /**
     * token类别
     */
    TEACHER_TOKEN("teachertoken"),
    STUDENT_TOKEN("studenttoken");

    private String type;
}