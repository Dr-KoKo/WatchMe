package com.A108.Watchme.Config;

import com.A108.Watchme.Exception.AuthenticationEntryPointHandler;
import com.A108.Watchme.Exception.WebAccessDeniedHandler;
import com.A108.Watchme.auth.CustomOAuth2UserService;
import com.A108.Watchme.jwt.JwtAuthenticationFilter;
import com.A108.Watchme.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class WebSecurityConfigure {
    private final CorsFilter corsFilter;
    private final JwtProvider jwtProvider;
    private final AuthenticationEntryPointHandler authenticationEntryPointHandler;
    private final WebAccessDeniedHandler webAccessDeniedHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilter(corsFilter) // @CrossOrigin(인증 X), 시큐리티 필터에 등록해줘야함.
                .authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/member/**").hasRole("MEMBER")
                .anyRequest().permitAll()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPointHandler)
                .accessDeniedHandler(webAccessDeniedHandler)
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);


        return http.build();
    }
}
