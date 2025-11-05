package com.maitrunghau.hotelbookingsystem.config.security;

import com.maitrunghau.hotelbookingsystem.common.*;
import com.maitrunghau.hotelbookingsystem.model.Role;
import com.maitrunghau.hotelbookingsystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final SetupRedirectFilter setupRedirectFilter;

    private final EmployeeRepository employeeRepository;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/setup/**", "/auth/**", "/api/**")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/setup/**", "/auth/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("Admin")
                        .requestMatchers("/customer/**").hasRole("Customer")
                        .requestMatchers("/service/**").hasAnyRole("Admin", "Service_Manager")
                        .requestMatchers("/reception/**").hasAnyRole("Admin", "Receptionist")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(customAuthenticationSuccessHandler)
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(request ->
                                "GET".equalsIgnoreCase(request.getMethod()) &&
                                        "/auth/logout".equals(request.getRequestURI())
                        )
                        .logoutSuccessHandler((request, response, authentication) -> {
                            request.getSession().setAttribute("SUCCESS_MESSAGE", "Đăng xuất thành công!");
                            response.sendRedirect("/auth/login?logout");
                        })
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )


                .exceptionHandling(ex -> {
                    ex.accessDeniedHandler(customAccessDeniedHandler);

                    long adminCount = employeeRepository.countByRole(Role.Admin);
                    if (adminCount == 0) {
                        System.out.println("⚠️ [SECURITY] Entry point disabled → redirect /setup/create-admin");
                        ex.authenticationEntryPoint((req, res, e) -> res.sendRedirect("/setup/create-admin"));
                    } else {
                        ex.authenticationEntryPoint(customAuthenticationEntryPoint);
                    }
                })

                .sessionManagement(session -> session
                        .invalidSessionUrl("/auth/login")
                        .maximumSessions(1)
                        .expiredUrl("/auth/login")
                );

        // 🧩 Đăng ký filter thủ công để chạy trước security filter chain
        http.addFilterBefore(setupRedirectFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ⚙️ Đảm bảo filter hoạt động ngay khi server boot
    @Bean
    public FilterRegistrationBean<SetupRedirectFilter> registerSetupFilter(SetupRedirectFilter filter) {
        FilterRegistrationBean<SetupRedirectFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }
}
