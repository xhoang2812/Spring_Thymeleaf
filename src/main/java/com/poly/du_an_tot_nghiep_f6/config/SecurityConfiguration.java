package com.poly.du_an_tot_nghiep_f6.config;

import com.poly.du_an_tot_nghiep_f6.service.CustomerSecurityService;
import com.poly.du_an_tot_nghiep_f6.service.EmployeeSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UserDetailsManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private EmployeeSecurityService employeeSecurityService;

    @Autowired
    private CustomerSecurityService customerSecurityService;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/css/**", "/js/**","/uploads/**","/home/**",
                            "/user/**","/login/**","/employee/**").permitAll();
                    registry.requestMatchers("/giaoca", "/xacnhantien").hasRole("EMPLOYEE");
                    registry.requestMatchers("/home/**").hasRole("USER");
                    registry.requestMatchers("/chart/**").hasRole("ADMIN");
                    registry.anyRequest().authenticated();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .successHandler(authenticationSuccessHandler)
                            .permitAll();
                })
                .logout(logoutConfigurer -> {
                    logoutConfigurer
                            .logoutUrl("/logout") // URL để đăng xuất
                            .logoutSuccessUrl("/login") // URL sau khi đăng xuất thành công
                            .invalidateHttpSession(true) // Hủy phiên làm việc
                            .deleteCookies("JSESSIONID") // Xóa cookie phiên
                            .permitAll(); // Cho phép tất cả người dùng truy cập vào /logout
                })
                .build();
        
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Tạo một người dùng cứng với tài khoản "test" và mật khẩu "test" với quyền ADMIN
        UserDetails user = User.withUsername("test")
                .password(passwordEncoder().encode("test")) // Mã hóa mật khẩu
                .roles("ADMIN")
                .build();

        // Sử dụng InMemoryUserDetailsManager để quản lý người dùng trong bộ nhớ
        return new InMemoryUserDetailsManager(user);
    }


    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(username -> {
            try {
                return employeeSecurityService.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                return customerSecurityService.loadUserByUsername(username);
            }
        });
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
