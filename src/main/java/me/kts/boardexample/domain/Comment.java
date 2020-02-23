package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Comment {

    @Id
    @NotNull
    String commentId;

    @NotNull
    String userId;

    @NotNull
    String boardId;

    @NotNull
    String content;

    String createdBy;

    String createDate;

    String lastModifiedBy;

    String lastModifiedDate;
}
