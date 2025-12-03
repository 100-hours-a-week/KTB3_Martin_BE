package com.example._th_assignment.Security;



import com.example._th_assignment.Security.Handler.LoginFailureHandler;
import com.example._th_assignment.Security.Handler.LoginSuccessHandler;
import com.example._th_assignment.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {


    private final LoginFailureHandler loginFailureHandler;
    private final LoginSuccessHandler loginSuccessHandler;
    private final LogoutSuccessHandler logoutSuccessHandler;




    @Bean
    public SecurityFilterChain springsecurityFilterChain(HttpSecurity http,
                                                   AuthenticationManager authenticationManager,
                                                   CustomAuthenticationProvider authenticationProvider) throws Exception {


        CustomAuthenticationFilter customAuthenticationFilter =
                new CustomAuthenticationFilter(authenticationManager);

        customAuthenticationFilter.setAuthenticationFailureHandler(loginFailureHandler);
        customAuthenticationFilter.setAuthenticationSuccessHandler(loginSuccessHandler);


        http
                //테스트시에는 off 실제 기동시에는 on ignoringReqeustMatchers
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(basicAuth -> basicAuth.disable())
                .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .securityContext(context ->
                        context.securityContextRepository(new HttpSessionSecurityContextRepository()))

                .authenticationProvider(authenticationProvider)
                .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)




                .logout(logout ->

                        //알아보기
                        logout.logoutRequestMatcher(PathPatternRequestMatcher
                                .withDefaults()
                                .matcher(HttpMethod.DELETE, "/api/user/session"))
                                .logoutSuccessHandler(logoutSuccessHandler)
                                .clearAuthentication(true)
                                .invalidateHttpSession(true))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/session").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/email-conflict").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/nickname-conflict").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user/profile").permitAll()
                        .anyRequest().authenticated()
                );


        return http.build();
    }



    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }


//
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return web -> web.ignoring().requestMatchers("/**");
//    }

}
