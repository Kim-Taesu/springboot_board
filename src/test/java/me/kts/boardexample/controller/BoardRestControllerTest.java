package me.kts.boardexample.controller;

import me.kts.boardexample.common.BaseControllerTest;
import me.kts.boardexample.domain.Board;
import me.kts.boardexample.repository.BoardRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

public class BoardRestControllerTest extends BaseControllerTest {

    @Autowired
    BoardRepository repository;

    @Test
    public void view_all_test() throws Exception {
        generateBoard();

        ResultActions actions = mockMvc.perform(get("/board/api/list"))
                .andDo(print());
    }

    @Test
    public void createBoard() throws Exception {

    }

    public void generateBoard() {
        for (int i = 0; i < 30; i++) {
            Board board = Board.builder()
                    .title("title_" + i)
                    .content("content_" + i)
                    .createdBy("kts")
                    .lastModifiedBy("kts")
                    .build();
            board.makeId(board.getCreatedBy());
            repository.save(board);
        }
    }


    private MockHttpSession generateSession() {
        MockHttpSession mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("id", "kts");
        return mockHttpSession;
    }
}