package me.kts.boardexample.controller;

import me.kts.boardexample.common.BaseControllerTest;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.repository.AccountRepository;
import me.kts.boardexample.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.ResultActions;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AccountControllerTest extends BaseControllerTest {

    @Autowired
    AccountService service;

    @Autowired
    AccountRepository repository;

    @Before
    public void initDatabase() {
        repository.deleteAll();
    }

    @Test
    public void logout() throws Exception {
        Account account = buildAccount("test", "test", "test", 26, "akvks456@gmail.com");
        repository.save(account);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", account.getId());
        assertThat(session.getAttribute("id")).isNotNull();

        ResultActions actions = mockMvc
                .perform(get("/account/logout")
                        .session(session))
                .andDo(print());

        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    public void loginPage() throws Exception {
        // when
        ResultActions actions = mockMvc
                .perform(get("/account/loginPage"))
                .andDo(print());
        //then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"));
    }

    @Test
    public void signUpPage() throws Exception {
        // when
        ResultActions actions = mockMvc
                .perform(get("/account/signUp"))
                .andDo(print());
        //then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("account/signUp"));
    }

    @Test
    public void login_fail_by_id() throws Exception {

        // given
        String id = "id";
        String password = "password";

        Account account = buildAccount(id, password);
        repository.save(account);

        // when
        ResultActions actions = mockMvc.perform(post("/account/login")
                .param("id", id + "dump")
                .param("password", password))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/login"));
    }

    @Test
    public void login_fail_by_password() throws Exception {

        // given
        String id = "id";
        String password = "password";

        Account account = buildAccount(id, password);
        repository.save(account);

        // when
        ResultActions actions = mockMvc.perform(post("/account/login")
                .param("id", id)
                .param("password", password + "dump"))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/login"));
    }

    @Test
    public void login_success() throws Exception {

        // given
        String id = "id";
        String password = "password";

        Account account = buildAccount(id, password);
        repository.save(account);

        // when
        ResultActions actions = mockMvc.perform(post("/account/login")
                .param("id", id)
                .param("password", password))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    private Account buildAccount(String id, String password) {
        return Account.builder()
                .id(id)
                .password(password)
                .build();
    }

    private Account buildAccount(String id, String password, String name, Integer age, String eMail) {
        return Account.builder()
                .id(id)
                .password(password)
                .age(age)
                .eMail(eMail)
                .name(name)
                .build();
    }

    @Test
    public void signUp_fail_invalid_data() throws Exception {
        Account account = buildAccount("kts_test", "kts_test");
        // when
        ResultActions actions = mockMvc.perform(post("/account/signUp")
                .param("id", account.getId())
                .param("password", account.getPassword()))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/signUp"));
    }

    @Test
    public void signUp_fail_id_duplicate() throws Exception {
        Account account = buildAccount("kts_test", "kts_test", "kts", 26, "akvks456@gmail.com");
        repository.save(account);

        // when
        ResultActions actions = mockMvc.perform(post("/account/signUp")
                .param("id", account.getId())
                .param("password", account.getPassword())
                .param("name", account.getName())
                .param("age", account.getAge().toString()))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/signUp"));
    }

    @Test
    public void signUp_success() throws Exception {
        Account account = buildAccount("signup_test3", "signup_test2", "kts", 26, "akvks456@gmail.com");

        // when
        ResultActions actions = mockMvc.perform(post("/account/signUp")
                .param("id", account.getId())
                .param("password", account.getPassword())
                .param("name", account.getName())
                .param("age", account.getAge().toString())
                .param("eMail", account.getEMail()))
                .andDo(print());

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("account/login"));
    }
}