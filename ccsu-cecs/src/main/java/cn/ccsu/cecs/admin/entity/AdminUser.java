package cn.ccsu.cecs.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 学院表
 *
 * @author ccsu-cecs
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@TableName("admin_user")
public class AdminUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 账号
     */
    private String username;
    /**
     * 密码(需要用md5加密.注意，这是敏感字段，在转换为json时，需要忽略该字段)
     */
    private String password;
    /**
     * 姓名
     */
    private String name;
    /**
     * 创建记录时间
     */
    private Date createdAt;
    /**
     * 记录创建人
     */
    private String createdBy;
    /**
     * 更新记录时间
     */
    private Date updatedAt;
    /**
     * 记录修改人
     */
    private String updatedBy;
    /**
     * 逻辑删除,0未删除,1已删除
     */
    private Integer deleted;

    public AdminUser(String name, String username) {
        this.name = name;
        this.username = username;
    }
}
