package me.kts.boardexample.handler;

import me.kts.boardexample.domain.Account;
import me.kts.boardexample.domain.AccountVisit;
import me.kts.boardexample.repository.AccountRepository;
import me.kts.boardexample.repository.AccountVisitRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final AccountVisitRepository accountVisitRepository;
    private final AccountRepository accountRepository;

    public CustomLogoutHandler(AccountVisitRepository accountVisitRepository, AccountRepository accountRepository) {
        this.accountVisitRepository = accountVisitRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        String userId = principal.getUsername();
        LocalDateTime now = LocalDateTime.now();
        String VISIT_TIME = "visitTime";
        String USE_TIME = "useTime";
        HttpSession session = request.getSession();
        String loginTime = (String) session.getAttribute(VISIT_TIME);
        String useTime = (String) session.getAttribute(USE_TIME);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일 a h시 m분 s초");
        String logoutTime = now.format(dateTimeFormatter);
        accountVisitRepository.save(new AccountVisit(userId, loginTime, useTime, logoutTime));

        Account account = accountRepository.findById(userId).get();
        account.setRecentAccess(logoutTime);
        account.setPersisted(true);
        accountRepository.save(account);

        session.removeAttribute(VISIT_TIME);
        session.removeAttribute(USE_TIME);
    }
}
