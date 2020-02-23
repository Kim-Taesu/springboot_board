package me.kts.boardexample.controller;

import me.kts.boardexample.common.BaseControllerTest;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.repository.BoardRepository;
import me.kts.boardexample.service.BoardService;
import org.junit.Before;
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

    @Before
    public void setup(){
        repository.deleteAll();
    }

    @Test
    public void create_fail_by_invalid_board() throws Exception {
        // given
        BoardDto boardDto = BoardDto.builder()
                .title("title")
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

    @Test
    public void create_fail_by_session_not_exist() throws Exception {
        // given
        BoardDto boardDto = generateBoardDto("title", "content");

        // when
        ResultActions actions = mockMvc.perform(post("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardDto)))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/login"));
    }

    @Test
    public void create_success() throws Exception {
        // given
        BoardDto boardDto = generateBoardDto("title", "content");

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

    private BoardDto generateBoardDto(String title, String content) {
        return BoardDto.builder()
                .title(title)
                .content(content)
                .build();
    }


    private Board generateSample() {
        return Board.builder()
                .title("test_title")
                .content("test_kts")
                .build();
    }

    private MockHttpSession generateSession() {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("id", "kts");
        return mockHttpSession;
    }

    @Test
    public void update_success() throws Exception {
        MockHttpSession mockHttpSession = generateSession();
        String user = (String) mockHttpSession.getAttribute("id");

        // given
        Board board = Board.builder()
                .title("title")
                .content("content")
                .createdBy(user)
                .build();
        board.makeId(board.getCreatedBy(), board.getTitle());

        repository.save(board);

        board.setTitle("title update!");
        board.setContent("content update!");

        // when
        ResultActions actions = mockMvc.perform(post("/update/" + board.getBoardId())
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(board)))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/detail/" + board.getBoardId()))
                .andExpect(flash().attribute("message", "update success"));
    }

    @Test
    public void update_fail_by_owner_not_matched() throws Exception {
        MockHttpSession mockHttpSession = generateSession();
        String user = (String) mockHttpSession.getAttribute("id");

        // given
        Board board = Board.builder()
                .title("title")
                .content("content")
                .createdBy(user + "dump")
                .build();
        board.makeId(board.getCreatedBy(), board.getTitle());

        repository.save(board);

        board.setTitle("title update!");
        board.setContent("content update!");

        // when
        ResultActions actions = mockMvc.perform(post("/update/" + board.getBoardId())
                .session(mockHttpSession)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(board)))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/detail/" + board.getBoardId()))
                .andExpect(flash().attribute("message", "update fail"));
    }

    @Test
    public void update_fail_by_session_not_exist() throws Exception {
        // given
        Board board = Board.builder()
                .title("title")
                .content("content")
                .createdBy("kts")
                .build();
        board.makeId(board.getCreatedBy(), board.getTitle());

        repository.save(board);

        board.setTitle("title update!");
        board.setContent("content update!");

        // when
        ResultActions actions = mockMvc.perform(post("/update/" + board.getBoardId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(board)))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/login"));
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