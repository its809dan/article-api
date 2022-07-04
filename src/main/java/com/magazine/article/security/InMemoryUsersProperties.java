package com.magazine.article.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InMemoryUsersProperties {

    public static final InMemoryUser ADMIN = new InMemoryUser("admin123",
                                                              "{bcrypt}$2a$10$L2XKxPwwNE.u8K.GEQYYN./1bPiBQx0l8cpLtVhX6vSALSvRV9aL.", // LkQH4ibu9X
                                                               new String[]{Role.ADMIN.name()});

    public static final InMemoryUser USER = new InMemoryUser("user123",
                                                             "{bcrypt}$2a$10$DBGodFa1rBrZBsCXvCiTXOP4wmQXqCNUqZ11ib416mTKzmpluKqBK", // oTEn251qS0
                                                              new String[]{Role.USER.name()});

    @AllArgsConstructor
    @Getter
    public static class InMemoryUser {

        private final String username;
        private final String password;
        private final String[] roles;
    }
}
