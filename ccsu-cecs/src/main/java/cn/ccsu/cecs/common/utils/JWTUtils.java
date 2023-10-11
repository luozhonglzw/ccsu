package cn.ccsu.cecs.common.utils;

import cn.ccsu.cecs.common.constant.TokenType;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Map;

public class JWTUtils {
    // 加密的签名 - 学生端
    private static String STUDENT_SECRET = "studentstudent^token!Q@W#E$R$E#W@Q!token^ccsu-cecs^studentstudent";
    // 加密的签名 - 老师端
    private static String TEACHER_SECRET = "teachertoken^&token!Q@W#E$R$E#W@Q!token^ccsu-cecs^tokenteacher";

    /**
     * 生产token
     */
    public static String getToken(Map<String, String> map, TokenType tokenType) {
        JWTCreator.Builder builder = JWT.create();

        //payload
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });

        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.DATE, 1); // 默认1天过期

        builder.withExpiresAt(instance.getTime());  // 指定令牌的过期时间

        String token;
        // 老师和学生有不同的签名方式
        if (tokenType == TokenType.STUDENT_TOKEN) {
            token = builder.sign(Algorithm.HMAC256(STUDENT_SECRET));  // 签名
        } else {
            token = builder.sign(Algorithm.HMAC256(TEACHER_SECRET));  // 签名
        }
        return token;
    }


    /**
     * 验证token
     */
    public static void verify(String token, TokenType tokenType) {
        if (tokenType == TokenType.STUDENT_TOKEN) {
            //如果有任何验证异常，此处都会抛出异常
            JWT.require(Algorithm.HMAC256(STUDENT_SECRET)).build().verify(token);
        } else {
            JWT.require(Algorithm.HMAC256(TEACHER_SECRET)).build().verify(token);
        }
    }

    /**
     * 获取token中的 payload
     */
    public static String getPayload(HttpServletRequest request, String field) {
        String tokenType;
        DecodedJWT decodedJWT;

        // 获取request的请求路径
        String requestURI = request.getRequestURI();
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        // 匹配请求路径
        boolean match = antPathMatcher.match("/student/**", requestURI);

        if (match) {
            tokenType = TokenType.STUDENT_TOKEN.getType();
            decodedJWT = JWT.require(Algorithm.HMAC256(STUDENT_SECRET)).build().verify(request.getHeader(tokenType));
        } else {
            tokenType = TokenType.TEACHER_TOKEN.getType();
            decodedJWT = JWT.require(Algorithm.HMAC256(TEACHER_SECRET)).build().verify(request.getHeader(tokenType));
        }

        return decodedJWT.getClaim(field).asString();
    }
}
