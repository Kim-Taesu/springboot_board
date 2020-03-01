package me.kts.boardexample.controller;

import me.kts.boardexample.common.BaseControllerTest;
import me.kts.boardexample.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BoardRestControllerTest extends BaseControllerTest {

    @Autowired
    BoardRepository boardRepository;

}