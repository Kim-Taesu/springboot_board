package me.kts.boardexample.controller;

import me.kts.boardexample.common.BaseControllerTest;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.repository.BoardRepository;
import me.kts.boardexample.service.BoardService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BoardControllerTest extends BaseControllerTest {

    @Autowired
    BoardService service;

    @Autowired
    BoardRepository repository;


    @Test
    public void create_fail_by_invalid_board() throws Exception {
        // given
        Board board = Board.builder()
                .boardId("kts")
                .build();

        // when
        ResultActions actions = mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(board))
                .session(generateSession()))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/create"));
    }

    @Test
    public void create_fail_by_session_not_exist() throws Exception {
        // given
        Board board = generateSample();

        // when
        ResultActions actions = mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(board)))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/login"));
    }

    @Test
    public void create_success() throws Exception {
        // given
        BoardDto boardDto = BoardDto.builder()
                .title("test_title")
                .content("test_content")
                .build();

        // when
        ResultActions actions = mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardDto))
                .session(generateSession()))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/list"));
    }


    private Board generateSample() {
        return Board.builder()
                .title("test_title")
                .content("test_kts")
                .build();
    }

    private MockHttpSession generateSession() {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("id", "test_session");
        return mockHttpSession;
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

    @Test
    public void boardList_success() throws Exception {
        String id = "kts";
        Account account = generateAccount(id);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("id", account.getId());

        ResultActions actions = mockMvc.perform(get("/list")
                .session(session));

        // then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("list"));
    }

    @Test
    public void boardList_fail_by_session_not_exist() throws Exception {
        ResultActions actions = mockMvc.perform(get("/list"))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/login"));
    }

    private Account generateAccount(String id) {
        return Account.builder()
                .id(id)
                .password(id + "_password")
                .build();
    }
}