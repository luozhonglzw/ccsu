/*
SQLyog Ultimate v10.00 Beta1
MySQL - 5.7.38 : Database - ccsu_cecs
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`ccsu_cecs` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `ccsu_cecs`;

/*Table structure for table `admin_user` */

DROP TABLE IF EXISTS `admin_user`;

CREATE TABLE `admin_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '账号',
  `password` varchar(64) COLLATE utf8mb4_bin NOT NULL COMMENT '密码(需要用md5加密.注意，这是敏感字段，在转换为json时，需要忽略该字段)',
  `name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '姓名',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `admin_user_pk` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='学院表';

/*Table structure for table `bonus_apply` */

DROP TABLE IF EXISTS `bonus_apply`;

CREATE TABLE `bonus_apply` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `year_id` int(11) NOT NULL COMMENT '学年id(用于查询优化)',
  `category_id` int(11) NOT NULL COMMENT '类别id(用于查询优化)',
  `bonus_id` int(11) NOT NULL COMMENT '加分项id',
  `stu_student_id` int(11) NOT NULL COMMENT '学生id',
  `score` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '成绩(把该加分项的分数，赋值给本成绩，可能会出现负数，因为有惩戒减分，学习成绩不会使用该加分项的分数，学习成绩就是直接使用导入时excel的成绩)',
  `remark` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注(申请备注)',
  `approval` int(11) DEFAULT '0' COMMENT '审批状态（0-未审批 1-已审批 -1-已驳回 2-预通过）',
  `approval_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '审批人',
  `approval_at` datetime DEFAULT NULL COMMENT '审批时间',
  `approval_comments` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '审批意见',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`),
  KEY `bonus_apply_year_id` (`year_id`),
  KEY `bonus_apply_approval` (`approval`),
  KEY `bonus_apply_stu_student_id` (`stu_student_id`)
) ENGINE=InnoDB AUTO_INCREMENT=307 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='加分申请表';

/*Table structure for table `bonus_apply_image` */

DROP TABLE IF EXISTS `bonus_apply_image`;

CREATE TABLE `bonus_apply_image` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `bonus_apply_id` int(11) DEFAULT NULL COMMENT '申请id(相同的一次申请，最多允许有三个图片.)',
  `oos_images_id` int(11) DEFAULT NULL COMMENT '图片资源id',
  `created_at` datetime DEFAULT NULL COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT NULL COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`),
  KEY `bonus_apply_id_image` (`bonus_apply_id`,`oos_images_id`),
  KEY `bonus_apply_id_oos_images` (`oos_images_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='申请材料表';

/*Table structure for table `bonus_bonus` */

DROP TABLE IF EXISTS `bonus_bonus`;

CREATE TABLE `bonus_bonus` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `category_id` int(11) DEFAULT NULL COMMENT '类别id',
  `name` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '加分项名称(惩戒减分项目，也是这个字段)',
  `score` decimal(6,2) DEFAULT '0.00' COMMENT '该加分项，单次所加分数(由于有惩戒减分，所以会有负数加分项)',
  `max_times` int(11) DEFAULT NULL COMMENT '该加分项的最多申请次数',
  `illustrate` text COLLATE utf8mb4_bin COMMENT '说明',
  `remark` varchar(128) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '备注',
  `created_at` datetime DEFAULT NULL COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT NULL COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='加分项表';

/*Table structure for table `bonus_category` */

DROP TABLE IF EXISTS `bonus_category`;

CREATE TABLE `bonus_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '综测加分类别(如:学习成绩、素拓成绩、实际与创新能力成绩)',
  `weights` decimal(6,2) DEFAULT '0.00' COMMENT '该类别所代表的权重(如:学习成绩占60%,素拓成绩占20%,实际与创新能力成绩占20%)',
  `created_at` datetime DEFAULT NULL COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT NULL COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='加分项类别表';

/*Table structure for table `bonus_comprehensive_score` */

DROP TABLE IF EXISTS `bonus_comprehensive_score`;

CREATE TABLE `bonus_comprehensive_score` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `year_id` int(11) NOT NULL COMMENT '学年id(用于查询优化)',
  `college_id` int(11) NOT NULL COMMENT '学院id(用于查询优化)',
  `grade_id` int(11) NOT NULL COMMENT '年级id(用于查询优化)',
  `profession_id` int(11) NOT NULL COMMENT '专业id(用于查询优化)',
  `class_id` int(11) NOT NULL COMMENT '班级id(用于查询优化)',
  `stu_student_id` int(11) NOT NULL COMMENT '学生id',
  `score` decimal(6,2) NOT NULL DEFAULT '0.00' COMMENT '本学年的最终综合成绩(该成绩是三大类成绩乘以权重后求和计算出来的)',
  `base_score` decimal(6,2) DEFAULT '0.00' COMMENT '基础成绩（60%）',
  `comprehensive_score` decimal(6,2) DEFAULT '0.00' COMMENT '综测加分（20%）',
  `discipline_score` decimal(6,2) DEFAULT '0.00' COMMENT '惩戒分数（20%）',
  `practical_innovation_score` decimal(6,2) DEFAULT '0.00' COMMENT '实践与创新能力（20%）',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT '0' COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1132 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='综合成绩表';

/*Table structure for table `bonus_year` */

DROP TABLE IF EXISTS `bonus_year`;

CREATE TABLE `bonus_year` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(32) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '学年名称(如:2020-2021学年，这个时间一般是指2020年9月至2021年7月，我们它称作为一学年)',
  `created_at` datetime DEFAULT NULL COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT NULL COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='学年表';

/*Table structure for table `oos_images` */

DROP TABLE IF EXISTS `oos_images`;

CREATE TABLE `oos_images` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `md5` varchar(32) COLLATE utf8mb4_bin NOT NULL COMMENT '图片的md5(前端在上传图片之前，先计算一下图片的md5，我这边会提供demo，如果图片md5值与db能匹配上，就可以不用上传图片了，只需要把图片更新一条引用记录到申请材料表中即可)',
  `url` text COLLATE utf8mb4_bin NOT NULL COMMENT '图片url',
  `path` text COLLATE utf8mb4_bin COMMENT '图片保存在服务器上的绝对路径(注意，这是敏感字段，在转换为json时，需要忽略该字段)',
  `created_at` datetime DEFAULT NULL COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT NULL COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='学院表';

/*Table structure for table `stu_class` */

DROP TABLE IF EXISTS `stu_class`;

CREATE TABLE `stu_class` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cl_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '班级名称',
  `created_at` datetime DEFAULT NULL COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT NULL COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='班级表';

/*Table structure for table `stu_college` */

DROP TABLE IF EXISTS `stu_college`;

CREATE TABLE `stu_college` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '学院名称',
  `created_at` datetime DEFAULT NULL COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT NULL COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='学院表';

/*Table structure for table `stu_grade` */

DROP TABLE IF EXISTS `stu_grade`;

CREATE TABLE `stu_grade` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `gd_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '年级',
  `created_at` datetime DEFAULT NULL COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT NULL COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='年级表';

/*Table structure for table `stu_profession` */

DROP TABLE IF EXISTS `stu_profession`;

CREATE TABLE `stu_profession` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `pf_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '专业名称',
  `created_at` datetime DEFAULT NULL COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT NULL COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='专业表';

/*Table structure for table `stu_student` */

DROP TABLE IF EXISTS `stu_student`;

CREATE TABLE `stu_student` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `college_id` int(11) DEFAULT NULL COMMENT '学院id',
  `grade_id` int(11) DEFAULT NULL COMMENT '年级id',
  `profession_id` int(11) DEFAULT NULL COMMENT '专业id',
  `class_id` int(11) DEFAULT NULL COMMENT '班级id',
  `stu_name` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '姓名',
  `stu_number` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '学号',
  `password` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '密码',
  `created_at` datetime DEFAULT NULL COMMENT '创建记录时间',
  `created_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录创建人',
  `updated_at` datetime DEFAULT NULL COMMENT '更新记录时间',
  `updated_by` varchar(64) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '记录修改人',
  `deleted` int(11) DEFAULT NULL COMMENT '逻辑删除,0未删除,1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=378 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='学生表';

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
