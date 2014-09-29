package me.aventium.avalon;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    public String name();

    public String[] aliases() default {};

    public String permission();

    public String description();

}
