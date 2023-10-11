package cn.ccsu.cecs.common.interceptor;

import cn.ccsu.cecs.common.constant.TokenType;
import cn.ccsu.cecs.common.utils.JWTUtils;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器
 */
@Slf4j
public class BaseInterceptor implements HandlerInterceptor {
    private final String attribute;

    public static ThreadLocal<TokenType> threadLocal = ThreadLocal.withInitial(() -> null);

    public static ThreadLocal<String> stuNumberThreadLocal = ThreadLocal.withInitial(() -> "");

    public BaseInterceptor(String attribute) {
        this.attribute = attribute;
    }

    /**
     * 校验用户是否登录（jwt认证）
     *
     * @param request  请求
     * @param response 响应
     * @param handler  handler
     * @return true or false
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 解决CORS
        response.setHeader("Access-Control-Allow-Origin", "*");

        // 获取请求头中的令牌
        String token = request.getHeader(TokenType.STUDENT_TOKEN.getType());

        try {
            // 验证toke令牌
            JWTUtils.verify(token, TokenType.STUDENT_TOKEN);

            // 设置threadLocal
            threadLocal.set(TokenType.STUDENT_TOKEN);

            // 设置学生学号的threadLocal
            String stuNumber = JWTUtils.getPayload(request, "stuNumber");
            stuNumberThreadLocal.set(stuNumber);
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

}
