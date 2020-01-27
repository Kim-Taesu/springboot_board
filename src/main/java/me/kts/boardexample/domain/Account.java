package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @NonNull
    String id;

    @NotNull
    String password;

    @NotNull
    String name;

    @NotNull @Min(0)
    Integer age;
}
