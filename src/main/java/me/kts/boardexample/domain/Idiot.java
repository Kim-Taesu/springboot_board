package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Idiot implements Persistable<String> {

    @Id
    String id;

    String idiotId;

    String boardId;

    String commentId;

    @NotNull
    String title;

    @NotNull
    String content;

    @CreatedDate
    String CreatedDate;

    @CreatedBy
    String userId;

    private boolean persisted;

    @Override
    public boolean isNew() {
        return !persisted;
    }

    public void makeId(String username) {
        this.setId(username + new Date().getTime());
    }
}
