/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.services;

import com.moklyak.Game.server.JDBCTemplates.UserJDBCTemplate;
import com.moklyak.Game.server.configurations.security.JWTUser;
import com.moklyak.Game.server.configurations.security.JWTUserFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Пользователь
 */
@Service
@Slf4j
public class JWTUserDetailsService implements UserDetailsService {

    private final UserJDBCTemplate userService;

    @Autowired
    public JWTUserDetailsService(UserJDBCTemplate userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.moklyak.Game.server.entities.User user = userService.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User with username: " + username + " not found");
        }
        
        JWTUser jwtUser = JWTUserFactory.create(user);
        log.info("IN loadByUserName - user with username: {} succesfully loaded", username);
        return jwtUser;
    }

}
