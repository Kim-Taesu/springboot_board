package me.kts.boardexample.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class AccountInterceptor implements HandlerInterceptor {

    private final String ACCOUNT = "id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AccountInterceptor interceptor preHandler");
        HttpSession session = request.getSession();
        if (session.getAttribute(ACCOUNT) == null) {
            log.info("\taccount session not exists");
            response.sendRedirect("/account/login");
            return false;
        } else {
            log.info("\taccount session exist");
            return true;
        }
    }
}
