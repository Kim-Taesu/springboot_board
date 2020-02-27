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
@AllArgsConstructor
@Builder
public class AccountVisit {

    @NotNull
    private String userId;

    private String loginTime;

    private String useTime;

    private String logoutTime;
}
