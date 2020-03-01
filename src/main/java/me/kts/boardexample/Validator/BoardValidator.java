package me.kts.boardexample.Validator;

import me.kts.boardexample.domain.Board;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class BoardValidator {

    public void validate(Board board, Errors errors) {
        if (board.getTitle().isBlank()) {
            errors.rejectValue("title", "wrongValue", "title value is wrong.");
        }
        if (board.getContent().isBlank()) {
            errors.rejectValue("content", "wrongValue", "title value is wrong.");
        }
    }
}
