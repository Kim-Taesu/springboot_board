package me.kts.boardexample.config;

import me.kts.boardexample.interceptor.VisitTimeInterceptor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebConfig implements WebMvcConfigurer {


    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new VisitTimeInterceptor());
    }
}
