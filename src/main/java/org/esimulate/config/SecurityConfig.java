package org.esimulate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        /*
          http
                        // 关闭 CSRF 保护（针对 API）
                        .csrf().disable().authorizeRequests()
                        // 允许公开访问
                        .antMatchers("/api/login", "/api/register","/api/thermal-load-schemes/test").permitAll()
                        // 其他请求需要认证
                        .anyRequest().authenticated().and()
                        // 禁用 Session 认证（如果只用 JWT）
                        .sessionManagement().disable()
                        // ❌ 禁用默认的 Spring Security 登陆页面
                        .formLogin().disable();
         */
        http
                .csrf().disable()  // 关闭 CSRF 保护（针对 API）
                .authorizeRequests()
                .anyRequest().permitAll()  // ✅ 允许所有接口访问
                .and()
                .sessionManagement().disable()  // 禁用 Session 认证
                .httpBasic().disable()  // 禁用 HTTP Basic 认证
                .formLogin().disable();  // 禁用默认的 Spring Security 登陆页面

        return http.build();
    }

}