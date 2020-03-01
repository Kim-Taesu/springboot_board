package me.kts.boardexample.config;

import me.kts.boardexample.common.CustomAccessDeniedHandler;
import me.kts.boardexample.common.CustomLogoutHandler;
import me.kts.boardexample.common.CustomOauthHandler;
import me.kts.boardexample.common.LoggingFilter;
import me.kts.boardexample.service.AccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.InMemoryOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AccountService accountService;
    private final CustomLogoutHandler customLogoutHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final Environment env;
    private final CustomOauthHandler customOauthHandler;

    public SecurityConfig(AccountService accountService, CustomLogoutHandler customLogoutHandler, CustomAccessDeniedHandler customAccessDeniedHandler, Environment env, CustomOauthHandler customOauthHandler) {
        this.accountService = accountService;
        this.customLogoutHandler = customLogoutHandler;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.env = env;
        this.customOauthHandler = customOauthHandler;
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
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .mvcMatchers("/board/**", "/account/detail", "/account/logout", "/account/delete/**").hasRole("USER")
                .mvcMatchers("/account/list", "/idiot/list", "/idiot/detail/**").hasRole("ADMIN")
                .mvcMatchers("/", "/account/loginPage", "/account/login", "/account/signUp").permitAll()
                .anyRequest().authenticated()
                .expressionHandler(expressionHandler());

        http.formLogin()
                .defaultSuccessUrl("/")
                .loginPage("/account/loginPage")
                .loginProcessingUrl("/account/login")
                .failureUrl("/account/loginPage")
                .usernameParameter("id")
                .passwordParameter("password");

        http.oauth2Login()
                .authorizationEndpoint(authorizationEndpointConfig ->
                        authorizationEndpointConfig.baseUri("/account/oauth2/authorization"))
                .defaultSuccessUrl("/")
                .clientRegistrationRepository(clientRegistrationRepository())
                .successHandler(customOauthHandler);

        http.logout()
                .logoutUrl("/account/logout")
                .logoutSuccessUrl("/")
                .deleteCookies()
                .addLogoutHandler(customLogoutHandler)
                .invalidateHttpSession(true);

        http.exceptionHandling()
                .accessDeniedHandler(customAccessDeniedHandler);

        http.sessionManagement()
                .sessionFixation().changeSessionId()
                .invalidSessionUrl("/account/loginPage")
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false);

        http.rememberMe()
                .userDetailsService(accountService)
                .key("remember-me-sample");

        http.addFilterBefore(new LoggingFilter(), WebAsyncManagerIntegrationFilter.class);
    }


    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<String> clients = Arrays.asList("google");
        List<ClientRegistration> registrations = clients.stream()
                .map(this::getRegistration)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return new InMemoryClientRegistrationRepository(registrations);
    }


    private ClientRegistration getRegistration(String client) {
        String CLIENT_PROPERTY_KEY = "spring.security.oauth2.client.registration.";
        String clientId = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-id");
        if (clientId == null) {
            return null;
        }
        String clientSecret = env.getProperty(CLIENT_PROPERTY_KEY + client + ".client-secret");
        if (clientSecret == null) {
            return null;
        }

        if (client.equals("google")) {
            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(clientId)
                    .scope("openid", "profile", "email", "phone")
                    .clientSecret(clientSecret)
                    .build();
        }
        if (client.equals("facebook")) {
            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .build();
        }
        return null;
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new InMemoryOAuth2AuthorizedClientService(
                clientRegistrationRepository());
    }
}
