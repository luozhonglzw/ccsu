package cn.ccsu.cecs.common.interceptor;

import cn.ccsu.cecs.common.constant.TokenType;
import cn.ccsu.cecs.common.utils.JWTUtils;
import cn.ccsu.cecs.config.IpWhiteProperties;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class TeacherInterceptor implements HandlerInterceptor {

    public static ThreadLocal<TokenType> threadLocal = ThreadLocal.withInitial(() -> null);

    // 老师姓名的threadLocal
    public static ThreadLocal<String> teacherInfoThreadLocal = ThreadLocal.withInitial(() -> "");

    @Autowired
    IpWhiteProperties ipWhiteProperties;

    /**
     * 校验老师是否登录（jwt校验）
     *
     * @param request  请求
     * @param response 响应
     * @param handler  handler
     * @return true or false
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String requestURI = request.getRequestURI();
        boolean match = antPathMatcher.match("/teacher/bonus-comprehensive-score/export-info", requestURI);
        if (match) {
            return true;
        }

        // ip白名单过滤
        filterIp(request);

        // 获取请求头中的令牌
        String token = request.getHeader(TokenType.TEACHER_TOKEN.getType());

        try {
            // 验证toke令牌
            JWTUtils.verify(token, TokenType.TEACHER_TOKEN);

            // 设置threadLocal
            threadLocal.set(TokenType.TEACHER_TOKEN);
            // 设置老师姓名的threadLocal
            String name = JWTUtils.getPayload(request, "name");
            String username = JWTUtils.getPayload(request, "username");

            teacherInfoThreadLocal.set("{name:" + name + ", username:" + username + "}");
            return true;
        } catch (SignatureVerificationException e) {
            throw new RuntimeException("签名不一致");
        } catch (TokenExpiredException e) {
            throw new RuntimeException("令牌过期");
        } catch (AlgorithmMismatchException e) {
            throw new RuntimeException("算法不匹配");
        } catch (InvalidClaimException e) {
            throw new RuntimeException("失效的payload");
        } catch (Exception e) {
            throw new RuntimeException("token无效");
        }
    }


    public void filterIp(HttpServletRequest request) {
        String[] ipList = ipWhiteProperties.getIpList();
        String hostAddress = getIpAddress(request);
//改为错
        boolean flag = true;
        for (String s : ipList) {
            if (s.contains("all") || s.contains(hostAddress)) {
                flag = true;
                break;
            }
        }
        if (flag) {
            log.info("老师端登录认证通过 ip:" + hostAddress);
            return;
        }
        log.error("未授权机器 ip:" + hostAddress);
        throw new RuntimeException("未为授权机器，无法访问后台应用");
    }

    public String getIpAddress(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            if (ip.contains(",")) {
                ip = ip.split(",")[0];
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            System.out.println("Proxy-Client-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            System.out.println("WL-Proxy-Client-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            System.out.println("HTTP_CLIENT_IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            System.out.println("HTTP_X_FORWARDED_FOR ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
            System.out.println("X-Real-IP ip: " + ip);
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            System.out.println("getRemoteAddr ip: " + ip);
        }
        return ip;
    }

    /**
     * * 判断一个字符串是否为空串
     *
     * @param str String
     * @return true：为空 false：非空
     */
    public static boolean isEmpty(String str) {
        return isNull(str) || "".equals(str.trim());
    }

    /**
     * * 判断一个字符串是否为非空串
     *
     * @param str String
     * @return true：非空串 false：空串
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object) {
        return object == null;
    }

}
