package me.kts.boardexample.service;

import me.kts.boardexample.domain.*;
import me.kts.boardexample.repository.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IdiotService {

    private final IdiotRepository idiotRepository;
    private final IdiotCustomRepository idiotCustomRepository;
    private final BoardRepository boardRepository;
    private final AccountRepository accountRepository;
    private final CommentRepository commentRepository;

    public IdiotService(IdiotRepository idiotRepository, IdiotCustomRepository idiotCustomRepository, BoardRepository boardRepository, AccountRepository accountRepository, CommentRepository commentRepository) {
        this.idiotRepository = idiotRepository;
        this.idiotCustomRepository = idiotCustomRepository;
        this.boardRepository = boardRepository;
        this.accountRepository = accountRepository;
        this.commentRepository = commentRepository;
    }

    public boolean addIdiot(String idiotId, String type, String typeId, IdiotDto idiotDto) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        if (type.equals("board")) {
            List<Board> isExist = idiotCustomRepository.findExistBoard(username, type, typeId, idiotId);
            if (!isExist.isEmpty())
                return false;
        } else {
            List<Comment> isExist = idiotCustomRepository.findExistComment(username, type, typeId, idiotId);
            if (!isExist.isEmpty())
                return false;
        }

        Idiot idiot = Idiot.builder()
                .idiotId(idiotId)
                .title(idiotDto.getTitle())
                .content(idiotDto.getContent())
                .idiotType(type)
                .build();

        if (type.equals("board")) {
            Board board = boardRepository.findById(typeId).orElse(null);
            if (board == null) {
                return false;
            }
            board.setIdiotCount(board.getIdiotCount() + 1);
            board.setPersisted(true);
            boardRepository.save(board);
            idiot.setIdiotDetail(board);
        } else {
            Comment comment = commentRepository.findById(typeId).orElse(null);
            if (comment == null) {
                return false;
            }
            idiot.setIdiotDetail(comment);
        }
        idiot.makeId(username);
        idiotRepository.save(idiot);

        Account account = accountRepository.findById(idiotId).orElse(null);
        if (account == null) {
            return false;
        }
        account.setIdiotCount(account.getIdiotCount() + 1);
        account.setPersisted(true);
        accountRepository.save(account);
        return true;
    }

    public List<Account> getIdiots() {
        return accountRepository.findByIdiotCountGreaterThan(0);
    }

    public String getBoardId(String typeId) {
        return commentRepository.findById(typeId).orElse(null).getBoardId();
    }
}
