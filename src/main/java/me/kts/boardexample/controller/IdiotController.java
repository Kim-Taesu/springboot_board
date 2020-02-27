package me.kts.boardexample.controller;

import me.kts.boardexample.domain.IdiotDto;
import me.kts.boardexample.repository.AccountRepository;
import me.kts.boardexample.repository.IdiotRepository;
import me.kts.boardexample.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/idiot")
public class IdiotController {

    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final IdiotRepository idiotRepository;

    public IdiotController(AccountRepository accountRepository, AccountService accountService, IdiotRepository idiotRepository) {
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.idiotRepository = idiotRepository;
    }

    @GetMapping("/list")
    public String idiotsPage(Model model) {
        model.addAttribute("list", accountRepository.findByIdiotCountGreaterThan(0));
        return "idiot/list";
    }

    @GetMapping("/detail/{idiotId}")
    public String idiotDetailPage(@PathVariable String idiotId,
                                  Model model) {
        model.addAttribute("info", idiotRepository.findByIdiotId(idiotId));
        return "idiot/detail";
    }

    @GetMapping("/{idiotId}/board/{boardId}")
    public String createIdiotPageFromBoard(Model model,
                                           @PathVariable String idiotId,
                                           @PathVariable String boardId) {
        model.addAttribute("idiotId", idiotId);
        model.addAttribute("boardId", boardId);
        model.addAttribute("commentId", "none");
        return "idiot/idiot-create";
    }

    @GetMapping("/{idiotId}/board/{boardId}/comment/{commentId}")
    public String createIdiotPageFromComment(Model model,
                                             @PathVariable String idiotId,
                                             @PathVariable String boardId,
                                             @PathVariable String commentId) {
        model.addAttribute("idiotId", idiotId);
        model.addAttribute("boardId", boardId);
        model.addAttribute("commentId", commentId);
        return "idiot/idiot-create";
    }

    @PostMapping("/{idiotId}/board/{boardId}/comment/{commentId}")
    public String addIdiot(RedirectAttributes attributes,
                           @PathVariable String idiotId,
                           @PathVariable String boardId,
                           @PathVariable String commentId,
                           @Valid IdiotDto IdiotDto) {
        if (accountService.addIdiot(idiotId, boardId, commentId, IdiotDto)) {
            attributes.addFlashAttribute("message", "신고 완료");
        } else {
            attributes.addFlashAttribute("message", "신고 오류");
        }
        return "redirect:/board/detail/" + boardId;
    }
}
