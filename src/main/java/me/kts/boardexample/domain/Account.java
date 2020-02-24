package me.kts.boardexample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account implements Persistable<String> {
    @Id
    @NotNull
    String id;

    @NotNull
    String password;

    @NotNull
    String name;

    @NotNull @Min(0)
    Integer age;

    @Email
    String eMail;

    @CreatedDate
    String createdDate;

    private boolean persisted;

    private String role;

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(password);
    }


    @Override
    public boolean isNew() {
        return !this.persisted;
    }
}
