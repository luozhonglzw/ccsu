package cn.ccsu.cecs.admin.dao;

import cn.ccsu.cecs.admin.entity.AdminUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学院表
 *
 * @author ccsu-cecs
 */
@Mapper
public interface AdminUserDao extends BaseMapper<AdminUser> {

}
