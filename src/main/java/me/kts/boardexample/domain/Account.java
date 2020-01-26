package me.kts.boardexample.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @Id
    @NonNull
    String id;

    @NonNull
    String password;

    String name;
}
