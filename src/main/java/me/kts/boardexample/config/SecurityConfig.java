package me.kts.boardexample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/board/**", "/account/detail", "/account/logout", "/account/delete/**").authenticated()
                .anyRequest().permitAll();

        http.formLogin()
                .defaultSuccessUrl("/")
                .loginPage("/account/loginPage")
                .loginProcessingUrl("/account/login")
                .usernameParameter("id")
                .passwordParameter("password");

        http.logout()
                .logoutUrl("/account/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true);
    }
}
