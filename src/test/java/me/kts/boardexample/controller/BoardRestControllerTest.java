package me.kts.boardexample.controller;

import me.kts.boardexample.common.BaseControllerTest;
import me.kts.boardexample.common.CustomMockUser;
import me.kts.boardexample.common.TestDescription;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.domain.BoardDto;
import me.kts.boardexample.repository.BoardRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class BoardRestControllerTest extends BaseControllerTest {

    @Autowired
    BoardRepository boardRepository;

    @Before
    public void init() {
        boardRepository.deleteAll();
    }

    @Test
    @CustomMockUser
    @TestDescription("게시판 생성")
    public void createBoard_success() throws Exception {
        BoardDto boardDto = buildBoardDto();

        ResultActions actions = mockMvc.perform(post("/board/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardDto))
                .accept(MediaTypes.HAL_JSON)
                .with(csrf())
        )
                .andDo(print());

        actions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE + ";charset=UTF-8"))

                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.board-list").exists())
        ;
    }

    @Test
    @CustomMockUser
    @TestDescription("게시판 생성, 제목 또는 내용 없음")
    public void createBoard_fail_by_invalid_boardDto() throws Exception {
        BoardDto boardDto = BoardDto.builder()
                .title("df")
                .build();

        ResultActions actions = mockMvc.perform(post("/board/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardDto))
                .accept(MediaTypes.HAL_JSON)
                .with(csrf())
        )
                .andDo(print());

        actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].field").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
        ;
    }

    @Test
    @CustomMockUser
    @TestDescription("게시판 생성, 제목 또는 내용이 빈칸일 경우")
    public void createBoard_fail_by_invalid_boardDto2() throws Exception {
        BoardDto boardDto = BoardDto.builder()
                .title("title")
                .content("          ")
                .build();

        ResultActions actions = mockMvc.perform(post("/board/api/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(boardDto))
                .accept(MediaTypes.HAL_JSON)
                .with(csrf())
        )
                .andDo(print());

        actions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].field").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].rejectedValue").exists())
        ;
    }

    private BoardDto buildBoardDto() {
        return BoardDto.builder()
                .title("title")
                .content("content")
                .build();
    }

    @Test
    @TestDescription("30개의 게시판을 5개씩 생성하고 2번째 페이지 조회하기")
    @CustomMockUser
    public void boardList() throws Exception {
        generateBoard(100);

        ResultActions actions = mockMvc.perform(get("/board/api/list")
                .param("size", "5")
                .param("page", "2")
                .with(csrf())
        )
                .andDo(print());

        actions
                .andExpect(status().isOk());
    }

    private void generateBoard(int maxBoardNum) throws InterruptedException {
        final String username = getUserName();
        for (int i = 1; i <= maxBoardNum; i++) {
            Board board = Board.builder()
                    .title("title " + i)
                    .content("content " + i)
                    .build();
            Thread.sleep(1);
            board.makeId(username);
            boardRepository.save(board);
        }
    }

    @Test
    @TestDescription("게시글 수정 요청")
    @CustomMockUser
    public void updateBoard() throws Exception {
        // Given
        String username = getUserName();
        Board board = Board.builder()
                .title("title")
                .content("content")
                .build();
        board.makeId(username);
        boardRepository.save(board);

        BoardDto newBoardDto = BoardDto.builder()
                .title("new title")
                .content("new content")
                .build();

        // When
        ResultActions actions = mockMvc.perform(put("/board/api/update/" + board.getBoardId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBoardDto))
                .with(csrf())
        )
                .andDo(print());

        actions
                .andExpect(status().isOk());
    }

    @Test
    @TestDescription("게시글 수정 요청, 게시글 작성자와 수정하려는 사용자가 다를 때")
    @CustomMockUser
    public void updateBoard_fail_by_not_matched_user() throws Exception {
        // Given
        String username = getUserName();
        Board board = Board.builder()
                .title("title")
                .content("content")
                .build();
        board.makeId(username);
        boardRepository.save(board);

        BoardDto newBoardDto = BoardDto.builder()
                .title("new title")
                .content("new content")
                .build();

        // When
        ResultActions actions = mockMvc.perform(put("/board/api/update/" + board.getBoardId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newBoardDto))
                .with(csrf())
                .with(user("id2").roles("USER"))
        )
                .andDo(print());

        actions
                .andExpect(status().isBadRequest());
    }

    private String getUserName() {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }
}