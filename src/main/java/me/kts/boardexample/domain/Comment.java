package me.kts.boardexample.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class Comment {

    @Id
    @NonNull
    String commentId;

    @NonNull
    String userId;

    @NonNull
    String content;

    String createdBy;

    String createDate;

    String lastModifiedBy;

    String lastModifiedDate;
}
