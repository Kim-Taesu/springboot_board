package me.kts.boardexample.controller;

import me.kts.boardexample.domain.CommentDto;
import me.kts.boardexample.service.CommentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/comment")
public class CommentController {

    private final CommentService service;

    public CommentController(CommentService service) {
        this.service = service;
    }

    @PostMapping("/create")
    public String createComment(@Valid CommentDto commentDto,
                                RedirectAttributes attributes) {
        if (service.createComment(commentDto)) {
            attributes.addFlashAttribute("message", "create comment success");
        } else {
            attributes.addFlashAttribute("message", "create comment fail");
        }
        return "redirect:/board/detail/" + commentDto.getBoardId();
    }

    @PostMapping("/update/{commentId}")
    public String updateComment(RedirectAttributes attributes,
                                @PathVariable String commentId,
                                @Valid CommentDto commentDto) {
        String message;
        if (service.updateComment(commentId, commentDto)) {
            message = "update comment success";
        } else {
            message = "update comment fail";
        }
        attributes.addFlashAttribute("message", message);
        return "redirect:/board/detail/" + commentDto.getBoardId();
    }

    @GetMapping("/delete/{commentId}/board/{boardId}")
    public String deleteComment(RedirectAttributes attributes,
                                @PathVariable String commentId,
                                @PathVariable String boardId) {
        String message;
        if (service.deleteComment(commentId)) {
            message = "delete comment success";
        } else {
            message = "delete comment fail";
        }
        attributes.addFlashAttribute("message", message);
        return "redirect:/board/detail/" + boardId;
    }
}
