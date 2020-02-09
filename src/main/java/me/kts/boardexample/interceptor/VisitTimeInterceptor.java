package me.kts.boardexample.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Slf4j
public class VisitTimeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("visitTime interceptor preHandler");
        HttpSession session = request.getSession();
        String ACCOUNT = "account";
        String VISIT_TIME = "visitTime";
        String USE_TIME = "useTime";
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분 s초");
        // account session 존재하면
        if (session.getAttribute(ACCOUNT) != null) {
            log.info("account exists");
            // visit time 존재 x
            if (session.getAttribute(VISIT_TIME) == null) {
                LocalDateTime now = LocalDateTime.now();
                session.setAttribute(VISIT_TIME, now.format(dateTimeFormatter));
                session.setAttribute(USE_TIME, ChronoUnit.MINUTES.between(now, LocalDateTime.now())+"분");
            } else {
                LocalDateTime visitTime = LocalDateTime.parse((CharSequence) session.getAttribute(VISIT_TIME), dateTimeFormatter);
                session.setAttribute(USE_TIME, ChronoUnit.MINUTES.between(visitTime, LocalDateTime.now())+"분");
            }
        }
        // account session 존재 x
        else {
            log.info("account not exists");
            if (session.getAttribute(VISIT_TIME) != null) {
                session.removeAttribute(VISIT_TIME);
            }
        }
        return true;
    }
}
