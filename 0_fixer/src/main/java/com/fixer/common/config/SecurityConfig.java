package com.fixer.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 스프링에서 클래스를 설정용으로 사용하는 어노테이션 , 프로그램 시작 시 안에 있는 @Bean 메서드를 전부 실행 (한 번만!!)
@EnableWebSecurity // 앞으로: 내 설정 → 내가 쓴 규칙 → 로그인 화면 안 뜸
public class SecurityConfig {

	@Bean // 스프링이 만들어서 보관하고 관리하는 객체 @Configuration 랑 짝임 이게 없으면 @Bean은 읽히지 않음.
	public SecurityFilterChain filterChain(HttpSecurity http) {

		// 시큐리티는 이걸 막으려고 모든 POST 요청에 "이 요청은 우리 화면에서 나온 게 맞다"는 증표(토큰)를 요구합니다.
		http.csrf(AbstractHttpConfigurer::disable) // CSRF = Cross-Site Request Forgery, 사이트 간 요청 위조를막음
				.formLogin(AbstractHttpConfigurer::disable) // 로그인 화면을 없애는 줄 / disable = 끄기
				.httpBasic(AbstractHttpConfigurer::disable) // 브라우저 자체 팝업으로 아이디/비번을 묻는 옛날 방식
				.logout(AbstractHttpConfigurer::disable)    // 로그인을 직접 만들 거니까 로그아웃도 직접 만듭니다
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		/*
		 * authorize(허가하다) + Http(웹) + Requests(요청들) → "웹 요청들의 허가 규칙"
		 * auth.anyRequest().permitAll() └ 어떤 요청이든 ┘ └ 전부 허용 ┘
		 * 
		 * csrf 위조 방지 검사 끄기 (JS 요청 막힘 방지) 
		 * formLogin 기본 로그인 화면 끄기 
		 * httpBasic 브라우저 팝업 로그인 끄기
		 * logout 기본 로그아웃 끄기 
		 * authorizeHttpRequests 모든 주소 통과 허용
		 */

		return http.build();
		}
	
	@Bean  // BCryptPasswordEncoder 는 스프링이 아니라 남이 만든 클래스 그래서 @Bean을 사용
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	//  BCrypt의 특징 2가지 : 되돌릴 수 없습니다 (단방향)  
	//                     같은 비밀번호도 매번 다른 값이 나옵니다 암호화할 때마다 소금(salt) 이라는 무작위 값을 섞기 때문
		/*
		 * SecurityFilterChain — 반환 타입 (이 메서드가 만들어내는 것)
		 * 
		 * 우리말로 "보안 필터 사슬". 검문소를 줄줄이 엮어놓은 것이라는 뜻입니다.
		 * 
		 * 브라우저 요청이 TestController 에 곧바로 가지 않습니다. 가는 길에 검문소를 하나씩 통과해요.
		 * 
		 * 브라우저 → [검문①] → [검문②] → [검문③] → Controller └──────── SecurityFilterChain
		 * ────────┘
		 * 
		 * 지금은 이 검문소가 "로그인 안 했으면 통과 금지" 로 설정돼 있어서 Please sign in 이 뜨는 겁니다.
		 * 
		 * 이 메서드가 하는 일 = 검문소를 어떻게 세울지 정해서 만들어 내보내는 것.
		 * 
		 * HttpSecurity http — 파라미터 (규칙을 적는 도구)
		 * 
		 * 검문 규칙을 작성할 수 있게 해주는 도구예요. 중요한 건 우리가 new 로 만들지 않는다는 겁니다.
		 * 
		 * java HttpSecurity http = new HttpSecurity(); // ❌ 이렇게 안 함
		 * 
		 * 파라미터 자리에 이름만 적어두면 스프링이 알아서 만들어서 넣어줍니다. 저번에 말한 "스프링이 대신 해준다" 가 여기서 나옵니다.
		 * 
		 * return http.build(); — 조립해서 내보내기
		 * 
		 * build() = "지금까지 적은 설정으로 완성품을 만들어라"
		 * 
		 */
	

}
