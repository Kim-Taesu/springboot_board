package me.kts.boardexample.controller;

import me.kts.boardexample.common.BaseControllerTest;
import me.kts.boardexample.common.CustomMockUser;
import me.kts.boardexample.common.TestDescription;
import me.kts.boardexample.domain.Account;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.repository.AccountRepository;
import me.kts.boardexample.repository.BoardRepository;
import me.kts.boardexample.service.BoardService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BoardControllerTest extends BaseControllerTest {

    private final String baseHostUrl = "http://localhost";
    private final String id = "id";
    private final String password = "password";
    @Autowired
    BoardService boardService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    BoardRepository boardRepository;

    @Before
    public void setup() {
        boardRepository.deleteAll();
    }

    @Test
    @TestDescription("게시글 생성 페이지 요청")
    @CustomMockUser
    public void create_board_page_success() throws Exception {
        // When
        final ResultActions actions = mockMvc.perform(get("/board/create"))
                .andDo(print());

        // Then
        actions
                .andExpect(status().isOk())
                .andExpect(view().name("board/create"));
    }

    @Test
    @TestDescription("인증 안된 상태로 게시글 생성 페이지 요청")
    public void create_board_page_fail_by_unauthenticated() throws Exception {
        // When
        final ResultActions actions = mockMvc.perform(get("/board/create"))
                .andDo(print());

        // Then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(baseHostUrl + "/account/loginPage"));
    }

    @Test
    @CustomMockUser
    @TestDescription("게시글의 제목 또는 내용을 안채우고 요청했을 경우")
    public void create_fail_by_invalid_board() throws Exception {
        // Given
        Account account = buildAccount(password);
        accountRepository.save(account);

        // When
        ResultActions actions = mockMvc.perform(post("/board/create")
                .param("title", "test_title")
                .with(csrf())
        )
                .andDo(print());

        // Then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/create"));
    }

    @Test
    @WithAnonymousUser
    @TestDescription("인증하지 않고 게시판 생성할 때")
    public void create_fail_by_unauthenticated() throws Exception {
        // Given
        BoardDto boardDto = buildBoardDto();

        // When
        ResultActions actions = mockMvc.perform(post("/board/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardDto)))
                .andDo(print());

        // Then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/account/loginPage"));
    }

    @Test
    @CustomMockUser
    @TestDescription("게시글 생성 성공")
    public void create_success() throws Exception {
        // Given
        BoardDto boardDto = buildBoardDto();
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("title", "title update");
        data.add("content", "content update");

        // When
        ResultActions actions = mockMvc.perform(post("/board/create")
                .params(data)
                .with(csrf())
        )
                .andDo(print());

        // Then
        actions
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/list"));
    }

    private BoardDto buildBoardDto() {
        return BoardDto.builder()
                .title("title")
                .content("content")
                .build();
    }

    @Test
    @CustomMockUser
    @TestDescription("게시글 업데이트")
    public void update_success() throws Exception {
        // Given
        Board board = buildBoard();
        boardRepository.save(board);

        // When
        ResultActions actions = mockMvc.perform(post("/board/update/" + board.getBoardId())
                .param("title", "title update!")
                .param("content", "content update!")
                .with(csrf())
        )
                .andDo(print());

        // Then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/detail/" + board.getBoardId()))
                .andExpect(flash().attribute("message", "update success"));
    }

    @Test
    @CustomMockUser
    @TestDescription("게시글 업데이트, 게시글 생성자와 현재 사용자가 다른 경우")
    public void update_fail_by_owner_not_matched() throws Exception {
        // Given
        Board board = buildBoard();

        // When
        ResultActions actions = mockMvc.perform(post("/board/update/" + board.getBoardId())
                .with(user("id2").roles("USER"))
                .param("title", "title update")
                .param("content", "content update")
                .with(csrf())
        )
                .andDo(print());

        // Then
        actions
                .andExpect(authenticated())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/board/detail/" + board.getBoardId()))
                .andExpect(flash().attribute("message", "update fail"));
    }

    @Test
    @TestDescription("인증되지 않은 상태로 게시글 업데이트 하는 경우")
    public void update_fail_by_unauthenticated() throws Exception {
        // Given
        Board board = buildBoard();
        board.makeId(board.getCreatedBy());

        boardRepository.save(board);

        board.setTitle("title update!");
        board.setContent("content update!");

        // When
        MultiValueMap<String, String> dataInfo = new LinkedMultiValueMap<>();
        dataInfo.add("title", "title_update");
        dataInfo.add("content", "content_update");

        ResultActions actions = mockMvc.perform(post("/board/update/" + board.getBoardId())
                .params(dataInfo)
                .with(csrf())
        )
                .andDo(print());

        // Then
        actions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(baseHostUrl + "/account/loginPage"));
    }

    private Board buildBoard() {
        final Board build = Board.builder()
                .title("title")
                .content("content")
                .build();
        build.makeId(id);
        return build;
    }


    @Test
    @CustomMockUser
    @TestDescription("게시글 목록 불러오기")
    public void boardList_success() throws Exception {
        ResultActions actions = mockMvc.perform(get("/board/list"));

        // Then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("board/list"));
    }

    @Test
    @TestDescription("인증하지 않고 게시글 목록을 요청")
    @WithAnonymousUser
    public void boardList_fail_unauthenticated() throws Exception {
        ResultActions actions = mockMvc.perform(get("/board/list"));

        // Then
        actions
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(baseHostUrl + "/account/loginPage"));
    }

    private Account buildAccount(String password) {
        return Account.builder()
                .id(id)
                .password(password)
                .age(1)
                .name(id)
                .role("USER")
                .eMail("test@test.com")
                .build();
    }
}