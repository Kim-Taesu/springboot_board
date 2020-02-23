package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board implements Persistable<String> {

    @Id
    private String boardId;

    @NotNull
    private String title;

    private String content;

    private List<Comment> comments = new ArrayList<>();

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private String createDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private String lastModifiedDate;

    private boolean persisted = false;

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    @Override
    public String toString() {
        return "생성 완료 (" +
                "title: " + title + ')';
    }

    public void makeId(String userId, String title) {
        this.setBoardId(userId + title);
    }

    @Override
    public String getId() {
        return this.boardId;
    }

    @Override
    public boolean isNew() {
        return !this.persisted;
    }
}
