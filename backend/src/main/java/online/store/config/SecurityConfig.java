package online.store.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import online.store.common.utils.JwtUtil;
import online.store.pojo.Result;
import online.store.common.filter.JwtAuthenticationFilter;
import online.store.service.common.TokenBlacklistService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 安全配置
 * <p>接口JWT认证</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * JWT 工具类对象
     */
    private final JwtUtil jwtUtil;

    /**
     * Token 黑名单服务
     */
    private final TokenBlacklistService tokenBlacklistService;

    private final ObjectMapper objectMapper;

    /**
     * 密码编码器 Bean
     * 使用 BCrypt 算法进行密码加密
     * @return PasswordEncoder 实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器 Bean
     * 用于处理用户认证, 便于其他地方验证
     * @param configuration AuthenticationConfiguration 实例
     * @return AuthenticationManager 实例
     * @throws Exception 可能抛出异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 安全过滤链 Bean
     * 配置 HTTP 安全性, 包括 JWT 过滤器和权限设置
     * @param http HttpSecurity 实例
     * @return SecurityFilterChain 实例
     * @throws Exception 可能抛出异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtUtil, tokenBlacklistService);

        http
                // 禁用 CSRF, 因为JWT无状态
                .csrf(AbstractHttpConfigurer::disable)

                // 启用 CORS
                .cors(cors -> {})

                // 2. 配置 Session 管理策略为 STATELESS，不创建 Session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. 配置路由权限
                .authorizeHttpRequests(auth -> auth
                        // 放行 Swagger UI 和 API 文档 及 Knife4j, 生产环境可关闭
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/doc.html",
                                "/webjars/**"
                        ).permitAll()
                        // 放行认证登录注册接口
                        .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                        // 放行公共商品查询接口
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        // 放行所有 OPTIONS 请求，支持跨域预检请求
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )

                // 4. 将 JWT 认证过滤器添加到 Spring Security 过滤器链中
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 5. 配置异常处理
                .exceptionHandling(exceptions -> exceptions
                        // 处理认证失败（如 Token 无效）
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(objectMapper.writeValueAsString(Result.error("用户未认证或Token无效")));
                        })
                        // 处理授权失败（如权限不足）
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write(objectMapper.writeValueAsString(Result.error("用户权限不足")));
                        })
                );

        return http.build();
    }
}
