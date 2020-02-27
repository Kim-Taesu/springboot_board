package me.kts.boardexample.config;

import me.kts.boardexample.common.CustomLogoutHandler;
import me.kts.boardexample.common.LoggingFilter;
import me.kts.boardexample.service.AccountService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final CustomLogoutHandler customLogoutHandler;

    public SecurityConfig(AccountService accountService, CustomLogoutHandler customLogoutHandler) {
        this.accountService = accountService;
        this.customLogoutHandler = customLogoutHandler;
    }

    public SecurityExpressionHandler expressionHandler() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        DefaultWebSecurityExpressionHandler handler = new
                DefaultWebSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/board/**", "/account/detail", "/account/logout", "/account/delete/**").hasRole("USER")
                .mvcMatchers("/", "/account/loginPage", "/account/login").permitAll()
                .anyRequest().authenticated()
                .expressionHandler(expressionHandler());

        http.formLogin()
                .defaultSuccessUrl("/")
                .loginPage("/account/loginPage")
                .loginProcessingUrl("/account/login")
                .usernameParameter("id")
                .passwordParameter("password");

        http.logout()
                .logoutUrl("/account/logout")
                .logoutSuccessUrl("/")
                .deleteCookies()
                .addLogoutHandler(customLogoutHandler)
                .invalidateHttpSession(true);

        http.exceptionHandling()
                .accessDeniedHandler((request, response, e) -> {
                    UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String username = principal.getUsername();
                    System.out.println(username + " is denied to access " + request.getRequestURI());
                    response.sendRedirect("/access-denied");
                });

        http.sessionManagement()
                .sessionFixation().changeSessionId()
                .invalidSessionUrl("/account/loginPage")
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1).maxSessionsPreventsLogin(false);

        http.rememberMe()
                .userDetailsService(accountService)
                .key("remember-me-sample");

        http.addFilterBefore(new LoggingFilter(), WebAsyncManagerIntegrationFilter.class);
    }
}
