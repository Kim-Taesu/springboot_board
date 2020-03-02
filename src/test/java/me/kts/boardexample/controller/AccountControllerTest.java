package me.kts.boardexample.controller;

import me.kts.boardexample.common.BaseControllerTest;
import me.kts.boardexample.common.CustomMockUser;
import me.kts.boardexample.common.TestDescription;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.repository.AccountRepository;
import me.kts.boardexample.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountControllerTest extends BaseControllerTest {

    private final String id = "id";
    @Autowired
    AccountService service;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AccountRepository accountRepository;

    @Before
    public void initDatabase() {
        accountRepository.deleteAll();
    }

    @Test
    @CustomMockUser
    @TestDescription("로그아웃")
    public void logout_success() throws Exception {
        // Given
        Account account = buildAccount();
        accountRepository.save(account);

        // When
        ResultActions actions = mockMvc.perform(post("/account/logout")
                .with(csrf())
        )
                .andDo(print());

        // Then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }


    @Test
    @TestDescription("로그인 화면")
    public void loginPage() throws Exception {
        // When
        ResultActions actions = mockMvc
                .perform(get("/account/loginPage"))
                .andDo(print());
        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"));
    }

    @Test
    @TestDescription("회원가입 화면")
    public void signUpPage() throws Exception {
        // When
        ResultActions actions = mockMvc
                .perform(get("/account/signUp"))
                .andDo(print());
        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("account/signUp"));
    }

    @Test
    public void login_fail_by_id() throws Exception {

        // Given
        Account account = buildAccount();
        accountRepository.save(account);

        // When
        ResultActions actions = mockMvc.perform(post("/account/login")
                .param("id", account.getId() + "dump")
                .param("password", account.getPassword()))
                .andDo(print());

        // Then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/loginPage"));
    }

    @Test
    public void login_fail_by_password() throws Exception {

        // Given
        Account account = buildAccount();
        accountRepository.save(account);

        // When
        ResultActions actions = mockMvc.perform(post("/account/login")
                .param("id", account.getId())
                .param("password", account.getPassword() + "dump"))
                .andDo(print());

        // Then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/loginPage"));
    }

    @Test
    public void login_success() throws Exception {

        // Given
        Account account = buildAccount();
        String password = "password";
        account.setPassword(password);
        account.encodePassword(passwordEncoder);
        accountRepository.save(account);

        // When
        ResultActions actions = mockMvc.perform(formLogin()
                .loginProcessingUrl("/account/login")
                .userParameter("id")
                .user(account.getId())
                .password(password)
        )
                .andDo(print());

        // Then
        actions
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    private Account buildAccount() {

        String password = "password";
        Account account = Account.builder()
                .id(id)
                .password(password)
                .age(1)
                .name(id)
                .eMail("test@test.com")
                .build();
        account.encodePassword(passwordEncoder);
        return account;
    }

    @Test
    public void signUp_fail_invalid_data() throws Exception {
        // When
        ResultActions actions = mockMvc.perform(post("/account/signUp")
                .param("id", "id")
                .param("password", "password")
                .with(csrf()))
                .andDo(print());

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("/account/signUp"));
    }

    @Test
    public void signUp_fail_id_duplicate() throws Exception {
        Account account = buildAccount();
        accountRepository.save(account);

        // When
        ResultActions actions = mockMvc.perform(post("/account/signUp")
                .param("id", account.getId())
                .param("password", account.getPassword())
                .param("name", account.getName())
                .param("age", account.getAge().toString())
                .param("eMail", account.getEMail())
                .with(csrf())
        )
                .andDo(print());

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("/account/signUp"));
    }

    @Test
    public void signUp_success() throws Exception {
        Account account = buildAccount();

        // When
        ResultActions actions = mockMvc.perform(post("/account/signUp")
                .param("id", account.getId())
                .param("password", account.getPassword())
                .param("name", account.getName())
                .param("age", account.getAge().toString())
                .param("eMail", account.getEMail())
                .with(csrf()))
                .andDo(print());

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"));
    }
}