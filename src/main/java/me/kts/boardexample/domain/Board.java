package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Board {
    @Id
    @NonNull
    String id;

    @NonNull
    String title;

    String content;

    List<Comment> comments = new ArrayList<>();

    @CreatedBy
    String createdBy;

    @CreatedDate
    Date createDate;

    @LastModifiedBy
    String lastModifiedBy;

    @LastModifiedDate
    Date lastModifiedDate;

    @Override
    public String toString() {
        return "생성 완료 (" +
                "title: " + title + ')';
    }
}
