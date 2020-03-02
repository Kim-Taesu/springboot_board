package me.kts.boardexample.aspect;

import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Document
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface ServiceLogging {
}
