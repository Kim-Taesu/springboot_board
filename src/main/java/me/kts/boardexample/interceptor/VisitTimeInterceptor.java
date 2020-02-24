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

        String ACCOUNT = "id";
        if (request.getUserPrincipal() != null) {
            request.getSession().setAttribute(ACCOUNT, request.getUserPrincipal().getName());
        }


        log.info("visitTime interceptor preHandler");
        // session 정보 가져오기
        HttpSession session = request.getSession();
        // 로그인한 상태라면
        String VISIT_TIME = "visitTime";
        if (session.getAttribute(ACCOUNT) != null) {
            log.info("\taccount exists");
            // date format
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분 s초");
            // 방문 시간이 존재하면
            String USE_TIME = "useTime";
            if (session.getAttribute(VISIT_TIME) == null) {
                log.info("\tvisit time not exists");
                // 현재 방문시간 계산
                LocalDateTime now = LocalDateTime.now();
                // 방문 시간을 시간 포맷에 맞게 변경 후 저장
                session.setAttribute(VISIT_TIME, now.format(dateTimeFormatter));
                // 이용 시간 저장
                session.setAttribute(USE_TIME, ChronoUnit.MINUTES.between(now, LocalDateTime.now()) + "분");
            } else {
                log.info("\tvisit time exists");
                // 방문했던 시간 LocalDateTime 형식으로 변환
                LocalDateTime visitTime = LocalDateTime.parse((CharSequence) session.getAttribute(VISIT_TIME), dateTimeFormatter);
                log.info("\tupdate useTime");
                // 이용 시간 저장
                session.setAttribute(USE_TIME, ChronoUnit.MINUTES.between(visitTime, LocalDateTime.now()) + "분");
            }
        }
        // 로그인한 상태가 아니라면
        else {
            log.info("\taccount not exists");
            // 로그인하지 않았는데 방문시간이 존재하면 삭제
            if (session.getAttribute(VISIT_TIME) != null) {
                session.removeAttribute(VISIT_TIME);
            }
        }
        return true;
    }
}
