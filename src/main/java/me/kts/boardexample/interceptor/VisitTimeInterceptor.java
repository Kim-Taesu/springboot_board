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


        HttpSession session = request.getSession();
        String VISIT_TIME = "visitTime";
        String USE_TIME = "useTime";

        // 인증됨
        if (request.getUserPrincipal() != null) {
            log.info(request.getRemoteAddr() + "|" + request.getUserPrincipal().getName() + "|" + request.getRequestURI());
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분 s초");

            // 첫번 째 접근 시 방문 시각 저장
            if (session.getAttribute(VISIT_TIME) == null) {
                LocalDateTime now = LocalDateTime.now();
                session.setAttribute(VISIT_TIME, now.format(dateTimeFormatter));
            }

            // 이용 시간 저장
            LocalDateTime visitTime = LocalDateTime.parse((CharSequence) session.getAttribute(VISIT_TIME), dateTimeFormatter);
            session.setAttribute(USE_TIME, ChronoUnit.MINUTES.between(visitTime, LocalDateTime.now()) + "분");
        } else {
            log.info(request.getRemoteAddr() + "|" + "Anonymous|" + request.getRequestURI());
        }
        return true;
    }
}
