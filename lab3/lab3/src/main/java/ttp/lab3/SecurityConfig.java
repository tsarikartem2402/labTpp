package ttp.lab3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import ttp.lab3.Role;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity

public class SecurityConfig {

    

	@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	
    	http.csrf(CsrfConfigurer::disable)
    	
    	.authorizeHttpRequests(auth -> auth
    			.requestMatchers("/home").permitAll()
    			.requestMatchers("/subjects/delete","/subjects/save","/teachers/delete","/teachers/save","/departments/delete","/departments/save","/students/delete","/students/save").hasRole("ADMIN")
    			.anyRequest().authenticated())
    	.formLogin(login -> login 
    			.loginPage("/login")
    			.defaultSuccessUrl("/home")
    			.permitAll())
    	.logout(logout-> logout
    			.logoutUrl("/logout")
    			.logoutSuccessUrl("/login")
    			.deleteCookies("JSESSIONID"));
		return http.build();
    	
    }
}
