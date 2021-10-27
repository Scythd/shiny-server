/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.configurations.security;

import com.moklyak.Game.server.entities.Role;
import com.moklyak.Game.server.entities.User;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 *
 * @author Пользователь
 */
public final class JWTUserFactory {

    public JWTUserFactory() {
    }
    
    public static JWTUser create(User user){
        return new JWTUser(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword(),
                user.getNickname(),
                user.getStatus().equals(Status.ACTIVE),
                mapToGrantedAuthority(user.getRoles())
        );
    }
    
    private static List<GrantedAuthority> mapToGrantedAuthority(List<Role> userRoles){
        return userRoles.stream()
                .map((Function<? super Role, ? extends SimpleGrantedAuthority>) (role) -> 
                {
                    return new SimpleGrantedAuthority(role.getName());
                }).collect(Collectors.toList());
    }
}
