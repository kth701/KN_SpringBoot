package com.example.mallapi.config;

import com.example.mallapi.mall.dto.MemberDTO;
import com.example.mallapi.mall.exception.Custom403Handler;
import com.example.mallapi.mall.exception.CustomAuthenticationEntryPoint;
import com.example.mallapi.mall.security.CustomUserDetailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/*
@EnableMethodSecurity 어노테이션을 이용한 권한 체크
    prePostEnabled: 메서드 수행하기전에 권한을 체크
    PostAuthorize: 메서드 수행후에 권한을 체크
 */
@Configuration
@Log4j2
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)// 메서드 단위로 접근권한 설정할 경우 사용하는 어노테이션
public class CustomSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
        UserDetailsService인터페이스를 직접 구현시
        CustomUserDetailService 클래스 객체 주입하기 설정 => 스프링 시큐리티에서 사용하는  세선 정보 객체로 등록한다는 의미
     */

    @Autowired
    CustomUserDetailService customUserDetailService;    // 세션 정보 객체로 등록한다는 의미

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("--- security configure : SecurityFilterChain ---");

        /*
        // --------------------------------------------------------------------------------- //
        // 1. JWT처리관련 설정
        // --------------------------------------------------------------------------------- //

        // 1-1. CORS 설정
        http.cors(httpSecurityCorsConfigurer ->
                httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource())
        );


        //1-2.  세션 STATELESS 설정(무세션) => RestFul API 방식일 경우 적용
        http.sessionManagement(sessionConfig ->
                sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 1-3. Security 로그인 폼 설정은 POST방식 적용됨:  username, password파리미터를 통해 로그인처리
        http.formLogin(login -> {
            // JWT 로그인 access token 인증하기
            login.loginPage("/api/member/login");// API서로 로그인 할 수 있게 설정
            login.successHandler(new APILoginSuccessHandler());
            login.failureHandler(new APILoginFailHandler());
        });


        // JWT 체크
        // 일반적으로 로그인 처리 이전에 JWT Check Filter먼저 처리(JWT인증처리할 것인지 여부 확인하는 절차)
        http.addFilterBefore(
                new JWTCheckFilter(), // 첫번째 인자는 두번째 인자 처리 이전에 실행
                UsernamePasswordAuthenticationFilter.class);// 두번째 인자
         */


        // -------------------------------------------------------------------------------------------------- //
        // 2. 로그인 폼 설정, 인증 및 인가 설정 모두 설정하기 // JWT방식이 아닌 요청 경로별 인가 설정
        // -------------------------------------------------------------------------------------------------- //

        // CSRF 비활성화 :   개발테스트용 비활성화: RestAPI에서는 비활성화 안됨
        http.csrf(AbstractHttpConfigurer::disable);
        /* 또는
        http.csrf(c -> c.disable()); // or
        http.httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable);
         */

        /*
        기능구현 테스트시 Security Login 절차 거치지 않고 url로 접속하여 구현테스트 할 경우
            1. @SpringBootApplication(exclude = SecurityAutoConfiguration.class) 형식으로 속성변경
            2. http.formLogin(), http.logout(), http.authorizeHttpRequests() 모두 주석 처리
            3. 메뉴 항목 표시는 인증관련 메서드는 관계로 표시여부는 그래로 두고,
                url에 직접 경로 입력하여 테스트 기능 구현 테스트가능
         */

        // ---------------------------------------------------------------- //
        // 2-1. Security Login Form 설정하기: 기본 url: "/login"
        // Security 로그인 폼 설정은 POST방식 적용됨:  username, password파리미터를 통해 로그인처리
        // ---------------------------------------------------------------- //
        http.formLogin(login -> {
            login.loginPage("/members/login")             // 로그인폼 처리할 URL( Security 기본 로그인으로 설정된 '/login' url 대신 적용됨)
                    .usernameParameter("email")             // 로그인 폼에서 전달 받은 email 매개변수값을   username으로 설정하여 세션 정보(User)객체와 비교
                    .passwordParameter("password")       // 로그인 폼에서 전달 받은 password 매개변수값을  password으로 설정하여 세션 정보(User)객체와 비교
//                    .defaultSuccessUrl("/") // 중복될 경우 defaultSuccessUrl()우선으로 처리됨
//                    .failureUrl("/members/login/error") // failureUrl() 우선으로 처리됨.
                    // 2.1 익명클래스로 작성하기
                    .successHandler(new AuthenticationSuccessHandler() { // 익명 클래스 적용
                        @Override
                        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                            log.info(" --------- successHandler() -----------<");
                            log.info("->1. authentication : "+authentication.toString());
                            log.info("->2. username: "+authentication.getName());
                            log.info("->3. getAuthorities(): "+authentication.getAuthorities());
                            log.info("->4. getPrincipal(): "+authentication.getPrincipal());

                            MemberDTO memberDTO = (MemberDTO)authentication.getPrincipal();
                            log.info("-> MemberDTO memberDTO = (MemberDTO)authentication.getPrincipal()");
                            log.info("->5.  memberDTO.getNickname(): "+memberDTO.getNickname());
                            log.info( "->6.  ((MemberDTO)authentication.getPrincipal()).getNickname(): "+((MemberDTO)authentication.getPrincipal()).getNickname());
                            response.sendRedirect("/");
                        }
                    })
                    .failureHandler(new AuthenticationFailureHandler(){
                        @Override
                        public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                            log.info("-> failureHandler(): " + exception.getMessage());
                            response.sendRedirect("/members/login/error");
                        }
                    });
                    // 2.2 Handler클래스를 별도로 작성하기
                    /*
                    .successHandler(new APILoginSuccessHandler())
                    .failureHandler(new APILoginFailHandler());
                    */
        });


        // ---------------------------------------------------------------- //
        // 2-2. Security Logout  관련 설정
        //    Security 기본 로그아웃 URL => url : "/logout" 로그아웃수행됨
        // ---------------------------------------------------------------- //

        // Security 기본 로그아웃 설정
        //http.logout(Customizer.withDefaults());

        /*  */
        http.logout(logout->{
            logout
                    .logoutUrl("/members/logout") // 기본 로그안 '/logout' 경로를 -> '/members/logout'으로 맵핑
                    //.logoutRequestMatcher(new AntPathRequestMatcher("/logout")) // AntPathRequestMatcher => error
                    .logoutSuccessUrl("/")
                    // 로그아웃시 remember-me 쿠키 삭제, Tomcat이 발급한 세션 유지 쿠키 삭제
                    .deleteCookies("remember-me","JSESSIONID")
                    .invalidateHttpSession(true);// 세션 삭제
        });


        // ---------------------------------------------------------------- //
        // 2-3. 요청 경로별 인가 설정: 리소스 접근에 대한 설정을 하지 않으면
        //  (http.authorizeHttpRequests()메서드 구현하지않을 경우)
        //      ->  로그인 정상 처리후 누구나 접근 가능하도록 설정됨.
        // 로그인 성공시: 인증과정 및 리소스 접근 권한 설정하기
        // ---------------------------------------------------------------- //
        http.authorizeHttpRequests(auth -> {
            //JWT:  로그인 및 토큰 재발급 경로는 모두에게 허용
            auth.requestMatchers("/api/member/login", "/api/member/refresh","/api/member/kakao").permitAll();

            // 1. 정적리소스 접근 권한  부여
            auth.requestMatchers("/axiosJS/**","/css/**","/js/**","/images/**").permitAll();
            // 2. 특정 리소스 접근 권한 부여 : 테스트용 경로 permitAll()에 적용 ,"/admin/**","/cart/**","/orders/**","/board/**"
            auth.requestMatchers("/","/members/**").permitAll();
            // 3. 특정 권한 설정에 따른 리소스 접근
            auth.requestMatchers("/cart/**","/orders/**").hasRole("USER");
            auth.requestMatchers("/admin/**").hasRole("ADMIN");

            // 4. 위에서 설정해준 리소스를 제외한 나머지는 무조건 인증절차를 요구하도록 설정
            auth.anyRequest().authenticated();
            //auth.anyRequest().permitAll(); // 테스트용: 인증 절차 없이 리소스 접근 가능

        });


        // ---------------------------------------------------------------- //
        // 인증되지 않은 사용자가 리소스요청할 경우 예외 처리 하지 않으면
        //  -> '페이지가 작동하지 않습니다.' 에러 페이지 표시됨
        // ---------------------------------------------------------------- //
        http.exceptionHandling(configurer -> {
            //configurer.authenticationEntryPoint(authenticationEntryPoint());// 예처리 방법1 -> 단순 예외처리 메시지전달
            configurer.accessDeniedHandler(accessDeniedHandler());// 예외처리 방법2 -> 예외발생시 로그인 페이지로 이동
            /*
            accessDeniedHandler()메서드 우선을 작동됨.
            configurer.authenticationEntryPoint(authenticationEntryPoint());// 1. 아래 빈으로 등록후 사용
            configurer.authenticationEntryPoint(new CustomAuthenticationEntryPoint());// 2. 직접 객체 빈 객체 생성
             */
        });

        return http.build();
    }





    // --------------------------------------------------------------------------------- //
    //  접근 권한에 맞지 않은 요청시 403에러 핸들러  객체 생성하여 처리 설정: 2가지 타입
    // --------------------------------------------------------------------------------- //
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(){
        return new CustomAuthenticationEntryPoint();
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return new Custom403Handler();
    }
    // --------------------------------------------------------------------------------- //


    // --------------------------------------------------------------------------------- //
    // 3. JWT 및 RestFul API적용시 다른 도메인관련된 설정
    // --------------------------------------------------------------------------------- //
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        //configuration.setAllowedOriginPatterns(List.of("*")); // 모든 도메인 허용 // Arrays.asList("*")
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000")); // 특정 도메인에 한 해서 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        //configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
