/*
* vwd KL
* Created by zzhao on 6/16/16 1:44 PM
*/
package com.vwd.sbs.reactor.domain;

import java.util.List;

import com.google.common.collect.ImmutableList;
import lombok.Value;

/**
 * @author zzhao
 */
@Value
public class User {
    public static final User SKYLER = new User("swhite", "Skyler", "White");

    public static final User JESSE = new User("jpinkman", "Jesse", "Pinkman");

    public static final User WALTER = new User("wwhite", "Walter", "White");

    public static final User SAUL = new User("sgoodman", "Saul", "Goodman");

    public static final List<User> USERS = ImmutableList.of(SKYLER, JESSE, WALTER, SAUL);

    private final String username;

    private final String firstname;

    private final String lastname;

    public User upperCase() {
        return new User(this.username.toUpperCase(), this.firstname.toUpperCase(),
                this.lastname.toUpperCase());
    }
}
