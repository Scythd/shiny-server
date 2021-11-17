/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.controllers;

import com.moklyak.Game.server.JDBCTemplates.RoleJDBCTemplate;
import com.moklyak.Game.server.JDBCTemplates.UserJDBCTemplate;
import com.moklyak.Game.server.configurations.security.JwtTokenProvider;
import com.moklyak.Game.server.configurations.security.Status;
import com.moklyak.Game.server.entities.Role;
import com.moklyak.Game.server.entities.User;
import com.moklyak.Game.server.models.AuthenticationRequestDto;
import com.moklyak.Game.server.models.RegistrationRequestDto;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Пользователь
 */
@RestController
@RequestMapping(value = "/api/auth")
public class RegistrationContoller {

    private final AuthenticationManager authenticationManager;

    private final JwtTokenProvider jwtTokenProvider;

    private final UserJDBCTemplate userJDBCTemplate;
    
    private final RoleJDBCTemplate roleJDBCTemplate;

    public RegistrationContoller(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserJDBCTemplate userJDBCTemplate, RoleJDBCTemplate roleJDBCTemplate) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userJDBCTemplate = userJDBCTemplate;
        this.roleJDBCTemplate = roleJDBCTemplate;
    }

    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody RegistrationRequestDto requestDto) {
        User user1 = userJDBCTemplate.findByUsername(requestDto.getUsername());
        if (user1 != null){
            Map<Object, Object> response = new HashMap<>();
            response.put("error","user already exist");
            return ResponseEntity.ok(response);
        }
            
        try {
            String username = requestDto.getUsername();
            User user = new User();
            user.setStatus(Status.ACTIVE);
            user.setRoles(List.of(roleJDBCTemplate.findByName("ROLE_USER")));
            user.setUsername(requestDto.getUsername());
            user.setNickname(requestDto.getNickname());
            user.setEmail(requestDto.getEmail());
            user.setPassword(jwtTokenProvider.passwordEncoder().encode(requestDto.getPassword()));
            user = userJDBCTemplate.save(user);
            //authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
           
            String token = jwtTokenProvider.createToken(username, user.getRoles());
            
            Map<Object, Object> response = new HashMap<>();
            
            response.put("username", username);
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch(AuthenticationException e) {
            throw new BadCredentialsException("Invalid username or password");
        }
    }
}
