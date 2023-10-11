CREATE DATABASE `ccsu-cecs` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin;

-- `ccsu-cecs`.admin_user definition

CREATE TABLE `admin_user`
(
    `id`         int                             NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`   varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '账号',
    `password`   varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '密码(需要用md5加密.注意，这是敏感字段，在转换为json时，需要忽略该字段)',
    `name`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '姓名',
    `created_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
    `updated_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
    `deleted`    int                                                   DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `admin_user_pk` (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='学院表';


-- `ccsu-cecs`.bonus_apply definition

CREATE TABLE `bonus_apply`
(
    `id`                int           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `year_id`           int           NOT NULL COMMENT '学年id(用于查询优化)',
    `category_id`       int           NOT NULL COMMENT '类别id(用于查询优化)',
    `bonus_id`          int           NOT NULL COMMENT '加分项id',
    `stu_student_id`    int           NOT NULL COMMENT '学生id',
    `score`             decimal(6, 2) NOT NULL                                 DEFAULT '0.00' COMMENT '成绩(把该加分项的分数，赋值给本成绩，可能会出现负数，因为有惩戒减分，学习成绩不会使用该加分项的分数，学习成绩就是直接使用导入时excel的成绩)',
    `remark`            varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注(申请备注)',
    `approval`          int                                                    DEFAULT '0' COMMENT '审批状态',
    `approval_by`       varchar(64) COLLATE utf8mb4_bin                        DEFAULT NULL COMMENT '审批人',
    `approval_at`       datetime                                               DEFAULT NULL COMMENT '审批时间',
    `approval_comments` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '审批意见',
    `created_at`        datetime                                               DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '记录创建人',
    `updated_at`        datetime                                               DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by`        varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  DEFAULT NULL COMMENT '记录修改人',
    `deleted`           int                                                    DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='加分申请表';


-- `ccsu-cecs`.bonus_apply_image definition

CREATE TABLE `bonus_apply_image`
(
    `id`             int NOT NULL AUTO_INCREMENT COMMENT '主键',
    `bonus_apply_id` int NOT NULL COMMENT '申请id(相同的一次申请，最多允许有三个图片.)',
    `oos_images_id`  int NOT NULL COMMENT '图片资源id',
    `created_at`     datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
    `updated_at`     datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
    `deleted`        int                                                   DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='申请材料表';


-- `ccsu-cecs`.bonus_bonus definition

CREATE TABLE `bonus_bonus`
(
    `id`          int                                                    NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category_id` int                                                    NOT NULL COMMENT '类别id',
    `name`        varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '加分项名称(惩戒减分项目，也是这个字段)',
    `score`       decimal(6, 2)                                          NOT NULL DEFAULT '0.00' COMMENT '该加分项，单次所加分数(由于有惩戒减分，所以会有负数加分项)',
    `max_times`   int                                                    NOT NULL COMMENT '该加分项的最多申请次数',
    `illustrate`  text CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT '说明',
    `remark`      varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '备注',
    `created_at`  datetime                                                        DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin           DEFAULT NULL COMMENT '记录创建人',
    `updated_at`  datetime                                                        DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin           DEFAULT NULL COMMENT '记录修改人',
    `deleted`     int                                                             DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='加分项表';


-- `ccsu-cecs`.bonus_category definition

CREATE TABLE `bonus_category`
(
    `id`         int                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '综测加分类别(如:学习成绩、素拓成绩、实际与创新能力成绩)',
    `weights`    decimal(6, 2)                                         NOT NULL DEFAULT '0.00' COMMENT '该类别所代表的权重(如:学习成绩占60%,素拓成绩占20%,实际与创新能力成绩占20%)',
    `created_at` datetime                                                       DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '记录创建人',
    `updated_at` datetime                                                       DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin          DEFAULT NULL COMMENT '记录修改人',
    `deleted`    int                                                            DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='加分项类别表';


-- `ccsu-cecs`.bonus_comprehensive_score definition

CREATE TABLE `bonus_comprehensive_score`
(
    `id`             int           NOT NULL AUTO_INCREMENT COMMENT '主键',
    `year_id`        int           NOT NULL COMMENT '学年id(用于查询优化)',
    `college_id`     int           NOT NULL COMMENT '学院id(用于查询优化)',
    `grade_id`       int           NOT NULL COMMENT '年级id(用于查询优化)',
    `profession_id`  int           NOT NULL COMMENT '专业id(用于查询优化)',
    `class_id`       int           NOT NULL COMMENT '班级id(用于查询优化)',
    `stu_student_id` int           NOT NULL COMMENT '学生id',
    `score`          decimal(6, 2) NOT NULL                                DEFAULT '0.00' COMMENT '本学年的最终综合成绩(该成绩是三大类成绩乘以权重后求和计算出来的)',
    `created_at`     datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
    `updated_at`     datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by`     varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
    `deleted`        int                                                   DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='综合成绩表';


-- `ccsu-cecs`.bonus_year definition

CREATE TABLE `bonus_year`
(
    `id`         int                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '学年名称(如:2020-2021学年，这个时间一般是指2020年9月至2021年7月，我们它称作为一学年)',
    `created_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
    `updated_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
    `deleted`    int                                                   DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='学年表';


-- `ccsu-cecs`.oos_images definition

CREATE TABLE `oos_images`
(
    `id`         int                             NOT NULL AUTO_INCREMENT COMMENT '主键',
    `md5`        varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '图片的md5(前端在上传图片之前，先计算一下图片的md5，我这边会提供demo，如果图片md5值与db能匹配上，就可以不用上传图片了，只需要把图片更新一条引用记录到申请材料表中即可)',
    `url`        text COLLATE utf8mb4_bin        NOT NULL COMMENT '图片url',
    `path`       text COLLATE utf8mb4_bin        NOT NULL COMMENT '图片保存在服务器上的绝对路径(注意，这是敏感字段，在转换为json时，需要忽略该字段)',
    `created_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
    `updated_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
    `deleted`    int                                                   DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `oos_images_un` (`md5`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='图片资源表';


-- `ccsu-cecs`.stu_class definition

CREATE TABLE `stu_class`
(
    `id`         int                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `cl_name`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '班级名称',
    `created_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
    `updated_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
    `deleted`    int                                                   DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='班级表';


-- `ccsu-cecs`.stu_college definition

CREATE TABLE `stu_college`
(
    `id`         int                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`       varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '学院名称',
    `created_at` datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
    `updated_at` datetime                        DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
    `deleted`    int                             DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 13
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='学院表';


-- `ccsu-cecs`.stu_grade definition

CREATE TABLE `stu_grade`
(
    `id`         int                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gd_name`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '年级',
    `created_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
    `updated_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
    `deleted`    int                                                   DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='年级表';


-- `ccsu-cecs`.stu_profession definition

CREATE TABLE `stu_profession`
(
    `id`         int                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `pf_name`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '专业名称',
    `created_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
    `updated_at` datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
    `deleted`    int                                                   DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='专业表';


-- `ccsu-cecs`.stu_student definition

CREATE TABLE `stu_student`
(
    `id`            int                                                   NOT NULL AUTO_INCREMENT COMMENT '主键',
    `college_id`    int                                                   NOT NULL COMMENT '学院id',
    `profession_id` int                                                   NOT NULL COMMENT '专业id',
    `class_id`      int                                                   NOT NULL COMMENT '班级id',
    `stu_name`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '姓名',
    `stu_number`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '学号',
    `password`      varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT '密码',
    `created_at`    datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
    `created_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
    `updated_at`    datetime                                              DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
    `updated_by`    varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
    `deleted`       int                                                   DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `stu_student_un` (`stu_number`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_bin COMMENT ='学生表';


-- 选择mysql数据库
use
    mysql;
-- 创建学生mysql账号并赋予权限
create
    user 'student'@'%' identified by 'set you password';
grant
    select,
        update,
        insert
        on `ccsu-cecs`.`stu_student` to 'student'@'%';
grant
    select,
        update,
        insert
        on `ccsu-cecs`.`stu_college` to 'student'@'%';
grant
    select,
        update,
        insert
        on `ccsu-cecs`.`stu_profession` to 'student'@'%';
grant
    select,
        update,
        insert
        on `ccsu-cecs`.`bonus_apply` to 'student'@'%';
grant
    select,
        update,
        insert
        on `ccsu-cecs`.`bonus_apply_image` to 'student'@'%';
grant
    select
        on `ccsu-cecs`.`bonus_bonus` to 'student'@'%';
grant
    select
        on `ccsu-cecs`.`bonus_category` to 'student'@'%';
grant
    select
        on `ccsu-cecs`.`bonus_year` to 'student'@'%';
grant
    select,
        update,
        insert
        on `ccsu-cecs`.`oos_images` to 'student'@'%';
-- 创建老师mysql账号并赋予权限
create
    user 'teacher'@'%' identified by 'set you password';
grant
    select,
        update,
        insert
        on `ccsu-cecs`.* to 'teacher'@'%';
-- 刷新MySQL的系统权限相关表，使添加用户操作生效，以免会出现拒绝访问
flush
    privileges;