package me.kts.boardexample.service;

import lombok.extern.slf4j.Slf4j;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.domain.Idiot;
import me.kts.boardexample.domain.IdiotDto;
import me.kts.boardexample.domain.UserAccount;
import me.kts.boardexample.repository.AccountRepository;
import me.kts.boardexample.repository.IdiotRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final IdiotRepository idiotRepository;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder, IdiotRepository idiotRepository) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.idiotRepository = idiotRepository;
    }

    public boolean signUpCheck(Account account) {
        return accountRepository.findById(account.getId()).orElse(null) == null;
    }

    public Account getInfo() {
        String id = getUsername();
        return accountRepository.findById(id).orElse(null);
    }

    private String getUsername() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }

    public boolean updateInfo(Account account) {
        String username = getUsername();
        if (!username.equals(account.getId())) {
            return false;
        }

        Account byId = accountRepository.findById(account.getId()).orElse(null);
        if (byId != null) {
            byId.setPassword(account.getPassword());
            byId.encodePassword(passwordEncoder);
            byId.setName(account.getName());
            byId.setAge(account.getAge());
            byId.setEMail(account.getEMail());
            byId.setPersisted(true);
            accountRepository.save(byId);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteUser(String accountId) {
        String username = getUsername();
        if (username.equals(accountId)) {
            try {
                accountRepository.deleteById(accountId);
            } catch (Exception e) {
                return false;
            }
            return true;
        } else {
            return false;
        }

    }

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) {
            throw new UsernameNotFoundException(id);
        }
        assert account.getId() != null;
        return new UserAccount(account);
    }


    public boolean signUp(Account account) {
        try {
            account.setRole("USER");
            account.encodePassword(passwordEncoder);
            accountRepository.save(account);
        } catch (Exception e) {
            log.error(e.toString());
            return false;
        }
        return true;
    }


    public boolean addIdiot(String idiotId, String boardId, String commentId, IdiotDto idiotDto) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (commentId.equals("none")) {
            Idiot isExist = idiotRepository.findByUserIdAndBoardIdAndIdiotId(principal.getUsername(), boardId, idiotId).orElse(null);
            if (isExist != null)
                return false;
        } else {
            Idiot isExist = idiotRepository.findByUserIdAndCommentIdAndIdiotId(principal.getUsername(), commentId, idiotId).orElse(null);
            if (isExist != null)
                return false;
        }

        Idiot idiot = Idiot.builder()
                .idiotId(idiotId)
                .boardId(boardId)
                .title(idiotDto.getTitle())
                .content(idiotDto.getContent())
                .build();

        if (!commentId.equals("none")) {
            idiot.setCommentId(commentId);
        }

        idiot.makeId(principal.getUsername());
        idiotRepository.save(idiot);

        Account account = accountRepository.findById(idiotId).orElse(null);
        if (account == null) {
            return false;
        }
        account.setIdiotCount(account.getIdiotCount() + 1);
        account.setPersisted(true);
        accountRepository.save(account);
        return true;
    }
}
