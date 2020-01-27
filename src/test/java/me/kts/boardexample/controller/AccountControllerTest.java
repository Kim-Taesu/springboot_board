package me.kts.boardexample.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AccountService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void loginPage() throws Exception {
        // when
        ResultActions actions = mockMvc
                .perform(get("/account/login"))
                .andDo(print());
        //then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
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
                .andExpect(view().name("signUp"));
    }

    @Test
    public void login() throws Exception {

        // given
        Account account = new Account();
        account.setId("id");
        account.setPassword("password");
        given(service.login("id", "password")).willReturn(account);

        // when
        ResultActions actions = mockMvc.perform(post("/account/login")
                .param("id", "id")
                .param("password", "password"))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/list"));

        //---------------------------------------------------------------------------//

        // given
        given(service.login("id2", "password")).willReturn("id err");

        // when
        ResultActions actions2 = mockMvc.perform(post("/account/login")
                .param("id", "id2")
                .param("password", "password"))
                .andDo(print());

        // then
        actions2
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/login"));
    }

    @Test
    public void signUp() throws Exception {
        // given
        Account account = new Account();
        account.setId("kts");
        account.setPassword("pw");
        account.setName("kts");
        given(service.signUp(account))
                .willReturn("success");

        // when
        ResultActions actions = mockMvc.perform(post("/account/signUp")
                .param("id", account.getId())
                .param("password", account.getPassword())
                .param("name", account.getName()))
                .andDo(print());

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("login"));

        // given
        Account account1 = new Account();
        account1.setId("kts222");
        account1.setPassword("pw");
        account1.setName("kts");
        given(service.signUp(account))
                .willReturn("fail");

        // when
        ResultActions actions1 = mockMvc.perform(post("/account/signUp")
                .param("id", account1.getId())
                .param("password", account1.getPassword())
                .param("name", account1.getName()))
                .andDo(print());

        // then
        actions1
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/signUp"));
    }
}