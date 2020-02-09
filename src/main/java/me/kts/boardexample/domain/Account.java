package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @Id
    @NotNull
    String id;

    @NotNull
    String password;

    @NotNull
    String name;

    @NotNull @Min(0)
    Integer age;

    @NotNull @Email
    String eMail;
}
