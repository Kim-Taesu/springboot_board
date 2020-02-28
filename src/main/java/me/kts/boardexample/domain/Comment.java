package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Document
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Comment implements Persistable<String> {

    @Id
    @NotNull
    private String commentId;

    @NotNull
    private String boardId;

    @NotNull
    private String content;

    @CreatedBy
    private String createdBy;

    @CreatedDate
    private String createDate;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private String lastModifiedDate;

    private boolean persisted;

    public void makeId(String userId) {
        this.commentId = userId + new Date().getTime();
    }

    @Override
    public String getId() {
        return this.commentId;
    }

    @Override
    public boolean isNew() {
        return !this.persisted;
    }
}
