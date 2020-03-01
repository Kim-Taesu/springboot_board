package me.kts.boardexample.common;

import org.springframework.security.test.context.support.WithMockUser;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithMockUser(value = "id", username = "id", roles = "USER")
public @interface CustomMockUser {
}
