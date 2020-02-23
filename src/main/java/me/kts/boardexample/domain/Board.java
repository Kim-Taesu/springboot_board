package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board {

    @Id
    private String boardId;

    @NotNull
    private String title;

    @NotNull
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
}
