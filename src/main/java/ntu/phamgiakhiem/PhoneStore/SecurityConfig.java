package ntu.phamgiakhiem.PhoneStore;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/register", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
            	    .loginPage("/login")
            	    .permitAll()
            	    .successHandler((request, response, authentication) -> {
            	        var authorities = authentication.getAuthorities();
            	        String targetUrl = "/"; 
            	        
            	        for (var authority : authorities) {
            	            if (authority.getAuthority().equals("ROLE_ADMIN")) {
            	                targetUrl = "/admin/dashboard";
            	                break;
            	            } else if (authority.getAuthority().equals("ROLE_USER")) {
            	                targetUrl = "/";
            	                break;
            	            }
            	        }
            	        response.sendRedirect(request.getContextPath() + targetUrl);
            	    })
            	)
            .logout(logout -> logout
                .permitAll()
                .logoutSuccessUrl("/login?logout")
            );
        return http.build();
    }
}
