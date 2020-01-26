package me.kts.boardexample.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.service.BoardService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(BoardController.class)
public class BoardControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BoardService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void index() throws Exception {
        // given
        given(service.viewAll()).willReturn(new ArrayList<>());

        // when
        ResultActions actions = mockMvc.perform(get("/list"))
                .andDo(print());

        // then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    public void create() throws Exception {
        // given
        Board board = new Board();
        board.setId("id");
        board.setTitle("title");
        board.setCreatedBy("kts");
        board.setCreateDate(new Date());
        given(service.create("id", "title", "kts")).willReturn(board.toString());

        // when
        ResultActions actions = mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(board)))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("message", board.toString()));
    }

    @Test
    public void update() throws Exception {
//        // given
//        Board board = new Board();
//        board.setTitle("title");
//        board.setId("id");
//        board.setContent("content");
//        board.setCreatedBy("kts");
//        given(service.update("id", "kts", "content")).willReturn(board.toString());
//
//        // when
//        ResultActions actions = mockMvc.perform(put("/update/id")
//                .param("createdBy", "kts")
//                .param("content", "content11"))
//                .andDo(print());
//
//        // then
//        actions
//                .andExpect(status().is3xxRedirection())
//                .andExpect(flash().attribute("message", board.toString()));
    }
}