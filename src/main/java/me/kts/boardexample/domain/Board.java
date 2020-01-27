package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    @Id
    private String boardId;

    @NotNull
    private String title;

    @NotNull
    private String content;

    private List<Comment> comments = new ArrayList<>();

    private String createdBy;

    private String createDate;

    private String lastModifiedBy;

    private String lastModifiedDate;

    @Override
    public String toString() {
        return "생성 완료 (" +
                "title: " + title + ')';
    }
}
