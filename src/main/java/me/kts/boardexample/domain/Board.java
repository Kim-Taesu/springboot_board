package me.kts.boardexample.domain;

import lombok.*;
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
    @Id @NonNull
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
}
