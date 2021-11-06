/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.moklyak.Game.server.controllers;

import com.moklyak.Game.server.JDBCTemplates.UserJDBCTemplate;
import com.moklyak.Game.server.entities.User;
import com.moklyak.Game.server.models.UserDto;
import java.io.Serializable;
import java.net.http.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Пользователь
 */
@RestController
@RequestMapping(value = "/api/admin")
public class AdminController {

    private final UserJDBCTemplate userService;

    //private final UserDetailsService userDetailsService;
    @Autowired
    public AdminController(UserJDBCTemplate userService/*, UserDetailsService userDetailsService*/) {
        this.userService = userService;
        //this.userDetailsService = userDetailsService;
    }

    @GetMapping(value = "/user/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") Long id, @RequestHeader("Authorization") String token) {
        User user = userService.findById(id);

        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        UserDto result = UserDto.fromUser(user);
        result.setToken(token);

        ResponseEntity re = new ResponseEntity<>(result, HttpStatus.OK);
        //SecurityContext ctx = SecurityContextHolder.getContext();

        //String out = ctx.getAuthentication().getCredentials().toString();
        //UserDetails userDetails = this.userDetailsService.loadUserByUsername(ctx.getAuthentication().getName());
        //Authentication newOne = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        //out = newOne.getCredentials().toString();
        //ctx.setAuthentication(newOne);
        return re;
    }
}
