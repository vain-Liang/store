package online.store.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import online.store.common.utils.JwtUtil;
import online.store.pojo.Result;
import online.store.service.common.TokenBlacklistService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT 认证过滤器, 每个请求前执行
 * <p>从每次请求头中解析 JWT</p>
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private final TokenBlacklistService tokenBlacklistService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String token;

        // 如果请求头为空或不是以 "Bearer " 开头, 则直接放行
        if (!StringUtils.hasText(header) || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        token = header.substring(7); // 去掉 "Bearer " 前缀得到纯 token, "Bearer".length() == 7

        // 检查 Token 是否在黑名单中
        if (tokenBlacklistService.isBlacklisted(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Result.error("Token已失效，请重新登录")));
            return;
        }

        try {

            Claims claims = jwtUtil.parseToken(token);
            final String username = claims.getSubject();

            // 如果用户名不为空, 且当前 SecurityContext 中没有认证信息
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // 将 role 转为 Spring Security 的 GrantedAuthority
                String role = claims.get("role", String.class);
                List<SimpleGrantedAuthority> authorities = role == null ? List.<SimpleGrantedAuthority>of() : List.of(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())); // 角色名称转换为大写
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证信息设置到 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            // token 无效或过期：直接不设置 authentication（也可以返回 401）
            // 这里选择继续过滤器链，让 Spring Security 拦截并返回 401

            // 可以在日志中记录异常信息以便调试
            logger.warn("JWT Token validation error: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
