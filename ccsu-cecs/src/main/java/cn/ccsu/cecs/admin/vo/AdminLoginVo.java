package cn.ccsu.cecs.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminLoginVo {
    // 管理员id
    private Integer id;
    // 管理员账号
    private String username;
    // 管理员密码
    private String password;
    // 管理员姓名
    private String name;
}
