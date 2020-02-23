package me.kts.boardexample.controller;

import me.kts.boardexample.common.BaseControllerTest;
import org.junit.Test;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class IndexControllerTest extends BaseControllerTest {

    @Test
    public void index() throws Exception {
        ResultActions actions = mockMvc.perform(get("/"))
                .andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

}