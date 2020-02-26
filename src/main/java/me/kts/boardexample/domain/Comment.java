package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Comment {

    @NotNull
    private String commentId;

    @NotNull
    private String userId;

    @NotNull
    private String boardId;

    @NotNull
    private String content;

    private String createdBy;

    private String createDate;

    private String lastModifiedBy;

    private String lastModifiedDate;
}
