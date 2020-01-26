package me.kts.boardexample.domain;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class Comment {

    @Id
    String id;

    @NonNull
    String userName;

    @NonNull
    String detail;
}
