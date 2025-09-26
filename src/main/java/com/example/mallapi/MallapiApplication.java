package com.example.mallapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

//@SpringBootApplication
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)// security로그인 화면 숨기기 어노테이션
public class MallapiApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(MallapiApplication.class, args);
	}

}


/*
 * Main method not found ->gradle buile error 발생시
 * 
 * // Java 버전 설정 (Toolchain을 사용하여 JDK 20으로 설정)
		java {
		변경전
			// toolchain {
			//     languageVersion = JavaLanguageVersion.of(20)
			// }

			// sourceCompatibility와 targetCompatibility는 toolchain에 의해 설정될 수 있지만,
			// 명시적으로 설정하여 호환성을 보장하는 것이 좋습니다.
			변경후
			sourceCompatibility = JavaVersion.VERSION_20
			targetCompatibility = JavaVersion.VERSION_20
		}

*		// 그레이드 삭제 :  >  ./gradlew.bat clean build 
 * 		//  그레이드 재 빌딩 >  ./gradlew build
 * 
 * 
 */