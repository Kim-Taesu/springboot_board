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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BoardControllerTest extends BaseControllerTest {

    @Autowired
    BoardService service;

    @Autowired
    BoardRepository repository;
    @Autowired
    LocalValidatorFactoryBean localValidatorFactoryBean;

    @Before
    public void setup() {
        repository.deleteAll();
    }

    @Test
    public void create_fail_by_invalid_board() throws Exception {
        // when
        ResultActions actions = mockMvc.perform(post("/board/create")
                .param("title", "test_title")
                .session(generateSession()))
                .andDo(print());

        // then
        actions
                .andExpect(status().isBadRequest());
    }

    @Test
    public void create_fail_by_session_not_exist() throws Exception {
        // given
        BoardDto boardDto = generateBoardDto("title", "content");

        // when
        ResultActions actions = mockMvc.perform(post("/board/create")
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
        // when
        ResultActions actions = mockMvc.perform(post("/board/create")
                .param("title", "title")
                .param("content", "content")
                .session(generateSession()))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
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
        board.makeId(board.getCreatedBy());

        repository.save(board);

        // when
        ResultActions actions = mockMvc.perform(post("/board/update/" + board.getBoardId())
                .session(mockHttpSession)
                .param("title", "title update!")
                .param("content", "content update!"))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/detail/" + board.getBoardId()))
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
        board.makeId(board.getCreatedBy());

        repository.save(board);

        board.setTitle("title update!");
        board.setContent("content update!");

        // when
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("title", "title update");
        data.add("content", "content update");

        ResultActions actions = mockMvc.perform(post("/board/update/" + board.getBoardId())
                .session(mockHttpSession)
                .params(data))
                .andDo(print());

        // then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/detail/" + board.getBoardId()))
                .andExpect(flash().attribute("message", "update fail"));
    }

    @Test
    public void create_board_check_date() throws Exception {
        Board board = Board.builder()
                .title("title")
                .content("content")
                .createdBy("kts")
                .lastModifiedBy("kts")
                .build();

        repository.save(board);

        repository.findAll().forEach(b -> {
            System.out.println(b.getCreateDate());
            System.out.println(b.getLastModifiedDate());
        });
        System.out.println("---------------------------");

        Board newBoard = repository.findById(board.getBoardId()).orElse(null);
        if (newBoard != null) {
            newBoard.setTitle("title1");
            repository.save(newBoard);
        }
        repository.findAll().forEach(b -> {
            System.out.println(b.getCreateDate());
            System.out.println(b.getLastModifiedDate());
        });

    }

    @Test
    public void update_fail_by_session_not_exist() throws Exception {
        // given
        Board board = Board.builder()
                .title("title")
                .content("content")
                .createdBy("kts")
                .build();
        board.makeId(board.getCreatedBy());

        repository.save(board);

        board.setTitle("title update!");
        board.setContent("content update!");

        // when
        MultiValueMap<String, String> dataInfo = new LinkedMultiValueMap<>();
        dataInfo.add("title", "title_update");
        dataInfo.add("content", "content_update");

        ResultActions actions = mockMvc.perform(post("/board/update/" + board.getBoardId())
                .params(dataInfo))
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

        ResultActions actions = mockMvc.perform(get("/board/list")
                .session(session));

        // then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("board/list"));
    }

    @Test
    public void boardList_fail_by_session_not_exist() throws Exception {
        ResultActions actions = mockMvc.perform(get("/board/list"))
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