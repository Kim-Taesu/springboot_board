package me.kts.boardexample.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.util.StopWatch;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Order
public class LoggingFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(((HttpServletRequest) request).getRequestURI());
        filterChain.doFilter(request, servletResponse);
        stopWatch.stop();
        log.info(stopWatch.getTotalTimeMillis() + "ms");
    }
}
