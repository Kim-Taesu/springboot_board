package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

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

    private Map<String, Comment> comments = new HashMap<>();

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private String createDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private String lastModifiedDate;

    private boolean persisted = false;

    public void makeId(String userId, String title) {
        title.replaceAll(" ", "");
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
