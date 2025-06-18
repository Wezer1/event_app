package com.example.events_app.model;


//import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum Role {
    USER(Set.of(Permission.USERS_READ)),
    ADMIN(Set.of(Permission.USERS_WRITE, Permission.USERS_READ));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    //с помощью SimpleGrantedAuthority security может определить кто и к чему может иметь доступ
    public Set<SimpleGrantedAuthority> getAuthorities(){
        return getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());//производим наш permission в SimpleGrantedAuthority
    }
}
